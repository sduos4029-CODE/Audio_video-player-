package com.noir.player;

import android.view.*;
import android.content.*;
import android.graphics.*;
import android.media.AudioManager;
import java.text.SimpleDateFormat;
import java.util.*;

public class PlayerView extends View {
 final RectF screenRect=new RectF(0,0,360,740); final MainActivity host; final Paint p=new Paint(3); Bitmap frame; Canvas scene; int accent,secondary; float scale,offset; boolean dots;
 final int[] colors={Color.WHITE,0xffff343b,0xffffd43b,0xff269bff,0xffbf59ff,0xffff343b,0xff269bff};
 public PlayerView(Context context){super(context);host=(MainActivity)context;setContentDescription("Pulse Media Player audio player. Tap top right for themes, bottom left for library, bottom right for online audio.");setFocusable(true);}
 void color(int c){p.setColor(c);p.setStyle(Paint.Style.FILL);p.setStrokeWidth(1.5f);}
 void text(Canvas c,String s,float x,float y,int size,int col){color(col);p.setTypeface(Typeface.create("sans-serif",Typeface.NORMAL));p.setTextSize(size);c.drawText(s,x,y,p);}
 void centered(Canvas c,String s,float y,int size,int col){p.setTextSize(size);text(c,s,180-p.measureText(s)/2,y,size,col);}
 void line(Canvas c,float x,float y,float xx,float yy,int col,float width){color(col);p.setStrokeWidth(width);p.setStrokeCap(Paint.Cap.ROUND);c.drawLine(x,y,xx,yy,p);}
 void ring(Canvas c,float x,float y,float r,int col){color(col);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(1.5f);c.drawCircle(x,y,r,p);p.setStyle(Paint.Style.FILL);}
 String time(int ms){return String.format(Locale.US,"%d:%02d",ms/60000,(ms/1000)%60);}
 protected void onDraw(Canvas output){super.onDraw(output);int theme=host.prefs.getInt("theme",5);accent=colors[Math.max(0,Math.min(6,theme))];secondary=theme==5?0xffffd43b:theme==6?0xffbf59ff:accent;dots=host.prefs.getBoolean("dots",false);
  if(frame==null) {frame=Bitmap.createBitmap(720,1480,Bitmap.Config.ARGB_8888);scene=new Canvas(frame);}frame.eraseColor(Color.BLACK);scene.save();scene.scale(2,2);drawScene(scene);scene.restore();
  output.drawColor(Color.BLACK);scale=Math.min(getWidth()/360f,getHeight()/740f);offset=(getWidth()-360*scale)/2;output.save();output.translate(offset,(getHeight()-740*scale)/2);output.scale(scale,scale);
  if(dots){color(Color.BLACK);output.drawRect(0,0,360,740,p);for(int y=0;y<1480;y+=5)for(int x=0;x<720;x+=5){int col=frame.getPixel(x,y);if(Color.red(col)+Color.green(col)+Color.blue(col)>65){color(col);output.drawCircle(x/2f,y/2f,.82f,p);}}}else output.drawBitmap(frame,null,screenRect,p);
  output.restore();postInvalidateDelayed(playing()?100:500);
 }
 boolean playing(){return PlaybackService.current!=null&&PlaybackService.current.playing();}
 void drawScene(Canvas c){PlaybackService s=PlaybackService.current;boolean lock=host.prefs.getBoolean("lock",false);centered(c,"P U L S E",35,16,Color.WHITE);text(c,"≡",320,35,22,accent);
  if(lock){centered(c,new SimpleDateFormat("HH:mm",Locale.getDefault()).format(new Date()),76,30,Color.WHITE);centered(c,"LOCK-SCREEN PLAYER",96,9,0xff999999);}else{centered(c,"YOUR SOUND. YOUR SPACE.",75,10,0xff888888);}
  RectF art=new RectF(62,111,298,347);color(accent);p.setStyle(Paint.Style.STROKE);c.drawRoundRect(art,8,8,p);p.setStyle(Paint.Style.FILL);
  if(s!=null&&s.artwork!=null){c.drawBitmap(s.artwork,null,new RectF(66,115,294,343),p);}else{c.save();c.clipRect(66,115,294,343);color(0xffc8c8c8);c.drawCircle(180,185,43,p);color(0xff111111);c.drawCircle(198,173,37,p);for(int j=0;j<22;j++){float y=249+j*4;for(int x=68;x<292;x+=3){float wave=(float)Math.sin(x*.05+j*.72)*2;line(c,x,y+wave,x+1,y+wave,j%3==0?secondary:0xff505050,1);}}c.restore();}
  String title=s==null?"Choose your soundtrack":s.title;if(title.length()>28)title=title.substring(0,27)+"…";centered(c,title,383,20,Color.WHITE);centered(c,s==null?"LOCAL FILES + HTTPS AUDIO":s.loading?"LOADING…":"P U L S E  /  AUDIO",405,10,0xff999999);
  float t=s==null?0:s.position()/1000f;for(int x=25;x<336;x+=5){float height=playing()?(float)(4+Math.abs(Math.sin(x*.08+t*3)*Math.cos(x*.021-t))*27):3;line(c,x,449-height,x,449+height,secondary,2);}
  int duration=s==null?0:s.duration(),position=s==null?0:s.position();float fraction=duration>0?position/(float)duration:0;line(c,25,493,335,493,0xff303030,3);line(c,25,493,25+310*fraction,493,secondary,3);color(secondary);c.drawCircle(25+310*fraction,493,5,p);text(c,time(position),25,515,11,Color.WHITE);text(c,time(duration),305,515,11,Color.WHITE);
  boolean filled=host.prefs.getBoolean("filled",true);if(filled){color(accent);c.drawCircle(180,566,36,p);}else ring(c,180,566,36,accent);int glyph=filled?Color.BLACK:accent;
  if(playing()){line(c,173,555,173,577,glyph,5);line(c,187,555,187,577,glyph,5);}else{color(glyph);Path path=new Path();path.moveTo(173,553);path.lineTo(173,579);path.lineTo(194,566);path.close();c.drawPath(path,p);}
  ring(c,89,566,21,Color.WHITE);text(c,"−10",76,571,13,Color.WHITE);ring(c,271,566,21,Color.WHITE);text(c,"+10",258,571,13,Color.WHITE);
  AudioManager audio=(AudioManager)host.getSystemService(Context.AUDIO_SERVICE);float volume=audio.getStreamVolume(AudioManager.STREAM_MUSIC)/(float)audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);text(c,"VOL",25,633,9,0xff999999);line(c,60,630,335,630,0xff303030,3);line(c,60,630,60+275*volume,630,secondary,3);color(secondary);c.drawCircle(60+275*volume,630,4,p);
  text(c,"LIBRARY",25,688,11,Color.WHITE);centered(c,dots?"DOT MATRIX":"NORMAL",688,10,secondary);text(c,"ONLINE",285,688,11,Color.WHITE);
  if(s!=null&&!s.error.isEmpty())centered(c,"Playback failed — try another file or URL",717,10,accent);else centered(c,lock?"Player over lock screen • device security stays on":"Tap Pulse Media Player to switch style • menu for themes",717,9,0xff777777);
 }
 public boolean onTouchEvent(android.view.MotionEvent e){if(e.getAction()!=MotionEvent.ACTION_UP)return true;float x=(e.getX()-offset)/scale,y=(e.getY()-(getHeight()-740*scale)/2)/scale;PlaybackService s=PlaybackService.current;if(y<50){if(x>280)host.settings();else host.prefs.edit().putBoolean("dots",!dots).apply();}else if(y>655&&y<705){if(x<120)host.library();else if(x>250)host.online();else host.prefs.edit().putBoolean("dots",!dots).apply();}else if(y>527&&y<607){if(s==null){host.library();}else if(x<125)s.skip(-10000);else if(x>235)s.skip(10000);else s.toggle();}else if(y>480&&y<523&&s!=null)s.seek((int)(Math.max(0,Math.min(1,(x-25)/310))*s.duration()));else if(y>613&&y<648){AudioManager a=(AudioManager)host.getSystemService(Context.AUDIO_SERVICE);a.setStreamVolume(AudioManager.STREAM_MUSIC,(int)(Math.max(0,Math.min(1,(x-60)/275))*a.getStreamMaxVolume(AudioManager.STREAM_MUSIC)),0);}invalidate();performClick();return true;}
 public boolean performClick(){super.performClick();return true;}
}
