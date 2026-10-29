package com.noir.player;

import android.media.AudioManager;
import android.content.Context;
import android.os.*;
import android.view.*;
import java.time.Duration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk=28,qualifiers="w360dp-h800dp-xxhdpi",shadows=YouTubeScreenTest.RecordingSession.class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class VideoGestureTest {
 public static class Playback extends PlayerUiImprovementTest.UiPlayback {
  int seeks,toggles;
  @Override public void skip(int amount){delta+=amount;offset=Math.max(0,Math.min(duration(),offset+amount));seeks++;}
  @Override public void toggle(){toggles++;super.toggle();}
 }
 void setup(Playback s,VideoPane p){new PlayerUiImprovementTest().fixture(s);p.setSizingMode(VideoGeometry.FIT);p.render();PlayerUiImprovementTest.layout(p,1080,2200);}
 @Test public void rightSwipeChangesOnlyMusicVolumeAndLeftSwipeChangesOnlyPlayerBrightness(){
  var sc=Robolectric.buildService(Playback.class).create();try(var ac=Robolectric.buildActivity(VideoActivity.class).setup()){
   VideoPane p=ac.get().pane;Playback s=sc.get();setup(s,p);AudioManager audio=(AudioManager)ac.get().getSystemService(Context.AUDIO_SERVICE);
   int max=audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);audio.setStreamVolume(AudioManager.STREAM_MUSIC,max/2,0);
   int ring=audio.getStreamVolume(AudioManager.STREAM_RING);float original=ac.get().getWindow().getAttributes().screenBrightness;
   swipe(p,900,1100,900,500);int louder=audio.getStreamVolume(AudioManager.STREAM_MUSIC);assertTrue(louder>max/2);
   assertEquals(ring,audio.getStreamVolume(AudioManager.STREAM_RING));assertEquals(original,ac.get().getWindow().getAttributes().screenBrightness,0);
   swipe(p,900,500,900,2000);assertTrue(audio.getStreamVolume(AudioManager.STREAM_MUSIC)<louder);
   swipe(p,150,1100,150,500);float brighter=ac.get().getWindow().getAttributes().screenBrightness;assertTrue(brighter>0&&brighter<=1);
   int volume=audio.getStreamVolume(AudioManager.STREAM_MUSIC);swipe(p,150,500,150,2100);float darker=ac.get().getWindow().getAttributes().screenBrightness;assertTrue(darker<brighter&&darker>=.01f);assertEquals(volume,audio.getStreamVolume(AudioManager.STREAM_MUSIC));
   assertTrue(p.sizingStatus.getText().toString().startsWith("Brightness"));assertEquals(0,s.seeks);assertEquals(0,s.toggles);
   ac.pause();assertEquals(original,ac.get().getWindow().getAttributes().screenBrightness,0);
  }finally{sc.destroy();}
 }
 @Test public void doubleTapsSeekTenSecondsWhileSingleTapOnlyTogglesOverlay(){
  var sc=Robolectric.buildService(Playback.class).create();try(var ac=Robolectric.buildActivity(VideoActivity.class).setup()){
   VideoPane p=ac.get().pane;Playback s=sc.get();setup(s,p);p.setControlsVisible(false);
   tap(p,850,800);idle(100);tap(p,850,800);idle(350);assertEquals(1,s.seeks);assertEquals(72000,s.position());assertEquals("+10 seconds",p.sizingStatus.getText());assertTrue(s.running);
   idle(350);tap(p,150,800);idle(100);tap(p,150,800);idle(350);assertEquals(2,s.seeks);assertEquals(62000,s.position());assertEquals("−10 seconds",p.sizingStatus.getText());
   idle(350);boolean visible=p.controlsVisible;tap(p,540,700);idle(350);assertEquals(!visible,p.controlsVisible);assertEquals(2,s.seeks);assertEquals(0,s.toggles);
   p.setControlsVisible(true);p.play.performClick();assertEquals(1,s.toggles);assertFalse(s.running);
  }finally{sc.destroy();}
 }
 @Test public void bottomControlsAndSmallSizingPanelFitPortraitAndLandscape()throws Exception{
  var sc=Robolectric.buildService(Playback.class).create();try(var ac=Robolectric.buildActivity(VideoActivity.class).setup()){
   VideoPane p=ac.get().pane;setup(sc.get(),p);
   for(int[] size:new int[][]{{1080,2200},{2400,1080}}){PlayerUiImprovementTest.layout(p,size[0],size[1]);int[] xy=new int[2];p.play.getLocationOnScreen(xy);assertTrue(xy[1]>size[1]-p.dp(170));assertTrue(p.play.getHeight()>=p.dp(48));assertTrue(p.seek.getBottom()<=((View)p.seek.getParent()).getHeight());}
   PlayerUiImprovementTest.save(p,1080,2200,"video-bottom-v1.13.png");PlayerUiImprovementTest.save(p,2400,1080,"video-bottom-landscape-v1.13.png");
   p.fit.performClick();VideoSizingSheet sheet=p.sizingSheet;assertTrue(sheet.getWindow().getAttributes().width<=p.dp(300));
   View content=sheet.getWindow().getDecorView();content.measure(View.MeasureSpec.makeMeasureSpec(p.dp(300),View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(p.dp(250),View.MeasureSpec.AT_MOST));assertTrue(content.getMeasuredHeight()<=p.dp(170));
   assertTrue(sheet.zoomIn.getMeasuredHeight()>=p.dp(48));PlayerUiImprovementTest.save(content,p.dp(300),content.getMeasuredHeight(),"video-size-compact-v1.13.png");sheet.dismiss();
  }finally{sc.destroy();}
 }
 @Test public void zoomedVerticalSideGestureAdjustsVolumeAndCentralDragPansWithoutSeeking(){
  var sc=Robolectric.buildService(Playback.class).create();try(var ac=Robolectric.buildActivity(VideoActivity.class).setup()){
   VideoPane p=ac.get().pane;setup(sc.get(),p);p.setSizingMode(VideoGeometry.STRETCH);p.changeZoom(2);p.setControlsVisible(false);
   swipe(p,500,1100,650,1400);assertTrue(Math.abs(p.panX)>.01f||Math.abs(p.panY)>.01f);assertEquals(0,sc.get().seeks);
   AudioManager audio=(AudioManager)ac.get().getSystemService(Context.AUDIO_SERVICE);int max=audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);audio.setStreamVolume(AudioManager.STREAM_MUSIC,max/2,0);float x=p.panX,y=p.panY;
   swipe(p,950,1100,950,500);assertTrue(audio.getStreamVolume(AudioManager.STREAM_MUSIC)>max/2);assertEquals(x,p.panX,.001f);assertEquals(y,p.panY,.001f);assertEquals(0,sc.get().seeks);
  }finally{sc.destroy();}
 }
 static void idle(long ms){Shadows.shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(ms));}
 static void tap(VideoPane p,float x,float y){long t=SystemClock.uptimeMillis();event(p,t,MotionEvent.ACTION_DOWN,x,y);idle(30);event(p,t,MotionEvent.ACTION_UP,x,y);}
 static void swipe(VideoPane p,float x,float y,float endX,float endY){long t=SystemClock.uptimeMillis();event(p,t,MotionEvent.ACTION_DOWN,x,y);idle(40);event(p,t,MotionEvent.ACTION_MOVE,endX,endY);idle(30);event(p,t,MotionEvent.ACTION_UP,endX,endY);idle(350);}
 static void event(VideoPane p,long down,int action,float x,float y){MotionEvent event=MotionEvent.obtain(down,SystemClock.uptimeMillis(),action,x,y,0);try{p.dispatchTouchEvent(event);}finally{event.recycle();}}
}
