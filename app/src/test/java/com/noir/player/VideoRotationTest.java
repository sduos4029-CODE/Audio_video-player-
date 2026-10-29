package com.noir.player;

import android.content.res.Configuration;
import android.net.Uri;
import android.view.*;
import androidx.media3.common.*;
import androidx.media3.exoplayer.ExoPlayer;
import java.lang.reflect.*;
import java.util.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk=28,qualifiers="w360dp-h800dp-xxhdpi",shadows=YouTubeScreenTest.RecordingSession.class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class VideoRotationTest {
 // Recording the ExoPlayer interface verifies lifecycle calls without claiming
 // to exercise a phone's codec, TextureView compositor or frame pacing.
 static class Output implements InvocationHandler {
  TextureView view;int attaches,clears,plays,pauses,releases;long position=62000;
  boolean playWhenReady;TrackSelectionParameters selection=TrackSelectionParameters.DEFAULT_WITHOUT_CONTEXT;
  Player.Listener listener;
  final ExoPlayer player=(ExoPlayer)Proxy.newProxyInstance(ExoPlayer.class.getClassLoader(),new Class<?>[]{ExoPlayer.class},this);
  public Object invoke(Object proxy,Method method,Object[] args){
   switch(method.getName()){
    case "setVideoTextureView":view=(TextureView)args[0];attaches++;return null;
    case "clearVideoTextureView":if(view==args[0])view=null;clears++;return null;
    case "getTrackSelectionParameters":return selection;
    case "setTrackSelectionParameters":selection=(TrackSelectionParameters)args[0];return null;
    case "addListener":listener=(Player.Listener)args[0];return null;
    case "getCurrentPosition":return position;
    case "getDuration":return 242000L;
    case "getPlayWhenReady":case "isPlaying":return playWhenReady;
    case "play":playWhenReady=true;plays++;return null;
    case "pause":playWhenReady=false;pauses++;return null;
    case "release":releases++;return null;
    case "toString":return "Recording video output";
    case "equals":return proxy==args[0];
    case "hashCode":return System.identityHashCode(proxy);
   }
   Class<?> type=method.getReturnType();if(type==boolean.class)return false;if(type==int.class)return 0;if(type==long.class)return 0L;if(type==float.class)return 0f;return null;
  }
 }
 public static class RecordingPlayback extends PlaybackService {
  final List<Output> outputs=new ArrayList<>();
  @Override ExoPlayer createPlayer(){Output out=new Output();outputs.add(out);return out.player;}
  @Override public void play(){if(player!=null)player.play();}
 }
 @Test public void rotationKeepsTexturePlayerPositionAndPlayIntentWithoutToggle(){
  var sc=Robolectric.buildService(RecordingPlayback.class).create();RecordingPlayback s=sc.get();s.load(Uri.parse("https://example.org/fixture.mp4"),"Fixture");s.hasVideo=true;
  try(var ac=Robolectric.buildActivity(VideoActivity.class).setup()){
   VideoPane p=ac.get().pane;PlayerUiImprovementTest.layout(p,1080,2200);Output out=s.outputs.get(0);
   assertSame(p.texture,out.view);assertTrue(s.isVideoSurfaceOwner(p.owner));assertFalse(out.selection.disabledTrackTypes.contains(C.TRACK_TYPE_VIDEO));int count=out.attaches;
   p.rotate.performClick();RuntimeEnvironment.setQualifiers("w800dp-h360dp-land-xxhdpi");ac.get().onConfigurationChanged(new Configuration(ac.get().getResources().getConfiguration()));PlayerUiImprovementTest.layout(p,2400,1080);p.render();
   assertSame(p.texture,out.view);assertEquals(count,out.attaches);assertEquals(0,out.clears);assertEquals(0,out.pauses);assertEquals(1,out.plays);assertEquals(62000,s.position());assertTrue(s.playing());assertEquals(1,s.outputs.size());
   out.listener.onPlaybackStateChanged(Player.STATE_BUFFERING);out.listener.onPlaybackStateChanged(Player.STATE_READY);assertEquals(1,out.plays);assertTrue(s.playing());
   ac.pause();assertNull(out.view);assertTrue(out.selection.disabledTrackTypes.contains(C.TRACK_TYPE_VIDEO));assertTrue(s.playing());
  }finally{sc.destroy();}
 }
 @Test public void switchingViewsAndReplacingDecoderUseCurrentTextureAndRejectStaleRelease(){
  var sc=Robolectric.buildService(RecordingPlayback.class).create();RecordingPlayback s=sc.get();s.load(Uri.parse("https://example.org/first.mp4"),"First");
  try{
   Object first=new Object(),second=new Object();TextureView home=new TextureView(s),full=new TextureView(s);Output old=s.outputs.get(0);
   s.attachVideoTextureView(first,home);s.attachVideoTextureView(second,full);assertSame(full,old.view);assertFalse(s.releaseVideoSurface(first));assertSame(full,old.view);
   s.load(Uri.parse("https://example.org/second.mp4"),"Second");assertEquals(1,old.releases);Output next=s.outputs.get(1);assertSame(full,next.view);assertEquals(1,next.attaches);assertTrue(s.isVideoSurfaceOwner(second));
   assertTrue(s.releaseVideoSurface(second));assertNull(next.view);assertEquals(1,next.clears);
  }finally{sc.destroy();}
 }
 @Test public void bottomRightSizingButtonOpensMenuAndHasDistinctModeIcons()throws Exception{
  var sc=Robolectric.buildService(PlayerUiImprovementTest.UiPlayback.class).create();new PlayerUiImprovementTest().fixture(sc.get());
  try(var ac=Robolectric.buildActivity(VideoActivity.class).setup()){
   VideoPane p=ac.get().pane;
   for(int[] size:new int[][]{{1080,2200},{2400,1080}}){PlayerUiImprovementTest.layout(p,size[0],size[1]);int[] xy=new int[2];p.fit.getLocationOnScreen(xy);assertTrue(xy[0]>size[0]-p.dp(80));assertTrue(xy[1]>size[1]-p.dp(100));assertEquals(p.dp(48),p.fit.getHeight());int[] play=new int[2];p.play.getLocationOnScreen(play);assertTrue(play[0]+p.play.getWidth()<=xy[0]);}
   p.fit.performClick();assertTrue(p.sizingSheet.isShowing());
   int[] modes={VideoGeometry.FIT,VideoGeometry.CROP,VideoGeometry.STRETCH};PlayerIcons.Icon[] icons={PlayerIcons.Icon.RESIZE_FIT,PlayerIcons.Icon.RESIZE_CROP,PlayerIcons.Icon.RESIZE_STRETCH};
   for(int i=0;i<modes.length;i++){p.sizingSheet.choices[modes[i]].performClick();assertEquals(icons[i],((PlayerIcons)p.fit.getDrawable()).icon);}
   p.sizingSheet.dismiss();p.setSizingMode(VideoGeometry.FIT);
   PlayerUiImprovementTest.save(p,2400,1080,"video-sizing-corner-landscape-v1.14.png");PlayerUiImprovementTest.save(p,1080,2200,"video-sizing-corner-portrait-v1.14.png");
  }finally{sc.destroy();}
 }
}
