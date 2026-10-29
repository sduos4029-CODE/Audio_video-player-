package com.noir.player;
import android.content.Intent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk=28)
public class YouTubeScreenTest {
 @Test public void musicLinkRoutesOnlyToPulseActivity(){
  try(var controller=Robolectric.buildActivity(MainActivity.class).setup()){
   MainActivity a=controller.get();a.load("https://music.youtube.com/watch?v=dQw4w9WgXcQ","test");
   Intent next=Shadows.shadowOf(a).getNextStartedActivity();assertEquals(YouTubeActivity.class.getName(),next.getComponent().getClassName());assertTrue(next.getBooleanExtra("music",false));assertEquals("dQw4w9WgXcQ",next.getStringExtra("id"));assertNull(Shadows.shadowOf(a).getNextStartedActivity());
  }
 }
 @Test public void browsingStaysInsidePulseAndBlocksAppHandoff(){
  Intent intent=new Intent().putExtra("music",true);
  try(var controller=Robolectric.buildActivity(YouTubeActivity.class,intent).setup()){
   YouTubeActivity a=controller.get();assertEquals("https://music.youtube.com/",Shadows.shadowOf(a.web).getLastLoadedUrl());assertTrue(a.web.getWebViewClient().shouldOverrideUrlLoading(a.web,"intent://watch?v=test"));assertTrue(a.web.getWebViewClient().shouldOverrideUrlLoading(a.web,"vnd.youtube:dQw4w9WgXcQ"));assertNull(Shadows.shadowOf(a).getNextStartedActivity());
  }
 }
}
