package com.noir.player;

import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;

/** Small anchored picker: browse actual shader thumbnails, then apply deliberately. */
final class SketchStylePopup extends PopupWindow implements SketchPreviewRequest.Callback {
 final VideoPane pane;final PlaybackService service;final String source;final LinearLayout content;final ImageView preview;final TextView status,name;
 final Spinner selector;final Button apply,cancel;final ProgressBar loading;final StyleAdapter adapter;final String[] names;
 final int panelWidth,panelHeight;Bitmap[] frames;int candidate;SketchPreviewRequest request;boolean closed;
 final Runnable timeout;
 SketchStylePopup(VideoPane pane){
  super(pane.getContext());this.pane=pane;service=pane.service();source=service.source;timeout=()->{if(!closed&&frames==null){service.sketchEffect.cancelPreview(request);failed();}};names=pane.getResources().getStringArray(R.array.video_sketch_styles);candidate=service.sketchEnabled()?service.sketchStyle():0;
  Rect screen=new Rect();pane.getWindowVisibleDisplayFrame(screen);int width=screen.width()>0?screen.width():pane.getResources().getDisplayMetrics().widthPixels,height=screen.height()>0?screen.height():pane.getResources().getDisplayMetrics().heightPixels;
  panelWidth=Math.min(dp(296),width-dp(24));int imageHeight=Math.max(dp(64),Math.min(dp(144),height-dp(24)-dp(168)));panelHeight=dp(168)+imageHeight;
  content=new LinearLayout(pane.getContext());content.setOrientation(LinearLayout.VERTICAL);content.setPadding(dp(12),dp(12),dp(12),dp(12));content.setBackground(round(0xff151515,16));
  LinearLayout header=new LinearLayout(pane.getContext());header.setGravity(Gravity.CENTER_VERTICAL);TextView title=text(pane.getResources().getString(R.string.video_sketch_style_title),16);header.addView(title,new LinearLayout.LayoutParams(0,dp(48),1));ImageButton close=pane.icon(PlayerIcons.Icon.CLOSE,pane.getResources().getString(R.string.video_close));close.setBackgroundColor(Color.TRANSPARENT);close.setOnClickListener(v->dismiss());header.addView(close,new LinearLayout.LayoutParams(dp(48),dp(48)));content.addView(header);
  FrameLayout picture=new FrameLayout(pane.getContext());picture.setBackgroundColor(Color.BLACK);preview=new ImageView(pane.getContext());preview.setScaleType(ImageView.ScaleType.FIT_CENTER);preview.setContentDescription(pane.getResources().getString(R.string.video_style_preview));picture.addView(preview,new FrameLayout.LayoutParams(-1,-1));name=text(names[candidate],11);name.setPadding(dp(6),dp(3),dp(6),dp(3));name.setBackgroundColor(0xaa000000);picture.addView(name,new FrameLayout.LayoutParams(-2,-2,Gravity.TOP|Gravity.START));status=text(pane.getResources().getString(R.string.video_preview_loading),11);status.setGravity(Gravity.CENTER);status.setBackgroundColor(0xbb000000);picture.addView(status,new FrameLayout.LayoutParams(-1,-2,Gravity.BOTTOM));loading=new ProgressBar(pane.getContext());picture.addView(loading,new FrameLayout.LayoutParams(dp(24),dp(24),Gravity.CENTER));content.addView(picture,new LinearLayout.LayoutParams(-1,imageHeight));
  selector=new Spinner(pane.getContext(),Spinner.MODE_DROPDOWN);selector.setContentDescription(pane.getResources().getString(R.string.video_choose_sketch));selector.setPopupBackgroundDrawable(round(0xff202020,12));selector.setDropDownWidth(panelWidth-dp(24));adapter=new StyleAdapter();selector.setAdapter(adapter);selector.setSelection(candidate);selector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){public void onNothingSelected(AdapterView<?> p){}public void onItemSelected(AdapterView<?> p,View v,int position,long id){candidate=position;showCandidate();}});content.addView(selector,new LinearLayout.LayoutParams(-1,dp(48)));
  LinearLayout actions=new LinearLayout(pane.getContext());cancel=button(R.string.video_cancel);cancel.setOnClickListener(v->dismiss());apply=button(R.string.video_apply_style);apply.setEnabled(false);apply.setAlpha(.4f);int accent=PlayerPalette.forIndex(pane.getContext().getSharedPreferences("noir",0).getInt("theme",5)).primary;apply.setTextColor(accent);apply.setOnClickListener(v->{if(service!=PlaybackService.current||!source.equals(service.source)||!service.canSketch()){dismiss();return;}if(candidate==0)service.setSketchEnabled(false);else service.setSketchStyle(candidate);pane.render();pane.showVideoFeedback(names[candidate]);dismiss();});actions.addView(cancel,new LinearLayout.LayoutParams(0,dp(48),1));actions.addView(apply,new LinearLayout.LayoutParams(0,dp(48),1));content.addView(actions);
  setContentView(content);setWidth(panelWidth);setHeight(panelHeight);setFocusable(true);setOutsideTouchable(true);setBackgroundDrawable(round(0xff151515,16));setElevation(dp(8));setInputMethodMode(INPUT_METHOD_NOT_NEEDED);setClippingEnabled(true);
  setOnDismissListener(()->{closed=true;pane.handler.removeCallbacks(timeout);service.sketchEffect.cancelPreview(request);preview.setImageDrawable(null);frames=null;selector.setAdapter(null);if(pane.stylePopup==this)pane.stylePopup=null;pane.showControls();});showCandidate();
 }
 void open(){showAsDropDown(pane.sketch,0,dp(4),Gravity.END);request=service.requestSketchPreviews(this);pane.handler.postDelayed(timeout,3000);}
 int dp(int value){return pane.dp(value);}
 GradientDrawable round(int color,int radius){GradientDrawable bg=new GradientDrawable();bg.setColor(color);bg.setCornerRadius(dp(radius));return bg;}
 TextView text(String value,int size){TextView view=new TextView(pane.getContext());view.setText(value);view.setTextColor(Color.WHITE);view.setTextSize(size);view.setGravity(Gravity.CENTER_VERTICAL);return view;}
 Button button(int label){Button button=new Button(pane.getContext());button.setText(label);button.setTextSize(13);button.setTextColor(Color.WHITE);button.setAllCaps(false);button.setMinWidth(0);button.setMinimumWidth(0);button.setBackgroundColor(Color.TRANSPARENT);return button;}
 void showCandidate(){name.setText(names[candidate]);preview.setImageBitmap(frames==null?null:frames[candidate]);preview.setContentDescription(pane.getResources().getString(R.string.video_style_preview_named,names[candidate]));}
 @Override public void ready(Bitmap[] result){if(closed||!source.equals(service.source)){SketchPreviewRequest.discard(result);dismiss();return;}pane.handler.removeCallbacks(timeout);frames=result;apply.setEnabled(true);apply.setAlpha(1);loading.setVisibility(View.GONE);status.setText(request!=null&&request.hdr?R.string.video_preview_sdr:R.string.video_frame_preview);adapter.notifyDataSetChanged();showCandidate();}
 @Override public void failed(){if(closed)return;pane.handler.removeCallbacks(timeout);apply.setEnabled(true);apply.setAlpha(1);loading.setVisibility(View.GONE);status.setText(R.string.video_preview_unavailable);}
 final class StyleAdapter extends BaseAdapter {
  public int getCount(){return names.length;}public Object getItem(int position){return names[position];}public long getItemId(int position){return position;}
  public View getView(int position,View old,ViewGroup parent){return row(position,false);}
  @Override public View getDropDownView(int position,View old,ViewGroup parent){return row(position,true);}
  View row(int position,boolean dropdown){LinearLayout row=new LinearLayout(pane.getContext());row.setGravity(Gravity.CENTER_VERTICAL);row.setPadding(dp(6),0,dp(6),0);row.setMinimumHeight(dp(48));ImageView thumb=new ImageView(pane.getContext());thumb.setScaleType(ImageView.ScaleType.FIT_CENTER);thumb.setBackgroundColor(Color.BLACK);if(frames!=null)thumb.setImageBitmap(frames[position]);row.addView(thumb,new LinearLayout.LayoutParams(dp(dropdown?56:40),dp(dropdown?32:26)));TextView label=text(names[position],13);label.setSingleLine(true);label.setPadding(dp(8),0,0,0);row.addView(label,new LinearLayout.LayoutParams(0,dp(48),1));return row;}
 }
}
