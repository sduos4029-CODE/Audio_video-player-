package com.noir.player;

import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Looper;
import android.view.*;
import android.widget.ImageView;
import android.widget.LinearLayout;
import java.nio.ByteBuffer;
import java.time.Duration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk=28,qualifiers="w360dp-h800dp-xxhdpi",shadows=YouTubeScreenTest.RecordingSession.class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class SketchPreviewTest {
 static Bitmap[] frames(){String[] files={"normal","fine-pencil","charcoal","ink-hatch","color-pencil","white-outline","comic"};Bitmap[] frames=new Bitmap[files.length];for(int i=0;i<frames.length;i++)try(var stream=SketchPreviewTest.class.getResourceAsStream("/style-previews/"+files[i]+".png")){assertNotNull(stream);frames[i]=BitmapFactory.decodeStream(stream);assertNotNull(frames[i]);}catch(java.io.IOException error){throw new RuntimeException(error);}return frames;}
 static void idle(){Shadows.shadowOf(Looper.getMainLooper()).idle();}
 @Test public void browsingShowsActualCapturedImagesAndApplyAloneChangesTheVideo(){
  var sc=Robolectric.buildService(VideoRotationTest.RecordingPlayback.class).create();var s=sc.get();s.load(Uri.parse("https://example.org/clip.mp4"),"Fixture");s.hasVideo=true;
  try(var ac=Robolectric.buildActivity(VideoActivity.class).setup()){
   VideoPane p=ac.get().pane;PlayerUiImprovementTest.layout(p,1080,2200);p.sketch.performClick();SketchStylePopup popup=p.stylePopup;assertFalse(popup.apply.isEnabled());assertTrue(popup.panelWidth<=p.dp(264));assertEquals(p.dp(64),popup.panelHeight);assertEquals(View.GONE,popup.picture.getVisibility());Bitmap[] captured=frames();popup.request.finish(captured);idle();assertTrue(popup.apply.isEnabled());
   popup.selector.setSelection(SketchStyle.COLOR_PENCIL);idle();assertSame(captured[SketchStyle.COLOR_PENCIL],((BitmapDrawable)popup.preview.getDrawable()).getBitmap());assertTrue(popup.previewExpanded);assertTrue(popup.panelHeight<=p.dp(164));popup.previewToggle.performClick();assertFalse(popup.previewExpanded);assertEquals(p.dp(64),popup.panelHeight);assertTrue(popup.apply.getHeight()>=p.dp(48));assertTrue(popup.cancel.getHeight()>=p.dp(48));assertFalse(s.sketchEnabled());assertEquals(SketchStyle.FINE_PENCIL,s.sketchStyle());
   LinearLayout row=(LinearLayout)popup.adapter.getDropDownView(SketchStyle.WHITE_OUTLINE,null,popup.selector);assertSame(captured[SketchStyle.WHITE_OUTLINE],((BitmapDrawable)((ImageView)row.getChildAt(0)).getDrawable()).getBitmap());
   popup.apply.performClick();assertTrue(s.sketchEnabled());assertEquals(SketchStyle.COLOR_PENCIL,s.sketchStyle());assertFalse(popup.isShowing());assertEquals(1,s.outputs.get(0).prepares);assertEquals(0,s.outputs.get(0).seeks);assertEquals(62000,s.position());
  }finally{sc.destroy();}
 }
 @Test public void cancelAndTimeoutCancelReadbackWithoutChangingTheSelectedStyle(){
  var sc=Robolectric.buildService(VideoRotationTest.RecordingPlayback.class).create();var s=sc.get();s.load(Uri.parse("https://example.org/clip.mp4"),"Fixture");s.hasVideo=true;
  try(var ac=Robolectric.buildActivity(VideoActivity.class).setup()){
   VideoPane p=ac.get().pane;PlayerUiImprovementTest.layout(p,1080,2200);p.sketch.performClick();SketchStylePopup popup=p.stylePopup;popup.selector.setSelection(SketchStyle.COMIC);idle();popup.cancel.performClick();assertTrue(popup.request.cancelled.get());assertNull(s.sketchEffect.previewRequest.get());assertFalse(s.sketchEnabled());Bitmap[] late=frames();popup.request.finish(late);idle();assertTrue(late[0].isRecycled());assertNull(p.stylePopup);
   p.sketch.performClick();popup=p.stylePopup;Shadows.shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(3100));assertTrue(popup.request.cancelled.get());assertTrue(popup.apply.isEnabled());assertEquals(ac.get().getString(R.string.video_preview_unavailable),popup.status.getText());assertFalse(s.sketchEnabled());popup.dismiss();
  }finally{sc.destroy();}
 }
 @Test public void sourceChangeOrTouchLockClosesThePickerAndDropsPendingWork(){
  var sc=Robolectric.buildService(VideoRotationTest.RecordingPlayback.class).create();var s=sc.get();s.load(Uri.parse("https://example.org/clip.mp4"),"Fixture");s.hasVideo=true;
  try(var ac=Robolectric.buildActivity(VideoActivity.class).setup()){
   VideoPane p=ac.get().pane;p.sketch.performClick();SketchStylePopup popup=p.stylePopup;s.source="https://example.org/other.mp4";p.render();assertFalse(popup.isShowing());assertTrue(popup.request.cancelled.get());p.sketch.performClick();popup=p.stylePopup;p.setTouchLocked(true);assertFalse(popup.isShowing());assertTrue(popup.request.cancelled.get());
  }finally{sc.destroy();}
 }
 @Test public void pausedSeekablePreviewRequestsAFrameWithoutPlayOrReprepare(){
  var sc=Robolectric.buildService(VideoRotationTest.RecordingPlayback.class).create();var s=sc.get();s.load(Uri.parse("https://example.org/clip.mp4"),"Fixture");var out=s.outputs.get(0);out.player.pause();out.seekable=true;
  try{SketchPreviewRequest request=s.requestSketchPreviews(new SketchPreviewRequest.Callback(){public void ready(Bitmap[] images){}public void failed(){}});assertEquals(1,out.seeks);assertEquals(1,out.prepares);assertEquals(1,out.plays);assertFalse(s.playing());assertEquals(62000,s.position());s.sketchEffect.cancelPreview(request);}finally{sc.destroy();}
 }
 @Test public void rgbaReadbackPreservesColorsAndFlipsOnlyTheVerticalDirection(){byte[] bytes={(byte)255,0,0,(byte)255,0,(byte)255,0,(byte)255,0,0,(byte)255,(byte)255,(byte)255,(byte)255,(byte)255,(byte)128};Bitmap image=SketchPreviewRequest.fromRgba(ByteBuffer.wrap(bytes),2,2);assertEquals(Color.BLUE,image.getPixel(0,0));assertEquals(Color.RED,image.getPixel(0,1));assertEquals(Color.GREEN,image.getPixel(1,1));assertEquals(128,Color.alpha(image.getPixel(1,0)));assertEquals(255,Color.red(image.getPixel(1,0)));}
 @Test public void captureCompletionIsDeliveredOnceAndCancelledResultsAreDiscarded(){int[] calls={0};SketchPreviewRequest request=new SketchPreviewRequest(new SketchPreviewRequest.Callback(){public void ready(Bitmap[] images){calls[0]++;}public void failed(){calls[0]++;}});Bitmap[] first=frames(),second=frames();request.finish(first);request.finish(second);idle();assertEquals(1,calls[0]);assertTrue(second[0].isRecycled());assertFalse(first[0].isRecycled());}
}
