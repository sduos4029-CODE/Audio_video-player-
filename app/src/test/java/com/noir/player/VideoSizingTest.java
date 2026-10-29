package com.noir.player;

import android.content.Context;
import android.graphics.*;
import android.view.*;
import java.time.Duration;
import android.os.Looper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk=28,qualifiers="w360dp-h800dp-xxhdpi",shadows=YouTubeScreenTest.RecordingSession.class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class VideoSizingTest {
 @Test public void fitCropAndStretchHaveExpectedBoundsInBothOrientations(){
  for(int[] size:new int[][]{{1080,2200},{2400,1080}}){int w=size[0],h=size[1];
   RectF fit=VideoGeometry.bounds(w,h,16/9f,VideoGeometry.FIT,1,0,0);
   assertTrue(fit.left>=-.01f&&fit.top>=-.01f&&fit.right<=w+.01f&&fit.bottom<=h+.01f);
   assertEquals(16/9f,fit.width()/fit.height(),.001f);
   RectF crop=VideoGeometry.bounds(w,h,16/9f,VideoGeometry.CROP,1,0,0);
   assertTrue(crop.left<=.01f&&crop.top<=.01f&&crop.right>=w-.01f&&crop.bottom>=h-.01f);
   assertEquals(16/9f,crop.width()/crop.height(),.001f);
   assertEquals(new RectF(0,0,w,h),VideoGeometry.bounds(w,h,16/9f,VideoGeometry.STRETCH,1,0,0));
  }
 }
 @Test public void zoomAndPanAreBoundedAndSmallPictureStaysCentered(){
  RectF enlarged=VideoGeometry.bounds(1000,1000,1,VideoGeometry.FIT,2,99,-99);
  assertEquals(new RectF(0,-1000,2000,1000),enlarged);
  RectF reduced=VideoGeometry.bounds(1000,1000,1,VideoGeometry.FIT,.5f,99,-99);
  assertEquals(new RectF(250,250,750,750),reduced);
  assertEquals(4,VideoGeometry.clampZoom(100),0);assertEquals(.5f,VideoGeometry.clampZoom(0),0);
  assertEquals(1,VideoGeometry.clampZoom(Float.NaN),0);
 }
 @Test public void sizingMenuButtonsAndFullscreenSharePreferencesWithoutSeeking()throws Exception{
  var sc=Robolectric.buildService(PlayerUiImprovementTest.UiPlayback.class).create();
  new PlayerUiImprovementTest().fixture(sc.get());
  try(var ac=Robolectric.buildActivity(VideoActivity.class).setup()){
   VideoPane pane=ac.get().pane;PlayerUiImprovementTest.layout(pane,1080,2200);
   pane.fit.performLongClick();VideoSizingSheet menu=pane.sizingSheet;assertTrue(menu.isShowing());
   Shadows.shadowOf(Looper.getMainLooper()).idleFor(Duration.ofSeconds(4));assertTrue(pane.controlsVisible);
   menu.choices[VideoGeometry.STRETCH].performClick();assertTrue(menu.choices[2].isSelected());
   RectF mapped=picture(pane);assertEquals(0,mapped.left,.01f);assertEquals(2200,mapped.bottom,.01f);
   menu.zoomIn.performClick();assertEquals(1.25f,pane.zoom,.001f);assertEquals("125%",menu.zoomValue.getText());
   assertEquals(62000,sc.get().position());assertTrue(sc.get().running);
   VideoPane home=new VideoPane(ac.get(),false);home.setVideoRequested(true,false);PlayerUiImprovementTest.layout(home,1080,1200);
   assertEquals(VideoGeometry.STRETCH,home.sizingMode);assertEquals(pane.zoom,home.zoom,.001f);
   int buttonWidth=pane.play.getWidth();pane.changeZoom(3);assertEquals(buttonWidth,pane.play.getWidth());
   assertEquals(1080,pane.texture.getWidth());assertEquals(2200,pane.texture.getHeight());
   menu.reset.performClick();assertEquals(1,pane.zoom,0);assertEquals(0,pane.panX,0);
   menu.choices[VideoGeometry.CROP].performClick();menu.zoomIn.performClick();
   PlayerUiImprovementTest.save(pane,1080,2200,"video-zoom-v1.12.png");
   View decor=menu.getWindow().getDecorView();decor.measure(View.MeasureSpec.makeMeasureSpec(1080,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(1800,View.MeasureSpec.AT_MOST));
   PlayerUiImprovementTest.save(decor,1080,decor.getMeasuredHeight(),"video-sizing-v1.12.png");menu.dismiss();Shadows.shadowOf(Looper.getMainLooper()).idle();
   assertNull(pane.sizingSheet);
  }finally{sc.destroy();}
 }
 @Test public void oldFillPreferenceMigratesAndAudioArtworkDoesNotZoom(){
  Context context=RuntimeEnvironment.getApplication();context.getSharedPreferences("noir",0).edit().putBoolean("video_fill",true).apply();
  VideoPane pane=new VideoPane(context,false);assertEquals(VideoGeometry.CROP,pane.sizingMode);
  pane.changeZoom(2);pane.setVideoRequested(false,false);PlayerUiImprovementTest.layout(pane,1080,1200);
  assertEquals(android.widget.ImageView.ScaleType.FIT_CENTER,pane.poster.getScaleType());
  assertEquals(1,pane.play.getScaleX(),0);
 }
 @Test public void pinchOverVisibleControlsZoomsWithoutTogglingPlaybackAndDragCannotExposeEdges(){
  var sc=Robolectric.buildService(PlayerUiImprovementTest.UiPlayback.class).create();new PlayerUiImprovementTest().fixture(sc.get());
  try(var ac=Robolectric.buildActivity(VideoActivity.class).setup()){
   VideoPane pane=ac.get().pane;PlayerUiImprovementTest.layout(pane,1080,2200);pane.setSizingMode(VideoGeometry.STRETCH);
   // Start directly over the bottom play button: the second pointer must cancel its click.
   int[] playLocation=new int[2];pane.play.getLocationOnScreen(playLocation);float playY=playLocation[1]+pane.play.getHeight()/2f;
   send(pane,0,MotionEvent.ACTION_DOWN,new float[]{530},new float[]{playY});
   send(pane,20,MotionEvent.ACTION_POINTER_DOWN|(1<<MotionEvent.ACTION_POINTER_INDEX_SHIFT),new float[]{530,550},new float[]{playY,playY});
   send(pane,50,MotionEvent.ACTION_MOVE,new float[]{400,680},new float[]{playY,playY});
   send(pane,80,MotionEvent.ACTION_MOVE,new float[]{250,830},new float[]{playY,playY});
   send(pane,90,MotionEvent.ACTION_MOVE,new float[]{100,980},new float[]{playY,playY});
   send(pane,100,MotionEvent.ACTION_POINTER_UP|(1<<MotionEvent.ACTION_POINTER_INDEX_SHIFT),new float[]{250,830},new float[]{playY,playY});
   send(pane,120,MotionEvent.ACTION_UP,new float[]{250},new float[]{playY});
   assertTrue("pinch zoom="+pane.zoom,pane.zoom>1);assertTrue("playback running",sc.get().running);assertEquals(62000,sc.get().position());
   pane.setControlsVisible(false);
   send(pane,200,MotionEvent.ACTION_DOWN,new float[]{500},new float[]{500});
   send(pane,230,MotionEvent.ACTION_MOVE,new float[]{5000},new float[]{5000});
   send(pane,250,MotionEvent.ACTION_UP,new float[]{5000},new float[]{5000});
   RectF rect=picture(pane);assertTrue(rect.left<=.01f&&rect.top<=.01f);assertTrue(rect.right>=1079&&rect.bottom>=2199);
   assertFalse(ac.get().isFinishing());assertTrue(sc.get().running);
   float savedZoom=pane.zoom;ac.recreate();assertEquals(savedZoom,ac.get().pane.zoom,.001f);
  }finally{sc.destroy();}
 }
 static RectF picture(VideoPane pane){RectF rect=new RectF(0,0,pane.getWidth(),pane.getHeight());pane.texture.getTransform(new Matrix()).mapRect(rect);return rect;}
 static void send(VideoPane pane,long t,int action,float[] x,float[] y){
  MotionEvent.PointerProperties[] properties=new MotionEvent.PointerProperties[x.length];MotionEvent.PointerCoords[] coords=new MotionEvent.PointerCoords[x.length];
  for(int i=0;i<x.length;i++){properties[i]=new MotionEvent.PointerProperties();properties[i].id=i;properties[i].toolType=MotionEvent.TOOL_TYPE_FINGER;coords[i]=new MotionEvent.PointerCoords();coords[i].x=x[i];coords[i].y=y[i];coords[i].pressure=1;coords[i].size=1;}
  MotionEvent event=MotionEvent.obtain(0,t,action,x.length,properties,coords,0,0,1,1,0,0,android.view.InputDevice.SOURCE_TOUCHSCREEN,0);
  try{pane.dispatchTouchEvent(event);}finally{event.recycle();}
 }
}
