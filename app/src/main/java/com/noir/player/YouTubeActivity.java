package com.noir.player;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.*;
import android.webkit.*;
import android.widget.*;

/** Browser-based YouTube inside Pulse. No external app launch or media extraction. */
public class YouTubeActivity extends Activity {
 boolean rendererGone; WebView web; TextView status; ProgressBar progress; FrameLayout frame; View fullscreen; WebChromeClient.CustomViewCallback fullscreenCallback;
 @android.annotation.SuppressLint("SetJavaScriptEnabled") // Required for trusted YouTube pages; no native JS bridge is exposed.
 protected void onCreate(Bundle state){
  super.onCreate(state);if(PlaybackService.current!=null)PlaybackService.current.pause();
  frame=new FrameLayout(this);LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setBackgroundColor(Color.BLACK);
  LinearLayout toolbar=new LinearLayout(this);Button back=new Button(this);back.setText("Back");back.setOnClickListener(v->onBackPressed());toolbar.addView(back);
  TextView heading=new TextView(this);heading.setText("PULSE • ONLINE");heading.setTextColor(Color.WHITE);heading.setGravity(Gravity.CENTER);toolbar.addView(heading,new LinearLayout.LayoutParams(0,-1,1));Button reload=new Button(this);reload.setText("Reload");reload.setOnClickListener(v->{if(rendererGone)recreate();else web.reload();});toolbar.addView(reload);root.addView(toolbar);
  status=new TextView(this);status.setText("Loading inside Pulse…");status.setTextColor(0xffcccccc);status.setPadding(16,8,16,8);root.addView(status);
  progress=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);root.addView(progress,new LinearLayout.LayoutParams(-1,6));
  web=new WebView(this);web.setBackgroundColor(Color.BLACK);WebSettings settings=web.getSettings();settings.setJavaScriptEnabled(true);settings.setDomStorageEnabled(true);settings.setMediaPlaybackRequiresUserGesture(true);settings.setAllowFileAccess(false);settings.setAllowContentAccess(false);settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);settings.setSafeBrowsingEnabled(true);CookieManager.getInstance().setAcceptThirdPartyCookies(web,false);
  web.setWebViewClient(new WebViewClient(){
   public boolean shouldOverrideUrlLoading(WebView view,WebResourceRequest request){return blockNavigation(request.getUrl().toString());}
   public boolean shouldOverrideUrlLoading(WebView view,String url){return blockNavigation(url);}
   public void onPageFinished(WebView view,String url){status.setText("Playback stays in Pulse • web controls and availability apply");}
   public void onReceivedError(WebView view,WebResourceRequest request,WebResourceError error){if(request.isForMainFrame())status.setText("Page failed to load. Check your connection, update Android System WebView, then tap Reload.");}
   public void onReceivedHttpError(WebView view,WebResourceRequest request,WebResourceResponse response){if(request.isForMainFrame())status.setText("Website returned HTTP "+response.getStatusCode()+". Tap Reload to retry.");}
   public boolean onRenderProcessGone(WebView view,RenderProcessGoneDetail detail){rendererGone=true;view.destroy();status.setText("Web playback stopped. Tap Reload to reopen the page.");return true;}
  });
  web.setWebChromeClient(new WebChromeClient(){
   public void onProgressChanged(WebView view,int value){progress.setProgress(value);progress.setVisibility(value==100?View.GONE:View.VISIBLE);}
   public void onShowCustomView(View view,CustomViewCallback callback){if(fullscreen!=null){callback.onCustomViewHidden();return;}fullscreen=view;fullscreenCallback=callback;frame.addView(view,new FrameLayout.LayoutParams(-1,-1));getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);}
   public void onHideCustomView(){hideFullscreen();}
  });
  root.addView(web,new LinearLayout.LayoutParams(-1,0,1));frame.addView(root);setContentView(frame);
  if(state==null||web.restoreState(state)==null)web.loadUrl(YouTubeNavigation.startUrl(getIntent().getStringExtra("id"),getIntent().getBooleanExtra("music",false)));
 }
 boolean blockNavigation(String url){if(YouTubeNavigation.allowed(url))return false;status.setText("This link requires another app or unsupported site. Use the website player inside Pulse.");return true;}
 void hideFullscreen(){if(fullscreen!=null){frame.removeView(fullscreen);fullscreen=null;getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);if(fullscreenCallback!=null){fullscreenCallback.onCustomViewHidden();fullscreenCallback=null;}}}
 public void onBackPressed(){if(fullscreen!=null)hideFullscreen();else if(!rendererGone&&web.canGoBack())web.goBack();else super.onBackPressed();}
 protected void onSaveInstanceState(Bundle state){if(!rendererGone)web.saveState(state);super.onSaveInstanceState(state);}
 protected void onPause(){super.onPause();if(rendererGone)return;web.evaluateJavascript("document.querySelectorAll('video,audio').forEach(function(v){v.pause();});",null);web.onPause();}
 protected void onResume(){super.onResume();if(web!=null&&!rendererGone)web.onResume();}
 protected void onDestroy(){hideFullscreen();if(web!=null&&!rendererGone){web.stopLoading();web.destroy();}super.onDestroy();}
}
