package com.noir.player;

import android.app.*;
import android.os.*;
import android.content.*;
import android.graphics.*;
import android.view.*;
import android.webkit.*;
import android.widget.*;
import java.net.*;
import java.io.*;
import org.json.*;

/** Official embedded playback. Never extracts audio or continues hidden playback. */
public class YouTubeActivity extends Activity {
 WebView web; Bitmap thumbnail; String id; TextView title; boolean dotMode;
 protected void onCreate(Bundle b){super.onCreate(b);id=getIntent().getStringExtra("id");if(id==null||!id.matches("[A-Za-z0-9_-]{11}")){finish();return;}
  if(PlaybackService.current!=null)PlaybackService.current.pause();
  dotMode=getSharedPreferences("noir",0).getBoolean("dots",false);
  LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(20,12,20,12);root.setBackgroundColor(Color.BLACK);
  title=new TextView(this);title.setText("YouTube • Loading video details…");title.setTextColor(Color.WHITE);title.setTextSize(18);root.addView(title);
  View art=new View(this){Paint paint=new Paint(3);RectF bounds=new RectF();protected void onDraw(Canvas c){if(thumbnail==null)return;float scale=Math.min(getWidth()/(float)thumbnail.getWidth(),getHeight()/(float)thumbnail.getHeight());float w=thumbnail.getWidth()*scale,h=thumbnail.getHeight()*scale,left=(getWidth()-w)/2,top=(getHeight()-h)/2;if(!dotMode){bounds.set(left,top,left+w,top+h);c.drawBitmap(thumbnail,null,bounds,paint);return;}for(int y=0;y<thumbnail.getHeight();y+=4)for(int x=0;x<thumbnail.getWidth();x+=4){int v=thumbnail.getPixel(x,y);int light=(Color.red(v)*54+Color.green(v)*183+Color.blue(v)*19)/256;if(light<20)continue;paint.setColor(Color.rgb(light,light,light));c.drawCircle(left+x*scale,top+y*scale,Math.max(1,scale*1.4f),paint);}}};root.addView(art,new LinearLayout.LayoutParams(-1,(int)(180*getResources().getDisplayMetrics().density)));
  TextView note=new TextView(this);note.setText("YouTube video plays below. Audio-only and screen-off playback are available for local files and direct streams, not this embedded player.");note.setTextColor(0xffcccccc);root.addView(note);
  web=new WebView(this);web.setBackgroundColor(Color.BLACK);web.getSettings().setJavaScriptEnabled(true);web.getSettings().setDomStorageEnabled(true);web.getSettings().setMediaPlaybackRequiresUserGesture(true);web.getSettings().setAllowFileAccess(false);web.getSettings().setAllowContentAccess(false);web.setWebChromeClient(new WebChromeClient());
  web.setWebViewClient(new WebViewClient(){public boolean shouldOverrideUrlLoading(WebView v,WebResourceRequest r){if(r.isForMainFrame()){String h=r.getUrl().getHost();if(h!=null&&(h.equals("www.youtube.com")||h.equals("www.youtube-nocookie.com")))return false;return true;}return false;}});
  root.addView(web,new LinearLayout.LayoutParams(-1,0,1));
  Button open=new Button(this);open.setText("Open in YouTube");open.setOnClickListener(v->startActivity(new Intent(Intent.ACTION_VIEW,android.net.Uri.parse("https://www.youtube.com/watch?v="+id))));root.addView(open);setContentView(root);
  String html="<html><head><meta name='viewport' content='width=device-width,initial-scale=1'><meta name='referrer' content='strict-origin-when-cross-origin'></head><body style='margin:0;background:black'><iframe width='100%' height='100%' src='https://www.youtube.com/embed/"+id+"?playsinline=1&enablejsapi=1' title='YouTube video player' frameborder='0' allow='encrypted-media; picture-in-picture' referrerpolicy='strict-origin-when-cross-origin' allowfullscreen></iframe></body></html>";
  web.loadDataWithBaseURL("https://github.com/sduos4029-CODE/Audio_video-player-/",html,"text/html","UTF-8",null);
  new Thread(()->{try{String endpoint="https://www.youtube.com/oembed?format=json&url="+URLEncoder.encode("https://www.youtube.com/watch?v="+id,"UTF-8");JSONObject info=new JSONObject(read(endpoint));String thumb=info.getString("thumbnail_url");URL url=new URL(thumb);if(!url.getProtocol().equals("https")||!url.getHost().endsWith(".ytimg.com"))return;URLConnection conn=url.openConnection();conn.setConnectTimeout(10000);conn.setReadTimeout(10000);try(InputStream in=conn.getInputStream()){thumbnail=BitmapFactory.decodeStream(in);}runOnUiThread(()->{title.setText(info.optString("title","YouTube video"));art.invalidate();});}catch(Exception e){runOnUiThread(()->title.setText("YouTube • If playback is unavailable, open in YouTube"));}}).start();
 }
 String read(String url)throws Exception{URLConnection c=new URL(url).openConnection();c.setConnectTimeout(10000);c.setReadTimeout(10000);try(InputStream in=c.getInputStream();ByteArrayOutputStream out=new ByteArrayOutputStream()){byte[] b=new byte[4096];int n;while((n=in.read(b))!=-1){if(out.size()>1024*1024)throw new IOException("Response too large");out.write(b,0,n);}return out.toString("UTF-8");}}
 protected void onPause(){super.onPause();web.onPause();web.loadUrl("javascript:document.querySelector('iframe').contentWindow.postMessage('{\"event\":\"command\",\"func\":\"pauseVideo\",\"args\":[]}', '*');");}
 protected void onResume(){super.onResume();if(web!=null)web.onResume();}
 protected void onDestroy(){if(web!=null){web.destroy();}super.onDestroy();}
}
