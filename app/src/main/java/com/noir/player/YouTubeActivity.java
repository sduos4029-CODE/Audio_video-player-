package com.noir.player;

import android.app.*;
import android.os.*;
import android.content.*;
import android.graphics.*;
import android.view.*;
import android.widget.*;
import java.net.*;
import java.io.*;
import org.json.*;

/** Native preview and official-app handoff; no embedded player. */
public class YouTubeActivity extends Activity {
 Bitmap thumbnail; String id; TextView title; boolean dotMode;
 protected void onCreate(Bundle b){super.onCreate(b);id=getIntent().getStringExtra("id");if(id==null||!id.matches("[A-Za-z0-9_-]{11}")){finish();return;}
  dotMode=getSharedPreferences("noir",0).getBoolean("dots",false);
  LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(20,12,20,12);root.setBackgroundColor(Color.BLACK);
  title=new TextView(this);title.setText("YouTube • Loading video details…");title.setTextColor(Color.WHITE);title.setTextSize(18);root.addView(title);
  View art=new View(this){Paint paint=new Paint(3);RectF bounds=new RectF();protected void onDraw(Canvas c){if(thumbnail==null)return;float scale=Math.min(getWidth()/(float)thumbnail.getWidth(),getHeight()/(float)thumbnail.getHeight());float w=thumbnail.getWidth()*scale,h=thumbnail.getHeight()*scale,left=(getWidth()-w)/2,top=(getHeight()-h)/2;if(!dotMode){bounds.set(left,top,left+w,top+h);c.drawBitmap(thumbnail,null,bounds,paint);return;}for(int y=0;y<thumbnail.getHeight();y+=4)for(int x=0;x<thumbnail.getWidth();x+=4){int v=thumbnail.getPixel(x,y);int light=(Color.red(v)*54+Color.green(v)*183+Color.blue(v)*19)/256;if(light<20)continue;paint.setColor(Color.rgb(light,light,light));c.drawCircle(left+x*scale,top+y*scale,Math.max(1,scale*1.4f),paint);}}};root.addView(art,new LinearLayout.LayoutParams(-1,(int)(180*getResources().getDisplayMetrics().density)));
  TextView note=new TextView(this);note.setText("Choose an app below to play. Playback uses that app and your account; ads and Premium features follow its settings.");note.setTextColor(0xffcccccc);root.addView(note);
  Button music=new Button(this);music.setText("Play in YouTube Music");music.setOnClickListener(v->open(true));root.addView(music);
  Button video=new Button(this);video.setText("Play in YouTube");video.setOnClickListener(v->open(false));root.addView(video);
  setContentView(root);
  new Thread(()->{try{String endpoint="https://www.youtube.com/oembed?format=json&url="+URLEncoder.encode("https://www.youtube.com/watch?v="+id,"UTF-8");JSONObject info=new JSONObject(read(endpoint));String thumb=info.getString("thumbnail_url");URL url=new URL(thumb);if(!url.getProtocol().equals("https")||!url.getHost().endsWith(".ytimg.com"))return;URLConnection conn=url.openConnection();conn.setConnectTimeout(10000);conn.setReadTimeout(10000);try(InputStream in=conn.getInputStream()){thumbnail=BitmapFactory.decodeStream(in);}runOnUiThread(()->{title.setText(info.optString("title","YouTube video"));art.invalidate();});}catch(Exception e){runOnUiThread(()->title.setText("YouTube • Choose an app below to play"));}}).start();
 }
 String read(String url)throws Exception{URLConnection c=new URL(url).openConnection();c.setConnectTimeout(10000);c.setReadTimeout(10000);try(InputStream in=c.getInputStream();ByteArrayOutputStream out=new ByteArrayOutputStream()){byte[] b=new byte[4096];int n;while((n=in.read(b))!=-1){if(out.size()>1024*1024)throw new IOException("Response too large");out.write(b,0,n);}return out.toString("UTF-8");}}
 void open(boolean music){if(!YouTubeLauncher.open(this,id,music))new AlertDialog.Builder(this).setTitle("No playback app available").setMessage("Install YouTube, YouTube Music or a browser, then try again.").setPositiveButton("OK",null).show();}
}
