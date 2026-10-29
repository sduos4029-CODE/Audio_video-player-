package com.noir.player;

import android.app.Activity;
import android.content.*;
import android.graphics.Color;
import android.os.*;
import android.text.TextUtils;
import android.view.*;
import android.webkit.*;
import android.widget.*;

/** Inset-aware browser UI attached to the service-owned web playback surface. */
public class YouTubeActivity extends Activity implements WebPlaybackService.Screen {
 WebPlaybackService browser; WebView web; TextView status; ProgressBar progress; FrameLayout frame,webContainer; LinearLayout toolbar; View fullscreen; WebChromeClient.CustomViewCallback fullscreenCallback; boolean bound;Bundle savedState;
 final ServiceConnection connection=new ServiceConnection(){
  public void onServiceConnected(ComponentName name,IBinder binder){browser=((WebPlaybackService.LocalBinder)binder).service();attachBrowser();}
  public void onServiceDisconnected(ComponentName name){browser=null;message("Web player stopped. Tap reload to reopen it.");}
 };
 protected void onCreate(Bundle state){super.onCreate(state);savedState=state;
  frame=new FrameLayout(this);frame.setBackgroundColor(Color.BLACK);LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setBackgroundColor(Color.BLACK);
  frame.setOnApplyWindowInsetsListener((view,insets)->{
   if(Build.VERSION.SDK_INT>=30){android.graphics.Insets bars=insets.getInsets(WindowInsets.Type.systemBars()|WindowInsets.Type.displayCutout());frame.setPadding(bars.left,bars.top,bars.right,bars.bottom);}else frame.setPadding(insets.getSystemWindowInsetLeft(),insets.getSystemWindowInsetTop(),insets.getSystemWindowInsetRight(),insets.getSystemWindowInsetBottom());
   return insets;
  });
  toolbar=new LinearLayout(this);toolbar.setGravity(Gravity.CENTER_VERTICAL);toolbar.setBackgroundColor(0xff111111);
  ImageButton back=icon(R.drawable.ic_back,"Go back");back.setOnClickListener(v->onBackPressed());toolbar.addView(back,new LinearLayout.LayoutParams(dp(48),dp(48)));
  TextView heading=new TextView(this);heading.setText("Pulse");heading.setTextSize(18);heading.setTextColor(Color.WHITE);heading.setSingleLine(true);heading.setEllipsize(TextUtils.TruncateAt.END);heading.setGravity(Gravity.CENTER_VERTICAL);heading.setPadding(dp(8),0,0,0);toolbar.addView(heading,new LinearLayout.LayoutParams(0,-1,1));
  ImageButton reload=icon(R.drawable.ic_reload,"Reload page");reload.setOnClickListener(v->reload());toolbar.addView(reload,new LinearLayout.LayoutParams(dp(48),dp(48)));
  ImageButton options=icon(R.drawable.ic_options,"Playback and filtering options");options.setOnClickListener(this::options);toolbar.addView(options,new LinearLayout.LayoutParams(dp(48),dp(48)));root.addView(toolbar,new LinearLayout.LayoutParams(-1,dp(56)));
  status=new TextView(this);status.setTextColor(0xffeeeeee);status.setTextSize(13);status.setPadding(dp(16),dp(8),dp(16),dp(8));status.setMaxLines(3);status.setVisibility(View.GONE);root.addView(status);
  progress=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);root.addView(progress,new LinearLayout.LayoutParams(-1,dp(2)));
  webContainer=new FrameLayout(this);root.addView(webContainer,new LinearLayout.LayoutParams(-1,0,1));frame.addView(root);setContentView(frame);frame.requestApplyInsets();
  Intent service=new Intent(this,WebPlaybackService.class);startForegroundService(service);bound=bindService(service,connection,BIND_AUTO_CREATE);
 }
 int dp(int n){return Math.round(n*getResources().getDisplayMetrics().density);}
 ImageButton icon(int drawable,String description){ImageButton b=new ImageButton(this);b.setImageResource(drawable);b.setContentDescription(description);b.setPadding(dp(12),dp(12),dp(12),dp(12));android.util.TypedValue value=new android.util.TypedValue();getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless,value,true);b.setBackgroundResource(value.resourceId);return b;}
 void attachBrowser(){browser.screen=this;browser.screenVisible=true;web=browser.web;if(web.getParent() instanceof ViewGroup)((ViewGroup)web.getParent()).removeView(web);webContainer.removeAllViews();webContainer.addView(web,new FrameLayout.LayoutParams(-1,-1));web.onResume();boolean newRequest=savedState==null&&(getIntent().hasExtra("id")||getIntent().hasExtra("music"));if(web.getUrl()==null||newRequest)web.loadUrl(YouTubeNavigation.startUrl(getIntent().getStringExtra("id"),getIntent().getBooleanExtra("music",false)));savedState=null;}
 void reload(){if(browser==null){recreate();return;}if(browser.gone){webContainer.removeAllViews();browser.createWebView();attachBrowser();}else web.reload();status.setVisibility(View.GONE);}
 void options(View anchor){if(browser==null)return;PopupMenu menu=new PopupMenu(this,anchor);menu.getMenu().add(0,1,0,"Background audio (experimental)").setCheckable(true).setChecked(browser.background);menu.getMenu().add(0,2,1,"Ad and tracker filtering").setCheckable(true).setChecked(browser.filtering);menu.getMenu().add(0,3,2,"Stop web audio");menu.setOnMenuItemClickListener(item->{if(item.getItemId()==1){browser.setBackground(!browser.background);if(browser.background)message("Background audio enabled. Website and Android behavior can still interrupt playback.");}else if(item.getItemId()==2)browser.setFiltering(!browser.filtering);else{browser.command("pause");stopService(new Intent(this,WebPlaybackService.class));finish();}return true;});menu.show();}
 public void progress(int value){progress.setProgress(value);progress.setVisibility(value==100?View.GONE:View.VISIBLE);}
 public void message(String text){status.setText(text);status.setVisibility(View.VISIBLE);}
 public void ready(){status.setVisibility(View.GONE);}
 public void rendererGone(){message("Web player closed unexpectedly. Tap reload to reopen it.");}
 public void showFullscreen(View view,WebChromeClient.CustomViewCallback callback){if(fullscreen!=null){callback.onCustomViewHidden();return;}fullscreen=view;fullscreenCallback=callback;frame.addView(view,new FrameLayout.LayoutParams(-1,-1));getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);}
 public void hideFullscreen(){if(fullscreen!=null){frame.removeView(fullscreen);fullscreen=null;getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);if(fullscreenCallback!=null){fullscreenCallback.onCustomViewHidden();fullscreenCallback=null;}}}
 public void onBackPressed(){if(fullscreen!=null)hideFullscreen();else if(web!=null&&browser!=null&&!browser.gone&&web.canGoBack())web.goBack();else super.onBackPressed();}
 protected void onPause(){super.onPause();if(browser!=null)browser.screenVisible=false;if(browser!=null&&!browser.background&&!browser.gone){browser.command("pause");web.onPause();}}
 protected void onResume(){super.onResume();if(browser!=null)browser.screenVisible=true;if(web!=null&&browser!=null&&!browser.gone)web.onResume();}
 protected void onDestroy(){hideFullscreen();if(browser!=null){if(browser.screen==this){browser.screen=null;browser.screenVisible=false;}if(!browser.background&&isFinishing())stopService(new Intent(this,WebPlaybackService.class));if(web!=null&&web.getParent()==webContainer)webContainer.removeView(web);}if(bound)unbindService(connection);super.onDestroy();}
}
