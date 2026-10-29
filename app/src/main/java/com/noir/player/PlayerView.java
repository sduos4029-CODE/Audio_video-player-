package com.noir.player;

import android.view.*;
import android.content.*;
import android.graphics.*;
import android.media.AudioManager;
import java.text.SimpleDateFormat;
import java.util.*;

public class PlayerView extends View {
 final java.util.List<Runnable> dotLabels=new java.util.ArrayList<>(); Canvas dotCanvas; final int[] pixels=new int[720*1480]; final RectF screenRect=new RectF(0,0,360,740); final MainActivity host; final Paint p=new Paint(3); Bitmap frame; Canvas scene; int accent,secondary; float scale,offset; boolean dots;
 final Path waveformPath=new Path();float[] dotWave=new float[AudioSpectrum.BANDS];
 final int[] colors={Color.WHITE,0xffff343b,0xffffd43b,0xff269bff,0xffbf59ff,0xffff343b,0xff269bff};
 public PlayerView(Context context){super(context);host=(MainActivity)context;setContentDescription("Pulse Media Player audio player. Tap top right for themes, bottom left for library, bottom right for online audio.");setFocusable(true);}
 void color(int c){p.setColor(c);p.setStyle(Paint.Style.FILL);p.setStrokeWidth(dots?2.5f:1.5f);}
 void text(Canvas c,String s,float x,float y,int size,int col){
  if(dots){final float cell=Math.max(size,13)/7f;dotLabels.add(()->DotFont.draw(dotCanvas,p,s,x,y,cell,col));return;}
  color(col);p.setTypeface(Typeface.create("sans-serif",Typeface.NORMAL));p.setTextSize(size);c.drawText(s,x,y,p);
 }
 void centered(Canvas c,String s,float y,int size,int col){
  if(dots){final float cell=Math.min(Math.max(size,13)/7f,320f/Math.max(1,s.length()*6-1));final float x=180-DotFont.width(s,cell)/2;dotLabels.add(()->DotFont.draw(dotCanvas,p,s,x,y,cell,col));return;}
  p.setTypeface(Typeface.create("sans-serif",Typeface.NORMAL));int fitted=size;p.setTextSize(fitted);while(p.measureText(s)>320&&fitted>9)p.setTextSize(--fitted);color(col);c.drawText(s,180-p.measureText(s)/2,y,p);
 }
 void line(Canvas c,float x,float y,float xx,float yy,int col,float width){color(col);p.setStrokeWidth(width);p.setStrokeCap(Paint.Cap.ROUND);c.drawLine(x,y,xx,yy,p);}
 void ring(Canvas c,float x,float y,float r,int col){color(col);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(dots?2.5f:1.5f);c.drawCircle(x,y,r,p);p.setStyle(Paint.Style.FILL);}
 String time(int ms){return String.format(Locale.US,"%d:%02d",ms/60000,(ms/1000)%60);}
 protected void onDraw(Canvas output){super.onDraw(output);PlayerPalette palette=PlayerPalette.forIndex(host.prefs.getInt("theme",5));accent=palette.primary;secondary=palette.secondary;dots=host.prefs.getBoolean("dots",false);
  if(frame==null) {frame=Bitmap.createBitmap(720,1480,Bitmap.Config.ARGB_8888);scene=new Canvas(frame);}frame.eraseColor(Color.BLACK);dotLabels.clear();scene.save();scene.scale(2,2);drawScene(scene);scene.restore();
  output.drawColor(Color.BLACK);scale=Math.min(getWidth()/360f,getHeight()/740f);offset=(getWidth()-360*scale)/2;output.save();output.translate(offset,(getHeight()-740*scale)/2);output.scale(scale,scale);
  if(dots){
   frame.getPixels(pixels,0,720,0,0,720,1480);
   for(int y=0;y<1480;y+=4)for(int x=0;x<720;x+=4){int col=DotSampler.color(pixels,720,1480,x,y,4);if((col&0x00ffffff)!=0){color(col);output.drawCircle((x+2)/2f,(y+2)/2f,.72f,p);}}
   dotCanvas=output;for(Runnable label:dotLabels)label.run();WaveformPainter.dots(output,p,dotWave,25,449,310,28,secondary);
  }else output.drawBitmap(frame,null,screenRect,p);
  output.restore();postInvalidateDelayed(playing()?100:500);
 }
 boolean playing(){return PlaybackService.current!=null&&PlaybackService.current.playing();}
 void drawScene(Canvas c){PlaybackService s=PlaybackService.current;boolean lock=host.prefs.getBoolean("lock",false);centered(c,"P U L S E",35,16,Color.WHITE);text(c,"≡",320,35,22,accent);
  if(lock){centered(c,ClockText.format(new Date(),android.text.format.DateFormat.is24HourFormat(host),TimeZone.getDefault()),76,30,Color.WHITE);centered(c,"LOCK-SCREEN PLAYER",96,9,0xff999999);}else{centered(c,"YOUR SOUND. YOUR SPACE.",75,10,0xff888888);}
  RectF art=new RectF(62,111,298,347);color(accent);p.setStyle(Paint.Style.STROKE);c.drawRoundRect(art,8,8,p);p.setStyle(Paint.Style.FILL);
  if(s!=null&&s.artwork!=null){c.drawBitmap(s.artwork,null,new RectF(66,115,294,343),p);}else{c.save();c.clipRect(66,115,294,343);color(0xffc8c8c8);c.drawCircle(180,185,43,p);color(0xff111111);c.drawCircle(198,173,37,p);for(int j=0;j<22;j++){float y=249+j*4;for(int x=68;x<292;x+=3){float wave=(float)Math.sin(x*.05+j*.72)*2;line(c,x,y+wave,x+1,y+wave,j%3==0?secondary:0xff505050,1);}}c.restore();}
  String title=s==null?"Choose your soundtrack":s.title;if(title.length()>28)title=title.substring(0,27)+"…";centered(c,title,383,20,Color.WHITE);centered(c,s==null?"OPEN AUDIO OR VIDEO":s.loading?"LOADING…":s.audioOnly?"AUDIO ONLY":"VIDEO MODE",405,10,0xff999999);
  dotWave=s!=null&&s.playing()?s.frequencyBands():new float[AudioSpectrum.BANDS];if(!dots)WaveformPainter.smooth(c,p,waveformPath,s!=null&&s.playing()?s.waveformSamples():new float[AudioWaveform.POINTS],25,449,310,28,secondary,1.5f);

  int duration=s==null?0:s.duration(),position=s==null?0:s.position();float fraction=duration>0?position/(float)duration:0;line(c,25,493,335,493,0xff303030,3);line(c,25,493,25+310*fraction,493,secondary,3);color(secondary);c.drawCircle(25+310*fraction,493,5,p);text(c,time(position),25,517,15,Color.WHITE);text(c,time(duration),282,517,15,Color.WHITE);
  boolean filled=host.prefs.getBoolean("filled",true);if(filled){color(accent);c.drawCircle(180,566,36,p);}else ring(c,180,566,36,accent);int glyph=filled?PlayerPalette.forIndex(host.prefs.getInt("theme",5)).filledIcon:accent;
  if(playing()){line(c,173,555,173,577,glyph,5);line(c,187,555,187,577,glyph,5);}else{color(glyph);Path path=new Path();path.moveTo(173,553);path.lineTo(173,579);path.lineTo(194,566);path.close();c.drawPath(path,p);}
  ring(c,102,566,21,Color.WHITE);text(c,"-10",85,572,14,Color.WHITE);ring(c,258,566,21,Color.WHITE);text(c,"+10",241,572,14,Color.WHITE);trackIcon(c,39,false,s!=null&&s.hasPreviousTrack());trackIcon(c,321,true,s!=null&&s.hasNextTrack());
  AudioManager audio=(AudioManager)host.getSystemService(Context.AUDIO_SERVICE);float volume=audio.getStreamVolume(AudioManager.STREAM_MUSIC)/(float)audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);text(c,"VOL",25,633,9,0xff999999);line(c,60,630,335,630,0xff303030,3);line(c,60,630,60+275*volume,630,secondary,3);color(secondary);c.drawCircle(60+275*volume,630,4,p);
  text(c,"LIBRARY",25,688,11,Color.WHITE);centered(c,dots?"DOT MATRIX":"NORMAL",688,10,secondary);text(c,"ONLINE",285,688,11,Color.WHITE);
  if(s!=null&&!s.error.isEmpty())centered(c,"TAP HERE FOR ERROR DETAILS",717,10,accent);else centered(c,lock?"LOCK MODE ON":"TAP ARTWORK FOR VIDEO",717,9,0xff777777);
 }
 void trackIcon(Canvas c,float x,boolean next,boolean enabled){int col=enabled?Color.WHITE:0xff555555;int direction=next?1:-1;color(col);Path triangle=new Path();triangle.moveTo(x-direction*6,556);triangle.lineTo(x-direction*6,576);triangle.lineTo(x+direction*6,566);triangle.close();c.drawPath(triangle,p);line(c,x+direction*10,556,x+direction*10,576,col,2);text(c,next?"NEXT":"PREV",x-16,607,8,col);}
 public boolean onTouchEvent(android.view.MotionEvent e){if(e.getAction()!=MotionEvent.ACTION_UP)return true;float x=(e.getX()-offset)/scale,y=(e.getY()-(getHeight()-740*scale)/2)/scale;PlaybackService s=PlaybackService.current;if(y>700&&s!=null&&!s.error.isEmpty()){new android.app.AlertDialog.Builder(host).setTitle("Playback error").setMessage(s.error).setPositiveButton("OK",null).show();}else if(y>110&&y<350&&s!=null){host.startActivity(new Intent(host,VideoActivity.class));}else if(y<50){if(x>280)host.settings();else host.prefs.edit().putBoolean("dots",!dots).apply();}else if(y>655&&y<705){if(x<120)host.library();else if(x>250)host.online();else host.prefs.edit().putBoolean("dots",!dots).apply();}else if(y>527&&y<607){if(s==null){host.library();}else if(x<66)s.previousTrack();else if(x<138)s.skip(-10000);else if(x>294)s.nextTrack();else if(x>222)s.skip(10000);else s.toggle();}else if(y>480&&y<523&&s!=null)s.seek((int)(Math.max(0,Math.min(1,(x-25)/310))*s.duration()));else if(y>613&&y<648){AudioManager a=(AudioManager)host.getSystemService(Context.AUDIO_SERVICE);a.setStreamVolume(AudioManager.STREAM_MUSIC,(int)(Math.max(0,Math.min(1,(x-60)/275))*a.getStreamMaxVolume(AudioManager.STREAM_MUSIC)),0);}host.refreshPlayerStyle();invalidate();performClick();return true;}
 public boolean performClick(){super.performClick();return true;}
}
