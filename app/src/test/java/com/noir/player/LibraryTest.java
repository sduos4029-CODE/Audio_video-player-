package com.noir.player;
import android.content.*;
import android.app.Activity;
import android.view.*;
import android.graphics.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import java.util.*;
import java.io.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk=28)
public class LibraryTest {
 @Test public void importsAreDeduplicatedAndSearchFiltersDoNotDropOriginals(){var prefs=RuntimeEnvironment.getApplication().getSharedPreferences("noir",0);LibraryStore.add(prefs,new LibraryStore.Track("content://media/1","Evening Song.mp3","audio/mpeg"));LibraryStore.add(prefs,new LibraryStore.Track("content://media/1","Duplicate","audio/mpeg"));LibraryStore.add(prefs,new LibraryStore.Track("file:///private/video","Concert.mp4","video/mp4"));var tracks=LibraryStore.read(prefs);assertEquals(2,tracks.size());assertEquals(1,LibraryStore.filter(tracks,"SONG",1).size());assertEquals(1,LibraryStore.filter(tracks,"",2).size());assertEquals(1,LibraryStore.filter(tracks,"",3).size());LibraryStore.remove(prefs,"content://media/1");assertEquals(1,LibraryStore.read(prefs).size());}
 @Test public void handlesLegacyAndMalformedEntries(){var prefs=RuntimeEnvironment.getApplication().getSharedPreferences("noir",0);prefs.edit().putString("library","[{\"uri\":\"content://media/1\",\"title\":\"Old song\"},null,{\"title\":\"broken\"}]").commit();assertEquals(1,LibraryStore.read(prefs).size());prefs.edit().putString("library","invalid JSON").commit();assertTrue(LibraryStore.read(prefs).isEmpty());}
 public static class PreviewLibrary extends LibraryActivity {void readMetadata(LibraryStore.Track track){metadata.put(track.uri,new Meta());}}
 @Test @GraphicsMode(GraphicsMode.Mode.NATIVE) @Config(qualifiers="w360dp-h800dp-xxhdpi") public void standardLibraryFiltersAndSelectsTrack()throws Exception{
  var prefs=RuntimeEnvironment.getApplication().getSharedPreferences("noir",0);LibraryStore.add(prefs,new LibraryStore.Track("content://media/1","Midnight Drift.mp3","audio/mpeg"));LibraryStore.add(prefs,new LibraryStore.Track("content://media/2","City Lights.mp4","video/mp4"));
  try(var c=Robolectric.buildActivity(PreviewLibrary.class).setup()){
   PreviewLibrary a=c.get();assertEquals(2,a.adapter.getCount());a.search.setText("Midnight");assertEquals(1,a.adapter.getCount());assertEquals("Midnight Drift.mp3",((LibraryStore.Track)a.adapter.getItem(0)).title);a.search.setText("");
   View root=a.findViewById(android.R.id.content);root.measure(View.MeasureSpec.makeMeasureSpec(1080,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(2200,View.MeasureSpec.EXACTLY));root.layout(0,0,1080,2200);Bitmap b=Bitmap.createBitmap(1080,2200,Bitmap.Config.ARGB_8888);root.draw(new Canvas(b));File dir=new File("build/player-screenshots");dir.mkdirs();try(FileOutputStream out=new FileOutputStream(new File(dir,"library-v1.6.png"))){b.compress(Bitmap.CompressFormat.PNG,100,out);}
   a.select(a.visible.get(0));assertEquals(Activity.RESULT_OK,Shadows.shadowOf(a).getResultCode());assertEquals("content://media/2",Shadows.shadowOf(a).getResultIntent().getStringExtra("uri"));
  }
 }
 @Test public void libraryReturnsToNativePlayerWithoutExternalApp(){try(var c=Robolectric.buildActivity(MainActivity.class).setup()){MainActivity a=c.get();a.library();Intent next=Shadows.shadowOf(a).getNextStartedActivity();assertEquals(LibraryActivity.class.getName(),next.getComponent().getClassName());assertEquals(View.VISIBLE,a.normalPlayer.getVisibility());a.prefs.edit().putBoolean("dots",true).commit();a.refreshPlayerStyle();assertEquals(View.VISIBLE,a.view.getVisibility());assertEquals(View.GONE,a.normalPlayer.getVisibility());}}
 @Test @GraphicsMode(GraphicsMode.Mode.NATIVE) @Config(qualifiers="w360dp-h800dp-xxhdpi") public void nativePlayerControlsHaveRealTouchTargets()throws Exception{try(var c=Robolectric.buildActivity(MainActivity.class).setup()){MainActivity a=c.get();NativePlayerView v=a.normalPlayer;v.measure(View.MeasureSpec.makeMeasureSpec(1080,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(2200,View.MeasureSpec.EXACTLY));v.layout(0,0,1080,2200);assertTrue(v.play.getHeight()>=v.dp(48));assertTrue(v.seek.getHeight()>=v.dp(40));assertEquals("Play",v.play.getContentDescription());Bitmap b=Bitmap.createBitmap(1080,2200,Bitmap.Config.ARGB_8888);v.draw(new Canvas(b));File dir=new File("build/player-screenshots");dir.mkdirs();try(FileOutputStream out=new FileOutputStream(new File(dir,"native-player-v1.6.png"))){b.compress(Bitmap.CompressFormat.PNG,100,out);}}}

}
