package com.noir.player;

import android.graphics.*;
import android.net.Uri;
import androidx.media3.common.Format;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk=28,qualifiers="w360dp-h800dp-xxhdpi",shadows=YouTubeScreenTest.RecordingSession.class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class VideoFitRegressionTest {
 @Test public void gpuFitDoesNotFitAlreadyLetterboxedVideoAgain(){
  for(int[] viewport:new int[][]{{1080,2400},{2400,1080}})for(float aspect:new float[]{16/9f,2.35f,4/3f,9/16f}){
   int w=viewport[0],h=viewport[1];Matrix transform=VideoGeometry.textureTransform(w,h,aspect,VideoGeometry.FIT,1,0,0,true);assertTrue(transform.isIdentity());
   RectF picture=VideoGeometry.pictureInOutput(w,h,aspect,true);transform.mapRect(picture);assertEquals(aspect,picture.width()/picture.height(),.001f);assertTrue(picture.left>=-.01f&&picture.top>=-.01f&&picture.right<=w+.01f&&picture.bottom<=h+.01f);
  }
  RectF movie=VideoGeometry.pictureInOutput(1080,2400,2.35f,true);assertEquals(1080/2.35f,movie.height(),.01f);assertTrue(movie.height()>450);
 }
 @Test public void cropAndStretchMapThePictureWithoutDuplicatingTheGpuFit(){
  for(boolean gpu:new boolean[]{true,false})for(int[] viewport:new int[][]{{1080,2400},{2400,1080}}){int w=viewport[0],h=viewport[1];
   RectF crop=VideoGeometry.pictureInOutput(w,h,2.35f,gpu);VideoGeometry.textureTransform(w,h,2.35f,VideoGeometry.CROP,1,0,0,gpu).mapRect(crop);assertEquals(2.35f,crop.width()/crop.height(),.001f);assertTrue(crop.left<=.01f&&crop.top<=.01f&&crop.right>=w-.01f&&crop.bottom>=h-.01f);
   RectF stretch=VideoGeometry.pictureInOutput(w,h,2.35f,gpu);VideoGeometry.textureTransform(w,h,2.35f,VideoGeometry.STRETCH,1,0,0,gpu).mapRect(stretch);assertEquals(0,stretch.left,.01f);assertEquals(0,stretch.top,.01f);assertEquals(w,stretch.width(),.01f);assertEquals(h,stretch.height(),.01f);
  }
 }
 @Test public void paneUsesTheActiveSourceAspectAndHandlesNormalRendererFallback(){
  var sc=Robolectric.buildService(VideoRotationTest.RecordingPlayback.class).create();var s=sc.get();s.load(Uri.parse("https://example.org/widescreen.mp4"),"Wide clip");s.hasVideo=true;var out=s.outputs.get(0);out.videoFormat=new Format.Builder().setWidth(1920).setHeight(816).setPixelWidthHeightRatio(1).build();
  try(var ac=Robolectric.buildActivity(VideoActivity.class).setup()){
   VideoPane p=ac.get().pane;p.setSizingMode(VideoGeometry.FIT);PlayerUiImprovementTest.layout(p,1080,2400);p.render();assertEquals(1920/816f,p.aspect,.001f);assertTrue(p.texture.getTransform(new Matrix()).isIdentity());RectF actual=VideoSizingTest.picture(p);assertTrue(actual.height()>450);
   s.useVideoEffects=false;p.render();RectF fallback=VideoSizingTest.picture(p);assertEquals(actual.height(),fallback.height(),.01f);assertFalse(p.texture.getTransform(new Matrix()).isIdentity());
  }finally{sc.destroy();}
 }
 @Test public void sourceAspectIncludesPixelShapeAndRotation(){assertEquals(16/9f,VideoGeometry.sourceAspect(720,576,64/45f,0),.001f);assertEquals(9/16f,VideoGeometry.sourceAspect(1920,1080,1,90),.001f);assertEquals(16/9f,VideoGeometry.sourceAspect(0,0,Float.NaN,0),.001f);}
 @Test public void aGpuLetterboxedTestFrameKeepsItsHeightAndRoundObjects(){
  int w=180,h=400;RectF sourceRect=VideoGeometry.pictureInOutput(w,h,2.35f,true);Bitmap buffer=Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);Canvas canvas=new Canvas(buffer);canvas.drawColor(Color.BLACK);Paint paint=new Paint();paint.setColor(Color.CYAN);canvas.drawRect(sourceRect,paint);paint.setColor(Color.RED);canvas.drawCircle(w/2f,h/2f,20,paint);
  Bitmap visible=Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);new Canvas(visible).drawBitmap(buffer,VideoGeometry.textureTransform(w,h,2.35f,VideoGeometry.FIT,1,0,0,true),null);
  int top=h,bottom=-1,left=w,right=-1,redTop=h,redBottom=-1;for(int y=0;y<h;y++)for(int x=0;x<w;x++){int pixel=visible.getPixel(x,y);if(pixel==Color.CYAN){top=Math.min(top,y);bottom=Math.max(bottom,y);}if(pixel==Color.RED){left=Math.min(left,x);right=Math.max(right,x);redTop=Math.min(redTop,y);redBottom=Math.max(redBottom,y);}}
  assertEquals(w/2.35f,bottom-top+1,2);assertEquals(right-left,redBottom-redTop,1);
 }
}
