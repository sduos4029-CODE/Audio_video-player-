package com.noir.player;
import android.app.Notification;
import android.content.*;
import android.graphics.*;
import android.view.*;
import org.junit.Test;
import org.junit.After;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import java.io.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk=28,shadows=YouTubeScreenTest.RecordingSession.class)
public class YouTubeScreenTest {
 @Implements(android.media.session.MediaSession.class) public static class RecordingSession extends org.robolectric.shadows.ShadowMediaSession {android.media.session.PlaybackState lastState;@Implementation protected void setPlaybackState(android.media.session.PlaybackState state){lastState=state;}}
 android.media.session.PlaybackState publishedState(WebPlaybackService s){return ((RecordingSession)org.robolectric.shadow.api.Shadow.extract(s.session)).lastState;}
 org.robolectric.android.controller.ServiceController<WebPlaybackService> service;
 WebPlaybackService createService(){service=Robolectric.buildService(WebPlaybackService.class).create();Shadows.shadowOf(RuntimeEnvironment.getApplication()).setComponentNameAndServiceForBindService(new ComponentName(RuntimeEnvironment.getApplication(),WebPlaybackService.class),service.get().binder);return service.get();}
 @After public void cleanup(){if(service!=null)service.destroy();}
 @Test public void musicLinkRoutesOnlyToPulseActivity(){try(var controller=Robolectric.buildActivity(MainActivity.class).setup()){MainActivity a=controller.get();a.load("https://music.youtube.com/watch?v=dQw4w9WgXcQ","test");Intent next=Shadows.shadowOf(a).getNextStartedActivity();assertEquals(YouTubeActivity.class.getName(),next.getComponent().getClassName());assertTrue(next.getBooleanExtra("music",false));assertNull(Shadows.shadowOf(a).getNextStartedActivity());}}
 @Test public void browserRetainedAcrossActivityCloseAndNoAppHandoff(){
  WebPlaybackService s=createService();s.background=true;
  var controller=Robolectric.buildActivity(YouTubeActivity.class,new Intent().putExtra("music",true)).setup();YouTubeActivity a=controller.get();
  assertEquals("https://music.youtube.com/",Shadows.shadowOf(a.web).getLastLoadedUrl());assertTrue(a.web.getWebViewClient().shouldOverrideUrlLoading(a.web,"intent://watch?v=test"));assertNull(Shadows.shadowOf(a).getNextStartedActivity());
  android.webkit.WebView retained=s.web;controller.pause().stop().destroy();assertSame(retained,s.web);assertNull(s.screen);assertTrue(s.background);
 }
 @Test public void notificationUsesMediaSessionAndStandardTransportActions(){
  Notification n=createService().notification();assertNotNull(n.extras.getParcelable(Notification.EXTRA_MEDIA_SESSION));assertEquals(4,n.actions.length);assertEquals("Back 10 seconds",n.actions[0].title);assertEquals("Forward 10 seconds",n.actions[2].title);assertEquals("Stop",n.actions[3].title);service.get().updateSession();assertEquals(2,publishedState(service.get()).getCustomActions().size());
 }
 @Test public void leavingMediaPageClearsStaleNotificationPlaybackState(){WebPlaybackService s=createService();s.snapshot(org.json.JSONObject.quote("{\"title\":\"Example\",\"playing\":true,\"position\":12,\"duration\":60}"));assertTrue(s.playing);assertEquals(12000,s.position);s.snapshot("null");assertFalse(s.playing);assertEquals(android.media.session.PlaybackState.STATE_PAUSED,publishedState(s).getState());}
 @Test public void disablingBackgroundPausesWhenActivityLeaves(){WebPlaybackService s=createService();s.background=false;try(var activity=Robolectric.buildActivity(YouTubeActivity.class).setup()){s.playing=true;activity.pause();assertFalse(s.playing);assertFalse(s.screenVisible);}}
 @Test @Config(sdk=35,qualifiers="w360dp-h800dp-xxhdpi") @GraphicsMode(GraphicsMode.Mode.NATIVE)
 public void systemBarsAndCutoutRemainOutsideTouchableToolbar() throws Exception {createService();try(var activity=Robolectric.buildActivity(YouTubeActivity.class).setup()){
  YouTubeActivity a=activity.get();
  WindowInsets insets=new WindowInsets.Builder().setInsets(WindowInsets.Type.systemBars(),Insets.of(0,90,0,60)).setInsets(WindowInsets.Type.displayCutout(),Insets.of(0,110,0,0)).build();a.frame.dispatchApplyWindowInsets(insets);
  int width=1080,height=2400;a.frame.measure(View.MeasureSpec.makeMeasureSpec(width,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(height,View.MeasureSpec.EXACTLY));a.frame.layout(0,0,width,height);
  assertEquals(110,a.frame.getPaddingTop());assertEquals(60,a.frame.getPaddingBottom());assertEquals(110,a.frame.getChildAt(0).getTop());assertEquals(a.dp(56),a.toolbar.getHeight());assertEquals(a.dp(48),a.toolbar.getChildAt(0).getWidth());assertEquals(View.GONE,a.status.getVisibility());
  Bitmap bitmap=Bitmap.createBitmap(width,420,Bitmap.Config.ARGB_8888);a.frame.draw(new Canvas(bitmap));File dir=new File("build/player-screenshots");dir.mkdirs();try(FileOutputStream out=new FileOutputStream(new File(dir,"web-toolbar-v1.4.png"))){bitmap.compress(Bitmap.CompressFormat.PNG,100,out);}
 }}
}
