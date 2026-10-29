package com.noir.player;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;
import java.util.Locale;

/** One compact sizing menu for inline and fullscreen video. */
final class VideoSizingSheet extends Dialog {
 final VideoPane pane;
 final Button[] choices=new Button[3];
 final Button zoomOut,zoomIn,reset;
 final TextView zoomValue;
 VideoSizingSheet(VideoPane pane){
  super(pane.getContext());this.pane=pane;requestWindowFeature(Window.FEATURE_NO_TITLE);
  FrameLayout safe=new FrameLayout(getContext());SafeArea.install(safe);
  LinearLayout root=new LinearLayout(getContext());root.setOrientation(LinearLayout.VERTICAL);root.setPadding(dp(12),dp(4),dp(12),dp(8));
  GradientDrawable bg=new GradientDrawable();bg.setColor(0xff161616);bg.setCornerRadius(dp(18));root.setBackground(bg);safe.addView(root);
  LinearLayout header=new LinearLayout(getContext());header.setGravity(Gravity.CENTER_VERTICAL);
  TextView title=text("Video size",16);header.addView(title,new LinearLayout.LayoutParams(0,dp(48),1));
  reset=button("Reset",()->{pane.changeZoom(1);refresh();});reset.setContentDescription("Reset zoom");header.addView(reset,new LinearLayout.LayoutParams(dp(48),dp(48)));
  ImageButton close=pane.icon(PlayerIcons.Icon.CLOSE,"Close video size");close.setOnClickListener(v->dismiss());header.addView(close,new LinearLayout.LayoutParams(dp(48),dp(48)));root.addView(header);
  LinearLayout sizes=new LinearLayout(getContext());String[] names={"Fit","Crop","Stretch"};
  for(int i=0;i<3;i++){final int index=i;Button b=button(names[i],()->{pane.setSizingMode(index);refresh();});b.setContentDescription(new String[]{"Fit: show the entire video","Crop: fill screen, crop edges","Stretch: fill screen, change proportions"}[i]);choices[i]=b;LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,dp(48),1);lp.setMargins(dp(2),0,dp(2),0);sizes.addView(b,lp);}root.addView(sizes);
  LinearLayout zoomRow=new LinearLayout(getContext());zoomRow.setGravity(Gravity.CENTER_VERTICAL);
  zoomOut=button("−",()->{pane.changeZoom(pane.zoom/1.25f);refresh();});zoomOut.setContentDescription("Zoom out");zoomRow.addView(zoomOut,new LinearLayout.LayoutParams(dp(48),dp(48)));
  zoomValue=text("100%",16);zoomValue.setGravity(Gravity.CENTER);zoomValue.setContentDescription("Video zoom");zoomRow.addView(zoomValue,new LinearLayout.LayoutParams(0,dp(48),1));
  zoomIn=button("+",()->{pane.changeZoom(pane.zoom*1.25f);refresh();});zoomIn.setContentDescription("Zoom in");zoomRow.addView(zoomIn,new LinearLayout.LayoutParams(dp(48),dp(48)));root.addView(zoomRow);
  setContentView(safe);refresh();
  setOnDismissListener(d->{pane.sizingSheet=null;pane.showControls();});
 }
 int dp(int n){return pane.dp(n);}
 TextView text(String value,int size){TextView v=new TextView(getContext());v.setText(value);v.setTextSize(size);v.setTextColor(Color.WHITE);v.setGravity(Gravity.CENTER_VERTICAL);return v;}
 Button button(String value,Runnable action){Button b=new Button(getContext());b.setText(value);b.setTextColor(Color.WHITE);b.setTextSize(12);b.setAllCaps(false);b.setMinWidth(0);b.setMinimumWidth(0);b.setPadding(dp(8),0,dp(8),0);GradientDrawable bg=new GradientDrawable();bg.setColor(0xff292929);bg.setCornerRadius(dp(12));b.setBackground(new android.graphics.drawable.RippleDrawable(android.content.res.ColorStateList.valueOf(0x33ffffff),bg,null));b.setOnClickListener(v->action.run());return b;}
 void refresh(){
  int theme=getContext().getSharedPreferences("noir",0).getInt("theme",5);int accent=PlayerPalette.forIndex(theme).primary;
  for(int i=0;i<choices.length;i++){boolean selected=pane.sizingMode==i;choices[i].setSelected(selected);GradientDrawable bg=new GradientDrawable();bg.setCornerRadius(dp(12));bg.setColor(selected?0xff343434:0xff222222);bg.setStroke(dp(1),selected?accent:Color.TRANSPARENT);choices[i].setBackground(bg);}
  zoomValue.setText(String.format(Locale.US,"%.0f%%",pane.zoom*100));zoomOut.setEnabled(pane.zoom>VideoGeometry.MIN_ZOOM+.001f);zoomIn.setEnabled(pane.zoom<VideoGeometry.MAX_ZOOM-.001f);
 }
 @Override protected void onStart(){super.onStart();Window w=getWindow();if(w!=null){w.setBackgroundDrawableResource(android.R.color.transparent);w.setGravity(Gravity.BOTTOM);w.setLayout(Math.min(getContext().getResources().getDisplayMetrics().widthPixels-dp(24),dp(300)),-2);w.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);WindowManager.LayoutParams a=w.getAttributes();a.dimAmount=.35f;w.setAttributes(a);}}
}
