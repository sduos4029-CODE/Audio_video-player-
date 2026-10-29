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
   for(int style=1;style<=6;style++){p.sketch.performClick();assertTrue(p.stylePopup.isShowing());assertEquals(7,p.stylePopup.selector.getAdapter().getCount());p.stylePopup.ready(SketchPreviewTest.frames());p.stylePopup.selector.setSelection(style);Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();p.stylePopup.apply.performClick();assertEquals(style,s.sketchStyle());assertTrue(s.sketchEnabled());assertEquals(style,ac.get().getSharedPreferences("noir",0).getInt("video_sketch_style",0));assertEquals(62000,s.position());}
   assertEquals(1,out.effectCalls);assertEquals(1,out.prepares);assertEquals(1,out.plays);assertEquals(0,out.pauses);assertEquals(0,out.seeks);assertSame(s.sketchEffect,out.effects.get(0));
   p.sketch.performClick();p.stylePopup.ready(SketchPreviewTest.frames());p.stylePopup.selector.setSelection(0);Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();p.stylePopup.apply.performClick();assertFalse(s.sketchEnabled());p.sketch.performLongClick();assertTrue(s.sketchEnabled());assertEquals(SketchStyle.COMIC,s.sketchStyle());
   p.sketch.performClick();p.stylePopup.ready(SketchPreviewTest.frames());Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();SketchStylePopup popup=p.stylePopup;PlayerUiImprovementTest.save(popup.content,popup.panelWidth,popup.panelHeight,"compact-style-popup-v1.17.png");popup.dismiss();
  }catch(Exception e){throw new RuntimeException(e);}finally{sc.destroy();}
 }
 @Test public void selectedStyleSurvivesServiceRestartAndInvalidPreferenceIsSafe(){
  var sc=Robolectric.buildService(VideoRotationTest.RecordingPlayback.class).create();sc.get().setSketchStyle(SketchStyle.WHITE_OUTLINE);sc.destroy();var next=Robolectric.buildService(VideoRotationTest.RecordingPlayback.class).create();try{assertEquals(SketchStyle.WHITE_OUTLINE,next.get().sketchStyle());assertTrue(next.get().sketchEnabled());next.get().setSketchStyle(-1);assertEquals(SketchStyle.FINE_PENCIL,next.get().sketchStyle());}finally{next.destroy();}
 }
 @Test public void pausedStyleChangeRefreshesFrameAndTouchLockBlocksPicker(){
  var sc=Robolectric.buildService(VideoRotationTest.RecordingPlayback.class).create();var s=sc.get();s.load(Uri.parse("https://example.org/clip.mp4"),"Fixture");s.hasVideo=true;
  try(var ac=Robolectric.buildActivity(VideoActivity.class).setup()){
   var out=s.outputs.get(0);out.player.pause();s.setSketchStyle(SketchStyle.COLOR_PENCIL);assertEquals(1,out.seeks);assertEquals(62000,s.position());assertFalse(s.playing());VideoPane p=ac.get().pane;p.setTouchLocked(true);p.showSketchStyles();assertNull(p.stylePopup);
  }finally{sc.destroy();}
 }
}
