package com.noir.player;

import android.app.Notification;
import android.content.Intent;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Looper;
import android.graphics.*;
import android.view.*;
import java.io.*;
import java.util.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk=28,shadows=YouTubeScreenTest.RecordingSession.class)
public class TrackNavigationTest {
 static List<LibraryStore.Track> tracks(){return Arrays.asList(new LibraryStore.Track("content://media/a","A.mp3","audio/mpeg"),new LibraryStore.Track("content://media/b","B.mp3","audio/mpeg"),new LibraryStore.Track("content://media/c","C.mp3","audio/mpeg"));}
 public static class PreviewPlayback extends PlaybackService {
  int loads,seekDelta;
  @Override void load(Uri uri,String name){source=uri.toString();title=name;loads++;update();}
  @Override public void skip(int delta){seekDelta+=delta;}
 }
 void start(PreviewPlayback s,String uri,boolean library){LibraryStore.write(s.getSharedPreferences("noir",0),"playback_queue",tracks());s.onStartCommand(new Intent(s,PlaybackService.class).setAction("load").putExtra("uri",uri).putExtra("title","Selected").putExtra("library_queue",library),0,1);}

 @Test public void queuePreservesSelectionOrderAndStopsAtEdges(){List<LibraryStore.Track> original=new ArrayList<>(tracks());PlaybackQueue q=new PlaybackQueue(original,"content://media/b","B");original.clear();assertTrue(q.hasPrevious());assertTrue(q.hasNext());assertEquals("C.mp3",q.move(1).title);assertFalse(q.hasNext());assertNull(q.move(1));assertEquals("B.mp3",q.move(-1).title);assertEquals("A.mp3",q.move(-1).title);assertFalse(q.hasPrevious());assertNull(q.move(-1));}
 @Test public void missingOrStandaloneSourceCannotUseStaleQueue(){PlaybackQueue q=new PlaybackQueue(tracks(),"https://example.com/live.mp3","Direct");assertFalse(q.hasPrevious());assertFalse(q.hasNext());assertNull(q.move(1));var service=Robolectric.buildService(PreviewPlayback.class).create();try{PreviewPlayback s=service.get();start(s,"content://media/b",false);s.nextTrack();assertEquals(1,s.loads);assertFalse(s.hasNextTrack());}finally{service.destroy();}}
 @Test public void notificationAndSessionSwitchTracksAndKeepTenSecondSeeking(){var controller=Robolectric.buildService(PreviewPlayback.class).create();try{PreviewPlayback s=controller.get();start(s,"content://media/b",true);s.update();Notification n=s.notification();assertEquals(5,n.actions.length);assertEquals("Previous track",n.actions[0].title);assertEquals("Back 10 seconds",n.actions[1].title);assertEquals("Forward 10 seconds",n.actions[3].title);assertEquals("Next track",n.actions[4].title);PlaybackState state=((YouTubeScreenTest.RecordingSession)org.robolectric.shadow.api.Shadow.extract(s.session)).lastState;assertTrue((state.getActions()&PlaybackState.ACTION_SKIP_TO_NEXT)!=0);assertTrue((state.getActions()&PlaybackState.ACTION_SKIP_TO_PREVIOUS)!=0);
  n.actions[4].actionIntent.send();Intent next=Shadows.shadowOf(s).getNextStartedService();assertEquals("next",next.getAction());s.onStartCommand(next,0,2);assertEquals("content://media/c",s.source);assertFalse(s.hasNextTrack());s.onStartCommand(new Intent().setAction("previous"),0,3);assertEquals("content://media/b",s.source);s.onStartCommand(new Intent().setAction("forward"),0,4);assertEquals(10000,s.seekDelta);
 }catch(Exception e){throw new AssertionError(e);}finally{controller.destroy();}}
 @Test public void endOfTrackAdvancesOnceAndLastTrackStops(){var controller=Robolectric.buildService(PreviewPlayback.class).create();try{PreviewPlayback s=controller.get();start(s,"content://media/b",true);s.onTrackEnded(0);Shadows.shadowOf(Looper.getMainLooper()).idle();assertEquals("content://media/c",s.source);assertEquals(2,s.loads);s.onTrackEnded(0);Shadows.shadowOf(Looper.getMainLooper()).idle();assertEquals(2,s.loads);s.onTrackEnded(99);Shadows.shadowOf(Looper.getMainLooper()).idle();assertEquals(2,s.loads);}finally{controller.destroy();}}
 @Test public void filteredLibrarySnapshotIsUsedWithoutChangingSavedFiles(){try(var c=Robolectric.buildActivity(LibraryActivity.class).setup()){LibraryActivity a=c.get();LibraryStore.write(a.prefs,tracks());a.search.setText("B.mp3");a.select(a.visible.get(0));assertEquals(1,LibraryStore.read(a.prefs,"playback_queue").size());assertEquals(3,LibraryStore.read(a.prefs).size());}}
 @Test @GraphicsMode(GraphicsMode.Mode.NATIVE) @Config(qualifiers="w360dp-h800dp-xxhdpi") public void normalAndMatrixTrackControlsDoNotSeek()throws Exception{var service=Robolectric.buildService(PreviewPlayback.class).create();try(var activity=Robolectric.buildActivity(MainActivity.class).setup()){PreviewPlayback s=service.get();start(s,"content://media/b",true);MainActivity a=activity.get();NativePlayerView v=a.normalPlayer;v.render();v.previousTrack.performClick();assertEquals("content://media/a",s.source);assertFalse(v.previousTrack.isEnabled());v.nextTrack.performClick();assertEquals("content://media/b",s.source);assertEquals(0,s.seekDelta);
  v.measure(View.MeasureSpec.makeMeasureSpec(1080,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(2200,View.MeasureSpec.EXACTLY));v.layout(0,0,1080,2200);assertTrue(v.previousTrack.getHeight()>=v.dp(48));assertTrue(v.nextTrack.getHeight()>=v.dp(48));save(v,1080,2200,"track-controls-normal-v1.8.png");
  a.prefs.edit().putBoolean("dots",true).apply();a.refreshPlayerStyle();a.view.layout(0,0,1080,2220);save(a.view,1080,2220,"track-controls-dots-v1.8.png");tap(a.view,321,566);assertEquals("content://media/c",s.source);tap(a.view,39,566);assertEquals("content://media/b",s.source);assertEquals(0,s.seekDelta);tap(a.view,102,566);assertEquals(-10000,s.seekDelta);
 }finally{service.destroy();}}
 static void tap(PlayerView v,float x,float y){MotionEvent e=MotionEvent.obtain(0,0,MotionEvent.ACTION_UP,x*3,y*3,0);try{v.onTouchEvent(e);}finally{e.recycle();}}
 static void save(View view,int w,int h,String name)throws IOException{Bitmap image=Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);view.draw(new Canvas(image));File dir=new File("build/player-screenshots");dir.mkdirs();try(var out=new FileOutputStream(new File(dir,name))){image.compress(Bitmap.CompressFormat.PNG,100,out);}}
}
