package com.noir.player;
import android.content.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import java.util.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk=28)
public class YouTubeLauncherTest {
 static final String ID="dQw4w9WgXcQ";
 static class Recorder extends ContextWrapper {
  final List<Intent> attempts=new ArrayList<>();final String available;
  Recorder(String available){super(null);this.available=available;}
  public void startActivity(Intent i){attempts.add(i);if(!Objects.equals(available,i.getPackage()))throw new ActivityNotFoundException();}
 }
 @Test public void opensMusicAppWithCorrectLink(){Recorder r=new Recorder("com.google.android.apps.youtube.music");assertTrue(YouTubeLauncher.open(r,ID,true));assertEquals(1,r.attempts.size());assertEquals("music.youtube.com",r.attempts.get(0).getData().getHost());}
 @Test public void fallsBackToYouTubeWhenMusicMissing(){Recorder r=new Recorder("com.google.android.youtube");assertTrue(YouTubeLauncher.open(r,ID,true));assertEquals(2,r.attempts.size());assertEquals("www.youtube.com",r.attempts.get(1).getData().getHost());}
 @Test public void fallsBackToBrowserWhenBothAppsMissing(){Recorder r=new Recorder(null);assertTrue(YouTubeLauncher.open(r,ID,false));assertEquals(3,r.attempts.size());assertNull(r.attempts.get(2).getPackage());}
 @Test public void handlesNoAppAndRejectsInvalidId(){Recorder r=new Recorder("none");assertFalse(YouTubeLauncher.open(r,ID,false));assertEquals(3,r.attempts.size());r.attempts.clear();assertFalse(YouTubeLauncher.open(r,"bad&id",true));assertTrue(r.attempts.isEmpty());}
}
