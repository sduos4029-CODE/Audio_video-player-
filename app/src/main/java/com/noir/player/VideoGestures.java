package com.noir.player;

import android.app.Activity;
import android.content.*;
import android.media.AudioManager;
import android.provider.Settings;
import android.view.*;

/** Surface-only gestures; buttons and the seek bar keep their normal touch behavior. */
final class VideoGestures extends GestureDetector.SimpleOnGestureListener {
 final VideoPane pane;
 final GestureDetector detector;
 final AudioManager audio;
 float startX,startY,startBrightness;
 int startVolume,kind,doubleSide;
 boolean cancelled;Float originalBrightness;
 static final int NONE=0,VOLUME=1,BRIGHTNESS=2,PAN=3,IGNORE=4;
 VideoGestures(VideoPane pane){this.pane=pane;audio=(AudioManager)pane.getContext().getSystemService(Context.AUDIO_SERVICE);detector=new GestureDetector(pane.getContext(),this);detector.setIsLongpressEnabled(false);}
 boolean onTouch(MotionEvent event){
  if(event.getActionMasked()==MotionEvent.ACTION_DOWN)cancelled=false;
  if(cancelled)return true;
  detector.onTouchEvent(event);
  if(event.getActionMasked()==MotionEvent.ACTION_UP){
   if(kind==NONE&&!pane.multiTouch&&!pane.sizingGesture&&pane.zoom==1){float dx=event.getX()-startX,dy=event.getY()-startY;if(dx>pane.dp(48)&&Math.abs(dx)>Math.abs(dy)*1.3f&&pane.onExpand!=null)pane.onExpand.run();}
   if(kind==PAN)pane.saveSizing();
   if(kind!=NONE)pane.showControls();
  }
  return true;
 }
 void cancel(MotionEvent original){cancelled=true;MotionEvent event=MotionEvent.obtain(original);event.setAction(MotionEvent.ACTION_CANCEL);detector.onTouchEvent(event);event.recycle();doubleSide=0;}
 @Override public boolean onDown(MotionEvent e){startX=e.getX();startY=e.getY();kind=NONE;startVolume=audio==null?0:audio.getStreamVolume(AudioManager.STREAM_MUSIC);startBrightness=brightness();return true;}
 boolean onPicture(float y){return y>=pane.dp(64)&&y<pane.getHeight()-pane.dp(150);}
 @Override public boolean onScroll(MotionEvent first,MotionEvent now,float distanceX,float distanceY){
  if(pane.multiTouch||pane.sizingGesture||!onPicture(startY))return true;
  float dx=now.getX()-startX,dy=now.getY()-startY;
  if(kind==NONE){
   if(Math.max(Math.abs(dx),Math.abs(dy))<pane.dp(16))return true;
   if(Math.abs(dy)>Math.abs(dx)*1.2f&&startX>pane.getWidth()*.6f)kind=VOLUME;
   else if(Math.abs(dy)>Math.abs(dx)*1.2f&&startX<pane.getWidth()*.4f)kind=BRIGHTNESS;
   else if(pane.zoom>1)kind=PAN;
   else kind=IGNORE;
   pane.touchAdjusting=true;pane.handler.removeCallbacks(pane.hide);
  }
  float change=-dy/Math.max(pane.dp(120),pane.getHeight()*.65f);
  if(kind==VOLUME&&audio!=null){
   int max=audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC),min=android.os.Build.VERSION.SDK_INT>=28?audio.getStreamMinVolume(AudioManager.STREAM_MUSIC):0;
   int volume=Math.max(min,Math.min(max,Math.round(startVolume+change*max)));
   try{audio.setStreamVolume(AudioManager.STREAM_MUSIC,volume,0);pane.showVideoFeedback(pane.getResources().getString(R.string.video_volume_percent,Math.round(100f*audio.getStreamVolume(AudioManager.STREAM_MUSIC)/Math.max(1,max))));}catch(SecurityException ex){pane.showVideoFeedback(pane.getResources().getString(R.string.video_volume_unavailable));}
  }else if(kind==BRIGHTNESS){
   Activity activity=activity();if(activity!=null){WindowManager.LayoutParams lp=activity.getWindow().getAttributes();if(originalBrightness==null)originalBrightness=lp.screenBrightness;lp.screenBrightness=Math.max(.01f,Math.min(1,startBrightness+change));activity.getWindow().setAttributes(lp);pane.showVideoFeedback(pane.getResources().getString(R.string.video_brightness_percent,Math.round(lp.screenBrightness*100)));}
  }else if(kind==PAN&&pane.getWidth()>0&&pane.getHeight()>0){pane.panX-=distanceX/pane.getWidth();pane.panY-=distanceY/pane.getHeight();pane.applySizing();}
  return true;
 }
 @Override public boolean onSingleTapUp(MotionEvent e){return true;}
 @Override public boolean onSingleTapConfirmed(MotionEvent e){if(!cancelled&&onPicture(e.getY())){if(pane.controlsVisible)pane.setControlsVisible(false);else pane.showControls();pane.performClick();}return true;}
 @Override public boolean onDoubleTap(MotionEvent first){doubleSide=onPicture(first.getY())?(first.getX()<pane.getWidth()/2f?-1:1):0;return true;}
 @Override public boolean onDoubleTapEvent(MotionEvent e){
  if(e.getActionMasked()==MotionEvent.ACTION_DOWN){if((e.getX()<pane.getWidth()/2f?-1:1)!=doubleSide)doubleSide=0;}
  if(e.getActionMasked()==MotionEvent.ACTION_MOVE&&(Math.abs(e.getX()-startX)>pane.dp(16)||Math.abs(e.getY()-startY)>pane.dp(16)))doubleSide=0;
  if(e.getActionMasked()==MotionEvent.ACTION_UP&&doubleSide!=0&&kind==NONE&&!pane.multiTouch&&!pane.sizingGesture&&onPicture(e.getY())){
   PlaybackService service=pane.service();if(service!=null){service.skip(doubleSide*10000);pane.showVideoFeedback(pane.getResources().getString(doubleSide>0?R.string.video_forward_ten:R.string.video_back_ten));pane.render();pane.showControls();}doubleSide=0;
  }
  return true;
 }
 Activity activity(){Context context=pane.getContext();while(context instanceof ContextWrapper){if(context instanceof Activity)return (Activity)context;Context next=((ContextWrapper)context).getBaseContext();if(next==context)break;context=next;}return context instanceof Activity?(Activity)context:null;}
 float brightness(){Activity a=activity();if(a!=null){float value=a.getWindow().getAttributes().screenBrightness;if(value>=0)return value;}try{return Math.max(.01f,Math.min(1,Settings.System.getInt(pane.getContext().getContentResolver(),Settings.System.SCREEN_BRIGHTNESS,128)/255f));}catch(SecurityException ignored){return .5f;}}
 void stop(){pane.touchAdjusting=false;if(originalBrightness!=null){Activity a=activity();if(a!=null){WindowManager.LayoutParams lp=a.getWindow().getAttributes();lp.screenBrightness=originalBrightness;a.getWindow().setAttributes(lp);}originalBrightness=null;}MotionEvent e=MotionEvent.obtain(0,0,MotionEvent.ACTION_CANCEL,0,0,0);cancel(e);e.recycle();}
}
