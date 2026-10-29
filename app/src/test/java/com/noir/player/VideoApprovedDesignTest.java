package com.noir.player;

import android.graphics.Rect;
import android.net.Uri;
import android.os.Looper;
import android.view.*;
import androidx.media3.common.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk=28,qualifiers="w360dp-h800dp-xxhdpi",shadows=YouTubeScreenTest.RecordingSession.class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class VideoApprovedDesignTest {
 @Test public void oneTapCyclesAllModesWithIconsAndHoldingOpensZoom(){
  var sc=Robolectric.buildService(PlayerUiImprovementTest.UiPlayback.class).create();new PlayerUiImprovementTest().fixture(sc.get());
  try(var ac=Robolectric.buildActivity(VideoActivity.class).setup()){
   VideoPane p=ac.get().pane;p.setSizingMode(VideoGeometry.FIT);
   for(int mode:new int[]{VideoGeometry.CROP,VideoGeometry.STRETCH,VideoGeometry.FIT}){p.fit.performClick();assertEquals(mode,p.sizingMode);assertNull(p.sizingSheet);assertEquals(mode,ac.get().getSharedPreferences("noir",0).getInt("video_sizing_mode",-1));PlayerIcons.Icon icon=((PlayerIcons)p.fit.getDrawable()).icon;assertEquals(mode==VideoGeometry.FIT?PlayerIcons.Icon.RESIZE_FIT:mode==VideoGeometry.CROP?PlayerIcons.Icon.RESIZE_CROP:PlayerIcons.Icon.RESIZE_STRETCH,icon);}
   p.fit.performLongClick();assertTrue(p.sizingSheet.isShowing());p.sizingSheet.dismiss();
  }finally{sc.destroy();}
 }
 @Test public void touchLockBlocksTransportGesturesAndSurvivesRecreationUntilHeldOrBack(){
  var sc=Robolectric.buildService(VideoGestureTest.Playback.class).create();new PlayerUiImprovementTest().fixture(sc.get());
  try(var ac=Robolectric.buildActivity(VideoActivity.class).setup()){
   VideoPane p=ac.get().pane;PlayerUiImprovementTest.layout(p,1080,2200);p.lock.performClick();assertTrue(p.touchLocked);assertEquals(View.INVISIBLE,p.controls.getVisibility());assertEquals(View.VISIBLE,p.lockedOverlay.getVisibility());
   VideoGestureTest.tap(p,850,800);VideoGestureTest.idle(100);VideoGestureTest.tap(p,850,800);VideoGestureTest.swipe(p,950,1200,950,400);VideoGestureTest.tap(p,540,2050);
   assertEquals(0,sc.get().seeks);assertEquals(0,sc.get().toggles);assertEquals(62000,sc.get().position());assertTrue(p.touchLocked);
   p.unlock.performClick();assertTrue(p.touchLocked);ac.recreate();p=ac.get().pane;assertTrue(p.touchLocked);p.unlock.performLongClick();assertFalse(p.touchLocked);assertTrue(p.controlsVisible);
   p.lock.performClick();ac.get().onBackPressed();assertFalse(p.touchLocked);assertFalse(ac.get().isFinishing());
  }finally{sc.destroy();}
 }
 @Test public void foregroundModeChangesReuseOutputDecoderTimelineAndPlayIntent(){
  var sc=Robolectric.buildService(VideoRotationTest.RecordingPlayback.class).create();var s=sc.get();s.load(Uri.parse("https://example.org/video.mp4"),"Fixture");s.hasVideo=true;
  try(var ac=Robolectric.buildActivity(VideoActivity.class).setup()){
   VideoPane p=ac.get().pane;PlayerUiImprovementTest.layout(p,1080,2200);var out=s.outputs.get(0);s.videoFrameReady=true;int attaches=out.attaches,selections=out.selections;
   for(int i=0;i<3;i++){p.mode.performClick();assertTrue(s.audioOnly);assertTrue(s.videoDecodingEnabled);assertTrue(s.videoFrameReady);assertEquals(View.VISIBLE,p.audioCover.getVisibility());assertEquals(View.VISIBLE,p.texture.getVisibility());p.mode.performClick();assertFalse(s.audioOnly);assertTrue(s.videoFrameReady);}
   assertEquals(attaches,out.attaches);assertEquals(selections,out.selections);assertEquals(0,out.clears);assertEquals(1,out.prepares);assertEquals(1,out.plays);assertEquals(0,out.pauses);assertEquals(62000,s.position());
   ac.pause();assertFalse(s.videoDecodingEnabled);assertTrue(out.selection.disabledTrackTypes.contains(C.TRACK_TYPE_VIDEO));assertNull(out.view);assertTrue(s.playing());
  }finally{sc.destroy();}
 }
 @Test public void sketchToggleUsesExistingPerFrameEffectWithoutReprepareOrSeek(){
  var sc=Robolectric.buildService(VideoRotationTest.RecordingPlayback.class).create();var s=sc.get();s.load(Uri.parse("https://example.org/video.mp4"),"Fixture");s.hasVideo=true;
  try(var ac=Robolectric.buildActivity(VideoActivity.class).setup()){
   var out=s.outputs.get(0);assertEquals(1,out.effects.size());assertSame(s.sketchEffect,out.effects.get(0));VideoPane p=ac.get().pane;
   p.sketch.performLongClick();assertTrue(s.sketchEnabled());assertTrue(p.sketch.isSelected());assertTrue(ac.get().getSharedPreferences("noir",0).getBoolean("video_sketch",false));
   p.sketch.performLongClick();assertFalse(s.sketchEnabled());assertEquals(1,out.effectCalls);assertEquals(1,out.prepares);assertEquals(62000,s.position());assertEquals(0,out.pauses);
  }finally{sc.destroy();}
 }
 @Test public void pausedSketchRequestsOneFrameWithoutResuming(){
  var sc=Robolectric.buildService(VideoRotationTest.RecordingPlayback.class).create();var s=sc.get();s.load(Uri.parse("https://example.org/video.mp4"),"Fixture");
  try{var out=s.outputs.get(0);out.player.pause();int plays=out.plays;s.setSketchEnabled(true);assertEquals(1,out.seeks);assertEquals(62000,s.position());assertEquals(plays,out.plays);assertFalse(s.playing());}finally{sc.destroy();}
 }
 @Test public void failedGpuFallsBackOnceToNormalVideoAtPausedPosition(){
  var sc=Robolectric.buildService(VideoRotationTest.RecordingPlayback.class).create();var s=sc.get();s.load(Uri.parse("https://example.org/video.mp4"),"Fixture");var old=s.outputs.get(0);old.player.pause();s.setSketchEnabled(true);
  try{
   old.listener.onPlayerError(new PlaybackException("No GPU context",null,PlaybackException.ERROR_CODE_VIDEO_FRAME_PROCESSOR_INIT_FAILED));Shadows.shadowOf(Looper.getMainLooper()).idle();
   assertEquals(2,s.outputs.size());var next=s.outputs.get(1);assertEquals(0,next.effectCalls);assertEquals(0,next.plays);assertEquals(62000,s.position());assertFalse(s.canSketch());assertFalse(s.sketchEnabled());assertFalse(s.effectNotice.isEmpty());
  }finally{sc.destroy();}
 }
 @Test public void deviceLimitsAndSourceDetailsDoNotClaimUniversalFormats(){
  String failure=PlaybackDetails.failure(new PlaybackException("8K",null,PlaybackException.ERROR_CODE_DECODING_FORMAT_EXCEEDS_CAPABILITIES));assertTrue(failure.contains("device decoder"));
  var output=new VideoRotationTest.Output();output.videoFormat=new Format.Builder().setSampleMimeType("video/hevc").setCodecs("hvc1").setWidth(7680).setHeight(4320).setFrameRate(60).build();String details=PlaybackDetails.describe(output.player,null);assertTrue(details.contains("7680 × 4320"));assertTrue(details.contains("not a playback guarantee"));assertTrue(details.contains("Dolby"));
 }
 @Test public void transportTargetsStaySeparatedAndCircularAtPhoneAndLandscapeSizes()throws Exception{
  var sc=Robolectric.buildService(PlayerUiImprovementTest.UiPlayback.class).create();new PlayerUiImprovementTest().fixture(sc.get());
  try(var ac=Robolectric.buildActivity(VideoActivity.class).setup()){
   VideoPane p=ac.get().pane;
   for(int[] size:new int[][]{{960,2100},{1080,2200},{1233,2600},{2400,1080}}){PlayerUiImprovementTest.layout(p,size[0],size[1]);View[] buttons={p.lock,p.rewind,p.previous,p.play,p.next,p.forward,p.fit};for(int i=0;i<buttons.length;i++){View a=buttons[i];assertTrue(a.getWidth()>=p.dp(48));assertTrue(a.getHeight()>=p.dp(48));Rect bounds=new Rect();a.getGlobalVisibleRect(bounds);for(int j=i+1;j<buttons.length;j++){Rect other=new Rect();buttons[j].getGlobalVisibleRect(other);assertFalse(Rect.intersects(bounds,other));}}assertTrue(p.play.getBackground() instanceof VideoControlBackground);}
   PlayerUiImprovementTest.save(p,2400,1080,"video-controls-v1.15.png");p.lock.performClick();PlayerUiImprovementTest.save(p,2400,1080,"video-locked-v1.15.png");p.unlock.performLongClick();PlayerUiImprovementTest.save(p,1080,2200,"video-portrait-v1.15.png");
  }finally{sc.destroy();}
 }
}
