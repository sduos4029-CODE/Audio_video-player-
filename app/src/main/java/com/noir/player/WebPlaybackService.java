package com.noir.player;

import android.app.*;
import android.content.*;
import android.graphics.Color;
import android.media.MediaMetadata;
import android.media.session.*;
import android.os.*;
import android.view.*;
import android.webkit.*;
import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Arrays;
import androidx.webkit.*;
import org.json.*;

/** Owns the web player across Activity/lock transitions; reports standard media controls. */
public class WebPlaybackService extends Service {
 public static WebPlaybackService current;
 interface Screen {
  void progress(int value);void message(String value);void ready();void showFullscreen(View view,WebChromeClient.CustomViewCallback callback);void hideFullscreen();void rendererGone();
 }
 final Binder binder=new LocalBinder();class LocalBinder extends Binder{WebPlaybackService service(){return WebPlaybackService.this;}}
 ServiceWorkerController serviceWorkers;ScriptHandler filterScript;String filterSource="";BackgroundWebView web;Screen screen;MediaSession session;final Handler handler=new Handler(Looper.getMainLooper());
 volatile boolean filtering=true;boolean gone,selected=true,background=true,playing,foreground,screenVisible;long position,duration;String title="Pulse web player";
 final BroadcastReceiver noisy=new BroadcastReceiver(){public void onReceive(Context c,Intent i){command("pause");}};
 final Runnable poll=new Runnable(){public void run(){if(!gone&&web!=null&&(selected||screenVisible)){String url=web.getUrl();if(url!=null&&YouTubeNavigation.mediaPage(url))web.evaluateJavascript(SNAPSHOT,WebPlaybackService.this::snapshot);}handler.postDelayed(this,2000);}};
 static final String SNAPSHOT="(function(){var v=document.querySelector('video,audio');if(!v)return null;var m=navigator.mediaSession&&navigator.mediaSession.metadata;return JSON.stringify({title:m&&m.title||document.title||'Pulse web player',playing:!v.paused&&!v.ended,position:Number.isFinite(v.currentTime)?v.currentTime:0,duration:Number.isFinite(v.duration)?v.duration:0});})()";
 public void onCreate(){super.onCreate();current=this;background=getSharedPreferences("noir",0).getBoolean("web_background",true);filtering=getSharedPreferences("noir",0).getBoolean("web_filter",true);
  ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(new NotificationChannel("web-playback","Web media playback",NotificationManager.IMPORTANCE_LOW));
  session=new MediaSession(this,"Pulse web playback");session.setCallback(new MediaSession.Callback(){public void onPlay(){command("play");}public void onPause(){command("pause");}public void onSeekTo(long ms){seek(ms);}public void onRewind(){skip(-10);}public void onFastForward(){skip(10);}public void onCustomAction(String action,Bundle extras){if("back".equals(action))skip(-10);else if("forward".equals(action))skip(10);}public void onStop(){command("pause");selected=false;session.setActive(false);stopForeground(STOP_FOREGROUND_REMOVE);foreground=false;stopSelf();}});session.setActive(true);
  registerReceiver(noisy,new IntentFilter(android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY));createWebView();handler.post(poll);
 }
 @android.annotation.SuppressLint("SetJavaScriptEnabled") // Trusted-site browser; no native JavaScript bridge is exposed.
 void createWebView(){gone=false;web=new BackgroundWebView(this);web.backgroundEnabled=background;web.setBackgroundColor(Color.BLACK);WebSettings s=web.getSettings();s.setJavaScriptEnabled(true);s.setDomStorageEnabled(true);s.setMediaPlaybackRequiresUserGesture(true);s.setAllowFileAccess(false);s.setAllowContentAccess(false);s.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);s.setSafeBrowsingEnabled(true);CookieManager.getInstance().setAcceptThirdPartyCookies(web,false);installFilter();
  try{serviceWorkers=ServiceWorkerController.getInstance();serviceWorkers.setServiceWorkerClient(new ServiceWorkerClient(){public WebResourceResponse shouldInterceptRequest(WebResourceRequest request){return filterResponse(request);}});}catch(UnsupportedOperationException ignored){serviceWorkers=null;}
  web.setWebViewClient(new WebViewClient(){
   public boolean shouldOverrideUrlLoading(WebView v,WebResourceRequest r){return block(r.getUrl().toString());}
   public boolean shouldOverrideUrlLoading(WebView v,String url){return block(url);}
   public WebResourceResponse shouldInterceptRequest(WebView v,WebResourceRequest r){return filterResponse(r);}
   public void onPageStarted(WebView v,String url,android.graphics.Bitmap icon){if(filtering&&YouTubeNavigation.mediaPage(url)&&filterScript==null)v.evaluateJavascript(filterSource,null);}
   public void onPageFinished(WebView v,String url){if(screen!=null)screen.ready();if(filtering&&YouTubeNavigation.mediaPage(url))v.evaluateJavascript(filterSource,null);}
   public void onReceivedError(WebView v,WebResourceRequest r,WebResourceError e){if(r.isForMainFrame())message("Page failed to load. Check your connection and Android System WebView, then reload.");}
   public void onReceivedHttpError(WebView v,WebResourceRequest r,WebResourceResponse response){if(r.isForMainFrame())message("Website returned HTTP "+response.getStatusCode()+". Try reload.");}
   public boolean onRenderProcessGone(WebView v,RenderProcessGoneDetail d){gone=true;playing=false;v.destroy();update();if(screen!=null)screen.rendererGone();return true;}
  });
  web.setWebChromeClient(new WebChromeClient(){public void onProgressChanged(WebView v,int n){if(screen!=null)screen.progress(n);}public void onShowCustomView(View v,CustomViewCallback callback){if(screen!=null)screen.showFullscreen(v,callback);else callback.onCustomViewHidden();}public void onHideCustomView(){if(screen!=null)screen.hideFullscreen();}});
 }
 void installFilter(){if(filterScript!=null){try{filterScript.remove();}catch(RuntimeException ignored){}filterScript=null;}if(!filtering)return;try(java.io.InputStream in=getAssets().open("web_filter.js");java.io.ByteArrayOutputStream out=new java.io.ByteArrayOutputStream()){byte[] buffer=new byte[4096];int count;while((count=in.read(buffer))!=-1)out.write(buffer,0,count);filterSource=out.toString("UTF-8");}catch(java.io.IOException e){filterSource="";return;}if(WebViewFeature.isFeatureSupported(WebViewFeature.DOCUMENT_START_SCRIPT))filterScript=WebViewCompat.addDocumentStartJavaScript(web,filterSource,new HashSet<>(Arrays.asList("https://*.youtube.com","https://youtube.com")));}
 WebResourceResponse filterResponse(WebResourceRequest r){if(filtering&&!r.isForMainFrame()&&WebRequestFilter.blocked(r.getUrl().toString()))return new WebResourceResponse("text/plain","UTF-8",200,"OK",Collections.emptyMap(),new ByteArrayInputStream(new byte[0]));return null;}
 boolean block(String url){if(YouTubeNavigation.allowed(url))return false;message("Use website playback; external-app links stay blocked.");return true;}
 void message(String text){if(screen!=null)screen.message(text);}
 public int onStartCommand(Intent intent,int flags,int id){String action=intent==null?null:intent.getAction();
  if("stop".equals(action)){command("pause");selected=false;session.setActive(false);stopForeground(STOP_FOREGROUND_REMOVE);foreground=false;stopSelf();return START_NOT_STICKY;}
  selected=true;session.setActive(true);startForeground(2,notification());foreground=true;
  if(action==null||"play".equals(action)){if(PlaybackService.current!=null)PlaybackService.current.suspendForWeb();}
  if("play".equals(action)||"pause".equals(action))command(action);else if("back".equals(action))skip(-10);else if("forward".equals(action))skip(10);
  return START_NOT_STICKY;
 }
 void ensureForeground(){if(selected&&(background||playing)){startForeground(2,notification());foreground=true;}}
 void snapshot(String value){if(value==null||value.length()>8192)return;try{Object decoded=new JSONTokener(value).nextValue();if(!(decoded instanceof String)){if(playing){playing=false;update();}return;}JSONObject state=new JSONObject((String)decoded);String nextTitle=state.optString("title","Pulse web player");if(nextTitle.length()>160)nextTitle=nextTitle.substring(0,160);long nextPosition=(long)(Math.max(0,Math.min(864000,state.optDouble("position",0)))*1000);long nextDuration=(long)(Math.max(0,Math.min(864000,state.optDouble("duration",0)))*1000);boolean nextPlaying=state.optBoolean("playing",false);if(!selected){if(!screenVisible||!nextPlaying)return;selected=true;session.setActive(true);}boolean changed=!title.equals(nextTitle)||playing!=nextPlaying||duration!=nextDuration;title=nextTitle;position=nextPosition;duration=nextDuration;playing=nextPlaying;updateSession();if(changed)update();}catch(JSONException ignored){}}
 void updateSession(){session.setMetadata(new MediaMetadata.Builder().putString(MediaMetadata.METADATA_KEY_TITLE,title).putString(MediaMetadata.METADATA_KEY_ARTIST,"Pulse web player").putLong(MediaMetadata.METADATA_KEY_DURATION,duration).build());session.setPlaybackState(new PlaybackState.Builder().setActions(PlaybackState.ACTION_PLAY|PlaybackState.ACTION_PAUSE|PlaybackState.ACTION_PLAY_PAUSE|PlaybackState.ACTION_SEEK_TO|PlaybackState.ACTION_REWIND|PlaybackState.ACTION_FAST_FORWARD|PlaybackState.ACTION_STOP).addCustomAction(new PlaybackState.CustomAction.Builder("back","Back 10 seconds",android.R.drawable.ic_media_rew).build()).addCustomAction(new PlaybackState.CustomAction.Builder("forward","Forward 10 seconds",android.R.drawable.ic_media_ff).build()).setState(playing?PlaybackState.STATE_PLAYING:PlaybackState.STATE_PAUSED,position,playing?1:0).build());}
 void update(){if(!selected)return;updateSession();ensureForeground();if(foreground)((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).notify(2,notification());}
 void command(String action){if(gone||web==null)return;String js="(function(){var v=document.querySelector('video,audio');if(v){"+("play".equals(action)?"v.play().catch(function(){});":"v.pause();")+"}})()";web.evaluateJavascript(js,null);if("pause".equals(action)){playing=false;update();}}
 void skip(int seconds){if(!gone)web.evaluateJavascript("(function(){var v=document.querySelector('video,audio');if(v)v.currentTime=Math.max(0,v.currentTime+("+seconds+"));})()",null);}
 void seek(long ms){if(!gone)web.evaluateJavascript("(function(){var v=document.querySelector('video,audio');if(v)v.currentTime="+Math.max(0,ms)/1000.0+";})()",null);}
 void setBackground(boolean enabled){background=enabled;web.backgroundEnabled=enabled;getSharedPreferences("noir",0).edit().putBoolean("web_background",enabled).apply();if(enabled)ensureForeground();else if(foreground){stopForeground(STOP_FOREGROUND_REMOVE);foreground=false;}}
 void setFiltering(boolean enabled){filtering=enabled;installFilter();getSharedPreferences("noir",0).edit().putBoolean("web_filter",enabled).apply();if(!gone)web.reload();}
 void suspendForNative(){command("pause");selected=false;session.setActive(false);stopForeground(STOP_FOREGROUND_REMOVE);foreground=false;}
 PendingIntent action(String action){return PendingIntent.getService(this,action.hashCode(),new Intent(this,WebPlaybackService.class).setAction(action),PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_UPDATE_CURRENT);}
 Notification notification(){PendingIntent open=PendingIntent.getActivity(this,2,new Intent(this,YouTubeActivity.class),PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_UPDATE_CURRENT);return new Notification.Builder(this,"web-playback").setSmallIcon(android.R.drawable.ic_media_play).setContentTitle(title).setContentText(background?"Web audio • Background enabled":"Web audio").setContentIntent(open).setVisibility(Notification.VISIBILITY_PUBLIC).setOngoing(playing).addAction(new Notification.Action.Builder(android.R.drawable.ic_media_rew,"Back 10 seconds",action("back")).build()).addAction(new Notification.Action.Builder(playing?android.R.drawable.ic_media_pause:android.R.drawable.ic_media_play,playing?"Pause":"Play",action(playing?"pause":"play")).build()).addAction(new Notification.Action.Builder(android.R.drawable.ic_media_ff,"Forward 10 seconds",action("forward")).build()).addAction(new Notification.Action.Builder(android.R.drawable.ic_menu_close_clear_cancel,"Stop",action("stop")).build()).setStyle(new Notification.MediaStyle().setMediaSession(session.getSessionToken()).setShowActionsInCompactView(0,1,2)).build();}
 public IBinder onBind(Intent intent){return binder;}
 public void onDestroy(){handler.removeCallbacks(poll);if(filterScript!=null){try{filterScript.remove();}catch(RuntimeException ignored){}}if(serviceWorkers!=null)serviceWorkers.setServiceWorkerClient(null);unregisterReceiver(noisy);if(web!=null&&!gone){web.evaluateJavascript("document.querySelectorAll('video,audio').forEach(function(v){v.pause();})",null);if(web.getParent() instanceof ViewGroup)((ViewGroup)web.getParent()).removeView(web);web.destroy();}session.release();current=null;super.onDestroy();}
}
