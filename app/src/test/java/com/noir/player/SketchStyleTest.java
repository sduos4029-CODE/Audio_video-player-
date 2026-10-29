package com.noir.player;

import android.net.Uri;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk=28,qualifiers="w360dp-h800dp-xxhdpi",shadows=YouTubeScreenTest.RecordingSession.class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class SketchStyleTest {
 @Test public void pickerAppliesAllSixStylesWithoutRebuildingPlayback(){
  var sc=Robolectric.buildService(VideoRotationTest.RecordingPlayback.class).create();var s=sc.get();s.load(Uri.parse("https://example.org/clip.mp4"),"Fixture");s.hasVideo=true;
  try(var ac=Robolectric.buildActivity(VideoActivity.class).setup()){
   VideoPane p=ac.get().pane;var out=s.outputs.get(0);
   for(int style=1;style<=6;style++){p.sketch.performClick();assertTrue(p.sketchDialog.isShowing());assertEquals(7,p.sketchDialog.getListView().getCount());p.sketchDialog.getListView().performItemClick(null,style,style);assertEquals(style,s.sketchStyle());assertTrue(s.sketchEnabled());assertEquals(style,ac.get().getSharedPreferences("noir",0).getInt("video_sketch_style",0));assertEquals(62000,s.position());}
   assertEquals(1,out.effectCalls);assertEquals(1,out.prepares);assertEquals(1,out.plays);assertEquals(0,out.pauses);assertEquals(0,out.seeks);assertSame(s.sketchEffect,out.effects.get(0));
   p.sketch.performClick();p.sketchDialog.getListView().performItemClick(null,0,0);assertFalse(s.sketchEnabled());p.sketch.performLongClick();assertTrue(s.sketchEnabled());assertEquals(SketchStyle.COMIC,s.sketchStyle());
   PlayerUiImprovementTest.save(p,2400,1080,"six-style-controls-v1.16.png");Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();p.sketch.performClick();Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();p.sketchDialog.getListView().setScrollIndicators(0);android.view.View decor=p.sketchDialog.getWindow().getDecorView();PlayerUiImprovementTest.save(decor,1080,1500,"sketch-picker-v1.16.png");p.sketchDialog.dismiss();
  }catch(Exception e){throw new RuntimeException(e);}finally{sc.destroy();}
 }
 @Test public void selectedStyleSurvivesServiceRestartAndInvalidPreferenceIsSafe(){
  var sc=Robolectric.buildService(VideoRotationTest.RecordingPlayback.class).create();sc.get().setSketchStyle(SketchStyle.WHITE_OUTLINE);sc.destroy();var next=Robolectric.buildService(VideoRotationTest.RecordingPlayback.class).create();try{assertEquals(SketchStyle.WHITE_OUTLINE,next.get().sketchStyle());assertTrue(next.get().sketchEnabled());next.get().setSketchStyle(-1);assertEquals(SketchStyle.FINE_PENCIL,next.get().sketchStyle());}finally{next.destroy();}
 }
 @Test public void pausedStyleChangeRefreshesFrameAndTouchLockBlocksPicker(){
  var sc=Robolectric.buildService(VideoRotationTest.RecordingPlayback.class).create();var s=sc.get();s.load(Uri.parse("https://example.org/clip.mp4"),"Fixture");s.hasVideo=true;
  try(var ac=Robolectric.buildActivity(VideoActivity.class).setup()){
   var out=s.outputs.get(0);out.player.pause();s.setSketchStyle(SketchStyle.COLOR_PENCIL);assertEquals(1,out.seeks);assertEquals(62000,s.position());assertFalse(s.playing());VideoPane p=ac.get().pane;p.setTouchLocked(true);p.showSketchStyles();assertNull(p.sketchDialog);
  }finally{sc.destroy();}
 }
}
