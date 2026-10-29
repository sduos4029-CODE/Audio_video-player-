package com.noir.player;

import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;

/** One short style row; expand a frame preview only when requested. */
final class SketchStylePopup extends PopupWindow implements SketchPreviewRequest.Callback {
 final VideoPane pane;final PlaybackService service;final String source;final LinearLayout content;final ImageView preview;final TextView status,name;
 final Spinner selector;final ImageButton apply,cancel,previewToggle;final FrameLayout picture;final ProgressBar loading,thumbnailLoading;final StyleAdapter adapter;final String[] names;
 final int panelWidth,imageHeight;int panelHeight;Bitmap[] frames;int candidate;SketchPreviewRequest request;boolean closed,previewExpanded;
 final Runnable timeout;
 SketchStylePopup(VideoPane pane){
  super(pane.getContext());this.pane=pane;service=pane.service();source=service.source;
  timeout=()->{if(!closed&&frames==null){service.sketchEffect.cancelPreview(request);failed();}};
  names=pane.getResources().getStringArray(R.array.video_sketch_styles);candidate=service.sketchEnabled()?service.sketchStyle():0;
  Rect screen=new Rect();pane.getWindowVisibleDisplayFrame(screen);int width=screen.width()>0?screen.width():pane.getResources().getDisplayMetrics().widthPixels,height=screen.height()>0?screen.height():pane.getResources().getDisplayMetrics().heightPixels;
  panelWidth=Math.min(dp(264),width-dp(24));imageHeight=Math.max(dp(48),Math.min(dp(96),height-dp(92)));panelHeight=dp(64);
  content=new LinearLayout(pane.getContext());content.setOrientation(LinearLayout.VERTICAL);content.setPadding(dp(8),dp(8),dp(8),dp(8));content.setBackground(round(0xff151515,12));content.setContentDescription(pane.getResources().getString(R.string.video_sketch_style_title));
  LinearLayout row=new LinearLayout(pane.getContext());row.setGravity(Gravity.CENTER_VERTICAL);
  FrameLayout thumb=new FrameLayout(pane.getContext());previewToggle=pane.icon(PlayerIcons.Icon.SKETCH,pane.getResources().getString(R.string.video_show_style_preview));previewToggle.setPadding(dp(4),dp(4),dp(4),dp(4));previewToggle.setBackground(round(Color.BLACK,6));previewToggle.setOnClickListener(v->setPreviewExpanded(!previewExpanded));thumb.addView(previewToggle,new FrameLayout.LayoutParams(-1,-1));thumbnailLoading=new ProgressBar(pane.getContext());thumb.addView(thumbnailLoading,new FrameLayout.LayoutParams(dp(16),dp(16),Gravity.CENTER));row.addView(thumb,new LinearLayout.LayoutParams(dp(48),dp(48)));
  selector=new Spinner(pane.getContext(),Spinner.MODE_DROPDOWN);selector.setContentDescription(pane.getResources().getString(R.string.video_choose_sketch));selector.setPopupBackgroundDrawable(round(0xff202020,12));selector.setDropDownWidth(panelWidth-dp(16));adapter=new StyleAdapter();selector.setAdapter(adapter);selector.setSelection(candidate);
  selector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){public void onNothingSelected(AdapterView<?> p){}public void onItemSelected(AdapterView<?> p,View v,int position,long id){boolean changed=position!=candidate;candidate=position;showCandidate();if(changed)setPreviewExpanded(true);}});
  row.addView(selector,new LinearLayout.LayoutParams(0,dp(48),1));
  int accent=PlayerPalette.forIndex(pane.getContext().getSharedPreferences("noir",0).getInt("theme",5)).primary;
  apply=pane.icon(PlayerIcons.Icon.CHECK,pane.getResources().getString(R.string.video_apply_style));apply.setImageDrawable(new PlayerIcons(PlayerIcons.Icon.CHECK,accent));apply.setBackgroundColor(Color.TRANSPARENT);apply.setEnabled(false);apply.setAlpha(.4f);
  apply.setOnClickListener(v->{if(service!=PlaybackService.current||!source.equals(service.source)||!service.canSketch()){dismiss();return;}if(candidate==0)service.setSketchEnabled(false);else service.setSketchStyle(candidate);pane.render();pane.showVideoFeedback(names[candidate]);dismiss();});row.addView(apply,new LinearLayout.LayoutParams(dp(48),dp(48)));
  cancel=pane.icon(PlayerIcons.Icon.CLOSE,pane.getResources().getString(R.string.video_cancel));cancel.setBackgroundColor(Color.TRANSPARENT);cancel.setOnClickListener(v->dismiss());row.addView(cancel,new LinearLayout.LayoutParams(dp(48),dp(48)));content.addView(row);
  picture=new FrameLayout(pane.getContext());picture.setBackgroundColor(Color.BLACK);preview=new ImageView(pane.getContext());preview.setScaleType(ImageView.ScaleType.FIT_CENTER);preview.setContentDescription(pane.getResources().getString(R.string.video_style_preview));picture.addView(preview,new FrameLayout.LayoutParams(-1,-1));
  name=text(names[candidate],11);name.setPadding(dp(6),dp(3),dp(6),dp(3));name.setBackgroundColor(0xaa000000);picture.addView(name,new FrameLayout.LayoutParams(-2,-2,Gravity.TOP|Gravity.START));
  status=text(pane.getResources().getString(R.string.video_preview_loading),11);status.setGravity(Gravity.CENTER);status.setBackgroundColor(0xbb000000);picture.addView(status,new FrameLayout.LayoutParams(-1,-2,Gravity.BOTTOM));loading=new ProgressBar(pane.getContext());picture.addView(loading,new FrameLayout.LayoutParams(dp(24),dp(24),Gravity.CENTER));LinearLayout.LayoutParams pictureParams=new LinearLayout.LayoutParams(-1,imageHeight);pictureParams.topMargin=dp(4);content.addView(picture,pictureParams);picture.setVisibility(View.GONE);
  setContentView(content);setWidth(panelWidth);setHeight(panelHeight);setFocusable(true);setOutsideTouchable(true);setBackgroundDrawable(round(0xff151515,12));setElevation(dp(8));setInputMethodMode(INPUT_METHOD_NOT_NEEDED);setClippingEnabled(true);
  setOnDismissListener(()->{closed=true;pane.handler.removeCallbacks(timeout);service.sketchEffect.cancelPreview(request);preview.setImageDrawable(null);previewToggle.setImageDrawable(null);frames=null;selector.setAdapter(null);if(pane.stylePopup==this)pane.stylePopup=null;pane.showControls();});showCandidate();
 }
 void open(){showAsDropDown(pane.sketch,0,dp(4),Gravity.END);request=service.requestSketchPreviews(this);pane.handler.postDelayed(timeout,3000);}
 int dp(int value){return pane.dp(value);}
 GradientDrawable round(int color,int radius){GradientDrawable bg=new GradientDrawable();bg.setColor(color);bg.setCornerRadius(dp(radius));return bg;}
 TextView text(String value,int size){TextView view=new TextView(pane.getContext());view.setText(value);view.setTextColor(Color.WHITE);view.setTextSize(size);view.setGravity(Gravity.CENTER_VERTICAL);return view;}
 void setPreviewExpanded(boolean value){previewExpanded=value;picture.setVisibility(value?View.VISIBLE:View.GONE);previewToggle.setContentDescription(pane.getResources().getString(value?R.string.video_hide_style_preview:R.string.video_show_style_preview));panelHeight=dp(64)+(value?imageHeight+dp(4):0);setHeight(panelHeight);if(isShowing())update(panelWidth,panelHeight);content.requestLayout();}
 void showCandidate(){name.setText(names[candidate]);preview.setImageBitmap(frames==null?null:frames[candidate]);if(frames!=null)previewToggle.setImageBitmap(frames[candidate]);preview.setContentDescription(pane.getResources().getString(R.string.video_style_preview_named,names[candidate]));}
 @Override public void ready(Bitmap[] result){if(closed||!source.equals(service.source)){SketchPreviewRequest.discard(result);dismiss();return;}pane.handler.removeCallbacks(timeout);frames=result;apply.setEnabled(true);apply.setAlpha(1);loading.setVisibility(View.GONE);thumbnailLoading.setVisibility(View.GONE);status.setText(request!=null&&request.hdr?R.string.video_preview_sdr:R.string.video_frame_preview);adapter.notifyDataSetChanged();showCandidate();}
 @Override public void failed(){if(closed)return;pane.handler.removeCallbacks(timeout);apply.setEnabled(true);apply.setAlpha(1);loading.setVisibility(View.GONE);thumbnailLoading.setVisibility(View.GONE);status.setText(R.string.video_preview_unavailable);}
 final class StyleAdapter extends BaseAdapter {
  public int getCount(){return names.length;}public Object getItem(int position){return names[position];}public long getItemId(int position){return position;}
  public View getView(int position,View old,ViewGroup parent){return row(position,false);}
  @Override public View getDropDownView(int position,View old,ViewGroup parent){return row(position,true);}
  View row(int position,boolean dropdown){LinearLayout row=new LinearLayout(pane.getContext());row.setGravity(Gravity.CENTER_VERTICAL);row.setPadding(dp(4),0,dp(4),0);row.setMinimumHeight(dp(48));if(dropdown){ImageView thumb=new ImageView(pane.getContext());thumb.setScaleType(ImageView.ScaleType.FIT_CENTER);thumb.setBackgroundColor(Color.BLACK);if(frames!=null)thumb.setImageBitmap(frames[position]);row.addView(thumb,new LinearLayout.LayoutParams(dp(56),dp(32)));}TextView label=text(names[position],13);label.setSingleLine(true);label.setEllipsize(android.text.TextUtils.TruncateAt.END);label.setPadding(dropdown?dp(8):0,0,0,0);row.addView(label,new LinearLayout.LayoutParams(0,dp(48),1));return row;}
 }
}
