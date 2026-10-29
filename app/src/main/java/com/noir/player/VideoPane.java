package com.noir.player;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.os.*;
import android.view.*;
import android.widget.*;
import java.util.Locale;

/** Shared inline/full-screen surface and tap-to-show video controls. */
public class VideoPane extends FrameLayout {
 final TextureView texture;final ImageView poster;final FrameLayout controls;final ImageButton play,previous,next,rewind,forward,mode,fit,rotate,expand,back,sketch,lock,unlock;final View audioCover;final FrameLayout lockedOverlay;final TextView errorLabel;final TextView heading,elapsed,total;final SeekBar seek;final ProgressBar loading;
 final Handler handler=new Handler(Looper.getMainLooper());final boolean fullscreen;final Object owner=new Object();boolean active,videoRequested,dragging,controlsVisible=true,lastPlaying,touchLocked;float downX,downY,aspect=16/9f;Bitmap previousArt;Runnable onExpand,onRotate,onBack,onPoster,onModeChanged;
 int sizingMode;float zoom=1,panX,panY,lastX,lastY;boolean sizingGesture,multiTouch,moved,touchAdjusting;VideoSizingSheet sizingSheet;TextView sizingStatus;
 final Runnable clearSizingStatus=()->{if(sizingStatus!=null)sizingStatus.setVisibility(GONE);};
 final ScaleGestureDetector scaleDetector;final VideoGestures gestures;
 final Runnable hide=()->{PlaybackService s=PlaybackService.current;if(!dragging&&!sizingGesture&&!touchAdjusting&&sizingSheet==null&&videoRequested&&s!=null&&s.playing())setControlsVisible(false);};
 final Runnable refresh=new Runnable(){public void run(){if(getWindowVisibility()==VISIBLE&&isShown())render();handler.postDelayed(this,250);}};
 public VideoPane(Context context){this(context,false);}
 public VideoPane(Context context,boolean fullscreen){super(context);this.fullscreen=fullscreen;readSizing();
  scaleDetector=new ScaleGestureDetector(context,new ScaleGestureDetector.SimpleOnScaleGestureListener(){
   @Override public boolean onScaleBegin(ScaleGestureDetector detector){sizingGesture=true;handler.removeCallbacks(hide);return videoRequested;}
   @Override public boolean onScale(ScaleGestureDetector detector){
    float oldZoom=zoom;zoom=VideoGeometry.clampZoom(zoom*detector.getScaleFactor());float ratio=zoom/oldZoom;
    if(getWidth()>0&&getHeight()>0){float fx=detector.getFocusX()/getWidth()-.5f,fy=detector.getFocusY()/getHeight()-.5f;panX=fx+(panX-fx)*ratio;panY=fy+(panY-fy)*ratio;}
    applySizing();showSizingStatus();return true;
   }
  });scaleDetector.setQuickScaleEnabled(false);gestures=new VideoGestures(this);setBackgroundColor(Color.BLACK);setFocusable(true);setContentDescription("Media artwork. Swipe right for full-screen video.");
  texture=new TextureView(context);addView(texture,new LayoutParams(-1,-1,Gravity.CENTER));audioCover=new View(context);audioCover.setBackgroundColor(Color.BLACK);addView(audioCover,new LayoutParams(-1,-1));poster=new ImageView(context);poster.setScaleType(ImageView.ScaleType.FIT_CENTER);poster.setContentDescription("Track thumbnail");addView(poster,new LayoutParams(-1,-1,Gravity.CENTER));
  loading=new ProgressBar(context);LayoutParams lp=new LayoutParams(dp(32),dp(32),Gravity.CENTER);addView(loading,lp);loading.setVisibility(GONE);
  controls=new FrameLayout(context);addView(controls,new LayoutParams(-1,-1));if(fullscreen)SafeArea.install(controls);
  LinearLayout top=new LinearLayout(context);top.setGravity(Gravity.CENTER_VERTICAL);top.setPadding(dp(4),dp(4),dp(4),dp(8));top.setBackground(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,new int[]{0xcc000000,0x00000000}));back=icon(PlayerIcons.Icon.BACK,"Back to player");back.setOnClickListener(v->{if(onBack!=null)onBack.run();});top.addView(back,new LinearLayout.LayoutParams(dp(48),dp(48)));back.setVisibility(fullscreen?VISIBLE:GONE);heading=label("Video",14);heading.setOnClickListener(v->showPlaybackDetails());heading.setContentDescription("Playback details");heading.setSingleLine(true);heading.setEllipsize(android.text.TextUtils.TruncateAt.END);top.addView(heading,new LinearLayout.LayoutParams(0,dp(48),1));mode=icon(PlayerIcons.Icon.AUDIO,"Switch to audio only");mode.setOnClickListener(v->{setVideoRequested(!videoRequested,true);if(onModeChanged!=null)onModeChanged.run();showControls();});top.addView(mode,new LinearLayout.LayoutParams(dp(48),dp(48)));sketch=icon(PlayerIcons.Icon.SKETCH,"Enable sketch video");sketch.setOnClickListener(v->{PlaybackService service=service();if(service!=null){service.setSketchEnabled(!service.sketchEnabled());showVideoFeedback(getResources().getString(service.sketchEnabled()?R.string.video_sketch_on:R.string.video_sketch_off));render();showControls();}});top.addView(sketch,new LinearLayout.LayoutParams(dp(48),dp(48)));fit=icon(PlayerIcons.Icon.RESIZE_FIT,"Video size: Fit");fit.setOnClickListener(v->{if(videoRequested){setSizingMode((sizingMode+1)%3);showVideoFeedback(new String[]{"Fit screen","Crop","Stretch"}[sizingMode]);showControls();}});fit.setOnLongClickListener(v->{showSizing();return true;});rotate=icon(PlayerIcons.Icon.ROTATE,"Rotate video");rotate.setOnClickListener(v->{if(onRotate!=null)onRotate.run();showControls();});top.addView(rotate,new LinearLayout.LayoutParams(dp(48),dp(48)));expand=icon(PlayerIcons.Icon.EXPAND,fullscreen?"Exit full screen":"Open full-screen video");expand.setOnClickListener(v->{if(onExpand!=null)onExpand.run();});top.addView(expand,new LinearLayout.LayoutParams(dp(48),dp(48)));controls.addView(top,new LayoutParams(-1,dp(64),Gravity.TOP));
  LinearLayout center=new LinearLayout(context);center.setGravity(Gravity.CENTER);center.setPadding(0,0,0,0);previous=icon(PlayerIcons.Icon.PREVIOUS,"Previous track");rewind=icon(PlayerIcons.Icon.BACK_10,"Back 10 seconds");play=icon(PlayerIcons.Icon.PLAY,"Play");forward=icon(PlayerIcons.Icon.FORWARD_10,"Forward 10 seconds");next=icon(PlayerIcons.Icon.NEXT,"Next track");ImageButton[] buttons={rewind,previous,play,next,forward};for(ImageButton b:buttons){LinearLayout.LayoutParams bp=new LinearLayout.LayoutParams(0,dp(48),1);b.setBackgroundColor(Color.TRANSPARENT);center.addView(b,bp);}previous.setOnClickListener(v->{if(service()!=null)service().previousTrack();render();showControls();});rewind.setOnClickListener(v->{if(service()!=null)service().skip(-10000);render();showControls();});play.setOnClickListener(v->{if(service()!=null)service().toggle();render();showControls();});forward.setOnClickListener(v->{if(service()!=null)service().skip(10000);render();showControls();});next.setOnClickListener(v->{if(service()!=null)service().nextTrack();render();showControls();});
  LinearLayout bottom=new LinearLayout(context);bottom.setOrientation(LinearLayout.VERTICAL);bottom.setPadding(dp(12),dp(16),dp(12),dp(8));bottom.setBackground(new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,new int[]{0xcc000000,0x00000000}));seek=new SeekBar(context);seek.setMax(1000);seek.setContentDescription("Video playback position");seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){public void onStartTrackingTouch(SeekBar b){dragging=true;handler.removeCallbacks(hide);}public void onProgressChanged(SeekBar b,int value,boolean user){if(user&&service()!=null)elapsed.setText(time(service().duration()*value/1000L));}public void onStopTrackingTouch(SeekBar b){if(service()!=null)service().seek((int)(service().duration()*b.getProgress()/1000L));dragging=false;showControls();}});bottom.addView(seek,new LinearLayout.LayoutParams(-1,dp(40)));LinearLayout timeRow=new LinearLayout(context);timeRow.setPadding(dp(8),0,dp(8),0);elapsed=label("0:00",12);total=label("0:00",12);total.setGravity(Gravity.END|Gravity.CENTER_VERTICAL);timeRow.addView(elapsed,new LinearLayout.LayoutParams(0,dp(24),1));timeRow.addView(total,new LinearLayout.LayoutParams(0,dp(24),1));bottom.addView(timeRow);
  lock=icon(PlayerIcons.Icon.LOCK,"Lock video controls");lock.setBackgroundColor(Color.TRANSPARENT);lock.setOnClickListener(v->setTouchLocked(true));fit.setBackgroundColor(Color.TRANSPARENT);
  bottom.addView(new VideoTransportBar(context,center,lock,fit),new LinearLayout.LayoutParams(-1,-2));
  controls.addView(bottom,new LayoutParams(-1,-2,Gravity.BOTTOM));
  sizingStatus=label("100%",14);sizingStatus.setGravity(Gravity.CENTER);sizingStatus.setPadding(dp(12),0,dp(12),0);GradientDrawable statusBg=new GradientDrawable();statusBg.setColor(0xaa000000);statusBg.setCornerRadius(dp(16));sizingStatus.setBackground(statusBg);LayoutParams statusLp=new LayoutParams(-2,dp(36),Gravity.TOP|Gravity.CENTER_HORIZONTAL);statusLp.topMargin=dp(76);addView(sizingStatus,statusLp);sizingStatus.setVisibility(GONE);
  errorLabel=label("",13);errorLabel.setGravity(Gravity.CENTER);errorLabel.setPadding(dp(20),dp(12),dp(20),dp(12));errorLabel.setBackgroundColor(0xdd000000);errorLabel.setOnClickListener(v->showPlaybackDetails());LayoutParams errorParams=new LayoutParams(-1,-2,Gravity.CENTER);addView(errorLabel,errorParams);errorLabel.setVisibility(GONE);
  lockedOverlay=new FrameLayout(context);lockedOverlay.setClickable(true);if(fullscreen)SafeArea.install(lockedOverlay);addView(lockedOverlay,new LayoutParams(-1,-1));LinearLayout unlockRow=new LinearLayout(context);unlockRow.setGravity(Gravity.CENTER_VERTICAL);unlock=icon(PlayerIcons.Icon.LOCK,"Hold to unlock video controls");unlock.setOnLongClickListener(v->{setTouchLocked(false);return true;});unlock.setOnClickListener(v->unlock.announceForAccessibility(getResources().getString(R.string.video_hold_unlock)));unlockRow.addView(unlock,new LinearLayout.LayoutParams(dp(48),dp(48)));TextView unlockText=label(getResources().getString(R.string.video_hold_unlock),12);unlockRow.addView(unlockText);LayoutParams unlockParams=new LayoutParams(-2,dp(48),Gravity.START|Gravity.BOTTOM);unlockParams.setMargins(dp(12),0,0,dp(16));lockedOverlay.addView(unlockRow,unlockParams);lockedOverlay.setVisibility(GONE);
  setControlsVisible(fullscreen);
 }
 PlaybackService service(){return PlaybackService.current;}
 int dp(int n){return Math.round(n*getResources().getDisplayMetrics().density);}
 TextView label(String text,int size){TextView v=new TextView(getContext());v.setText(text);v.setTextColor(Color.WHITE);v.setTextSize(size);v.setGravity(Gravity.CENTER_VERTICAL);return v;}
 ImageButton icon(PlayerIcons.Icon type,String description){ImageButton b=new ImageButton(getContext());b.setScaleType(ImageView.ScaleType.FIT_CENTER);b.setImageDrawable(new PlayerIcons(type,Color.WHITE));b.setPadding(dp(12),dp(12),dp(12),dp(12));GradientDrawable bg=new GradientDrawable();bg.setShape(GradientDrawable.OVAL);bg.setColor(0x55000000);b.setBackground(bg);b.setContentDescription(description);return b;}
 void setVideoRequested(boolean value,boolean persist){if(persist)getContext().getSharedPreferences("noir",0).edit().putBoolean("video_mode",value).apply();if(videoRequested==value){render();return;}videoRequested=value;if(!value)gestures.stop();if(value||fullscreen)showControls();else setControlsVisible(false);render();requestLayout();}
 void setHostActive(boolean value){active=value;if(!value){gestures.stop();handler.removeCallbacks(hide);setKeepScreenOn(false);detach();}else{render();if(videoRequested||fullscreen)showControls();}}
 void attach(){PlaybackService s=service();if(active&&s!=null&&s.hasVideo){if(videoRequested)s.attachVideoTextureView(owner,texture);else s.keepVideoWarm(owner,texture);}}
 void detach(){PlaybackService s=service();if(s!=null){boolean owned=s.releaseVideoSurface(owner);if(owned||!s.hasVideoSurface())s.setAudioOnly(true);}poster.setVisibility(VISIBLE);}
 void showControls(){if(touchLocked)return;setControlsVisible(true);handler.removeCallbacks(hide);PlaybackService s=service();if(active&&videoRequested&&s!=null&&s.playing())handler.postDelayed(hide,3500);}
 void setControlsVisible(boolean visible){controlsVisible=visible&&!touchLocked;controls.setVisibility(controlsVisible?VISIBLE:INVISIBLE);}
 void render(){if(!sizingGesture&&!touchAdjusting)readSizing();PlaybackService s=service();if(active)attach();boolean playing=s!=null&&s.playing();if(playing!=lastPlaying){lastPlaying=playing;if(videoRequested||fullscreen)showControls();}heading.setText(s==null?"Choose a video":s.title);mode.setImageDrawable(new PlayerIcons(videoRequested?PlayerIcons.Icon.AUDIO:PlayerIcons.Icon.VIDEO,Color.WHITE));mode.setContentDescription(videoRequested?"Switch to audio only":"Switch to video");mode.setEnabled(s!=null&&s.hasVideo);mode.setAlpha(mode.isEnabled()?1f:.4f);play.setImageDrawable(new PlayerIcons(playing?PlayerIcons.Icon.PAUSE:PlayerIcons.Icon.PLAY,Color.WHITE));play.setContentDescription(playing?"Pause":"Play");play.setEnabled(s!=null);previous.setEnabled(s!=null&&s.hasPreviousTrack());next.setEnabled(s!=null&&s.hasNextTrack());previous.setAlpha(previous.isEnabled()?1f:.35f);next.setAlpha(next.isEnabled()?1f:.35f);rewind.setEnabled(s!=null);forward.setEnabled(s!=null);int duration=s==null?0:s.duration(),position=s==null?0:s.position();if(!dragging){seek.setProgress(duration>0?(int)(1000L*position/duration):0);elapsed.setText(time(position));}total.setText(time(duration));seek.setEnabled(duration>0&&s!=null&&!s.loading);int theme=getContext().getSharedPreferences("noir",0).getInt("theme",5);PlayerPalette palette=PlayerPalette.forIndex(theme);seek.setProgressTintList(ColorStateList.valueOf(palette.secondary));seek.setThumbTintList(ColorStateList.valueOf(Color.WHITE));loading.setVisibility(s!=null&&s.loading&&videoRequested?VISIBLE:GONE);boolean filled=getContext().getSharedPreferences("noir",0).getBoolean("filled",true);play.setBackground(new VideoControlBackground(palette.primary,filled,dp(2)));play.setImageDrawable(new PlayerIcons(playing?PlayerIcons.Icon.PAUSE:PlayerIcons.Icon.PLAY,filled?palette.filledIcon:palette.primary));
  fit.setContentDescription("Video size: "+new String[]{"Fit screen","Crop","Stretch"}[sizingMode]+", "+Math.round(zoom*100)+" percent zoom. Tap to change, hold for zoom options");fit.setImageDrawable(new PlayerIcons(sizingMode==VideoGeometry.STRETCH?PlayerIcons.Icon.RESIZE_STRETCH:sizingMode==VideoGeometry.CROP?PlayerIcons.Icon.RESIZE_CROP:PlayerIcons.Icon.RESIZE_FIT,sizingMode!=VideoGeometry.FIT||zoom!=1?palette.primary:Color.WHITE));fit.setEnabled(videoRequested);lock.setEnabled(videoRequested);sketch.setEnabled(s!=null&&s.hasVideo&&s.canSketch());boolean sketching=s!=null&&s.sketchEnabled();sketch.setSelected(sketching);sketch.setContentDescription(getResources().getString(sketching?R.string.video_disable_sketch:R.string.video_enable_sketch));sketch.setImageDrawable(new PlayerIcons(PlayerIcons.Icon.SKETCH,sketching?palette.primary:Color.WHITE));audioCover.setVisibility(videoRequested?GONE:VISIBLE);texture.setVisibility(active&&s!=null&&s.hasVideo?VISIBLE:INVISIBLE);errorLabel.setText(s==null?"":s.error);errorLabel.setVisibility(s!=null&&!s.error.isEmpty()&&!touchLocked?VISIBLE:GONE);poster.setScaleType(videoRequested?ImageView.ScaleType.MATRIX:ImageView.ScaleType.FIT_CENTER);setKeepScreenOn(active&&videoRequested&&playing);Bitmap art=s==null?null:s.artwork;if(art!=previousArt||art==null){previousArt=art;if(art!=null){poster.setImageTintList(null);poster.setImageBitmap(art);}else{poster.setImageResource(R.drawable.ic_music);poster.setImageTintList(ColorStateList.valueOf(palette.primary));}}poster.setVisibility(s!=null&&s.videoFrameReady&&s.isVideoSurfaceOwner(owner)&&active&&videoRequested?INVISIBLE:VISIBLE);float nextAspect=s!=null&&s.videoAspect>0?s.videoAspect:16/9f;if(aspect!=nextAspect){aspect=nextAspect;requestLayout();}if(videoRequested)applySizing();
 }
 static String time(long ms){return String.format(Locale.US,"%d:%02d",ms/60000,(ms/1000)%60);}
 void readSizing(){
  android.content.SharedPreferences prefs=getContext().getSharedPreferences("noir",0);
  int nextMode=prefs.getInt("video_sizing_mode",prefs.getBoolean("video_fill",false)?VideoGeometry.CROP:VideoGeometry.FIT);
  nextMode=Math.max(VideoGeometry.FIT,Math.min(VideoGeometry.STRETCH,nextMode));
  float nextZoom=VideoGeometry.clampZoom(prefs.getFloat("video_zoom",1)),nextX=prefs.getFloat("video_pan_x",0),nextY=prefs.getFloat("video_pan_y",0);
  if(sizingMode!=nextMode||zoom!=nextZoom||panX!=nextX||panY!=nextY){sizingMode=nextMode;zoom=nextZoom;panX=nextX;panY=nextY;requestLayout();}
 }
 void saveSizing(){getContext().getSharedPreferences("noir",0).edit().putInt("video_sizing_mode",sizingMode).putBoolean("video_fill",sizingMode==VideoGeometry.CROP).putFloat("video_zoom",zoom).putFloat("video_pan_x",panX).putFloat("video_pan_y",panY).apply();}
 void setSizingMode(int value){sizingMode=Math.max(VideoGeometry.FIT,Math.min(VideoGeometry.STRETCH,value));zoom=1;panX=panY=0;saveSizing();applySizing();render();}
 void changeZoom(float value){zoom=VideoGeometry.clampZoom(value);if(zoom==1)panX=panY=0;applySizing();saveSizing();render();}
 void showSizing(){if(!videoRequested||touchLocked)return;if(sizingSheet!=null){sizingSheet.dismiss();return;}handler.removeCallbacks(hide);sizingSheet=new VideoSizingSheet(this);sizingSheet.show();}
 void showSizingStatus(){showVideoFeedback(getResources().getString(R.string.video_zoom_percent,Math.round(zoom*100)));}
 void showVideoFeedback(String message){if(sizingStatus==null||touchLocked)return;sizingStatus.setText(message);sizingStatus.setVisibility(VISIBLE);handler.removeCallbacks(clearSizingStatus);handler.postDelayed(clearSizingStatus,1200);}
 void applySizing(){
  int w=getWidth(),h=getHeight();if(w<=0||h<=0||!videoRequested)return;
  RectF rect=VideoGeometry.bounds(w,h,aspect,sizingMode,zoom,panX,panY);
  panX=(rect.centerX()-w/2f)/w;panY=(rect.centerY()-h/2f)/h;
  // Keep the surface viewport fixed and transform the picture within it. A
  // pinch never resizes the decoder surface or the overlay hit targets.
  Matrix transform=new Matrix();transform.setRectToRect(new RectF(0,0,w,h),rect,Matrix.ScaleToFit.FILL);texture.setTransform(transform);
  if(poster.getDrawable()!=null){int pw=poster.getDrawable().getIntrinsicWidth(),ph=poster.getDrawable().getIntrinsicHeight();if(pw>0&&ph>0){RectF art=VideoGeometry.bounds(w,h,pw/(float)ph,sizingMode,zoom,panX,panY);Matrix m=new Matrix();m.setRectToRect(new RectF(0,0,pw,ph),art,Matrix.ScaleToFit.FILL);poster.setImageMatrix(m);}}
 }
 @Override protected void onLayout(boolean changed,int l,int t,int r,int b){super.onLayout(changed,l,t,r,b);int w=r-l,h=b-t;texture.layout(0,0,w,h);if(videoRequested){poster.layout(0,0,w,h);applySizing();}else{int side=Math.max(0,Math.min(w-dp(32),h-dp(12)));poster.layout((w-side)/2,(h-side)/2,(w+side)/2,(h+side)/2);}}
 void setTouchLocked(boolean value){
  if(touchLocked==value)return;touchLocked=value;MotionEvent cancel=MotionEvent.obtain(0,0,MotionEvent.ACTION_CANCEL,0,0,0);gestures.cancel(cancel);cancel.recycle();sizingGesture=multiTouch=touchAdjusting=dragging=false;handler.removeCallbacks(hide);handler.removeCallbacks(clearSizingStatus);sizingStatus.setVisibility(GONE);if(sizingSheet!=null)sizingSheet.dismiss();lockedOverlay.setVisibility(value?VISIBLE:GONE);if(value){setControlsVisible(false);errorLabel.setVisibility(GONE);}else{showControls();render();}
 }
 void showPlaybackDetails(){if(touchLocked)return;PlaybackService s=service();TextView body=label((s==null?"":s.error.isEmpty()?"":s.error+"\n\n")+PlaybackDetails.describe(s==null?null:s.player,getDisplay())+(s==null||s.effectNotice.isEmpty()?"":"\n\n"+s.effectNotice),14);body.setPadding(dp(20),dp(12),dp(20),dp(12));ScrollView scroll=new ScrollView(getContext());scroll.addView(body);new android.app.AlertDialog.Builder(getContext()).setTitle("Playback details").setView(scroll).setPositiveButton("Close",null).show();}
 @Override public boolean dispatchTouchEvent(MotionEvent event){
  if(touchLocked)return super.dispatchTouchEvent(event);
  if(event.getActionMasked()==MotionEvent.ACTION_DOWN){downX=lastX=event.getX();downY=lastY=event.getY();multiTouch=false;moved=false;}
  if(videoRequested){if(event.getPointerCount()>1){if(!multiTouch)gestures.cancel(event);multiTouch=true;requestDisallowInterceptTouchEvent(false);}scaleDetector.onTouchEvent(event);}
  boolean handled=super.dispatchTouchEvent(event);
  if(event.getActionMasked()==MotionEvent.ACTION_UP||event.getActionMasked()==MotionEvent.ACTION_CANCEL){if(sizingGesture||multiTouch){sizingGesture=false;saveSizing();showControls();}multiTouch=false;touchAdjusting=false;}
  return handled;
 }
 @Override public boolean onInterceptTouchEvent(MotionEvent event){return videoRequested&&(event.getPointerCount()>1||multiTouch)||super.onInterceptTouchEvent(event);}
 @Override public boolean onTouchEvent(MotionEvent event){
  if(videoRequested)return gestures.onTouch(event);
  switch(event.getActionMasked()){
   case MotionEvent.ACTION_DOWN:return true;
   case MotionEvent.ACTION_POINTER_DOWN:sizingGesture=true;handler.removeCallbacks(hide);return true;
   case MotionEvent.ACTION_MOVE:
    if(Math.abs(event.getX()-downX)>ViewConfiguration.get(getContext()).getScaledTouchSlop()||Math.abs(event.getY()-downY)>ViewConfiguration.get(getContext()).getScaledTouchSlop())moved=true;
    if(videoRequested&&zoom>1&&!multiTouch&&moved&&getWidth()>0&&getHeight()>0){sizingGesture=true;panX+=(event.getX()-lastX)/getWidth();panY+=(event.getY()-lastY)/getHeight();applySizing();}
    lastX=event.getX();lastY=event.getY();return true;
   case MotionEvent.ACTION_UP:
    if(multiTouch||sizingGesture)return true;
    float dx=event.getX()-downX,dy=event.getY()-downY;
    if(dx>dp(48)&&Math.abs(dx)>Math.abs(dy)*1.3f){if(onExpand!=null)onExpand.run();}
    else if(!moved){if(videoRequested||fullscreen){if(controlsVisible)setControlsVisible(false);else showControls();}else if(onPoster!=null)onPoster.run();performClick();}return true;
   case MotionEvent.ACTION_CANCEL:return true;
   default:return true;
  }
 }
 @Override protected void onAttachedToWindow(){super.onAttachedToWindow();handler.post(refresh);}
 @Override protected void onDetachedFromWindow(){gestures.stop();if(sizingSheet!=null)sizingSheet.dismiss();handler.removeCallbacks(refresh);handler.removeCallbacks(hide);handler.removeCallbacks(clearSizingStatus);detach();super.onDetachedFromWindow();}
 @Override public boolean performClick(){super.performClick();return true;}
}
