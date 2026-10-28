package com.noir.player;

import android.app.*;
import android.content.*;
import android.graphics.Bitmap;
import android.media.*;
import android.media.session.*;
import android.os.*;
import android.net.Uri;

public class PlaybackService extends Service {
 public static PlaybackService current;
 androidx.media3.exoplayer.ExoPlayer player; MediaSession session; AudioManager audio; AudioFocusRequest focus;
 public String title="Choose your soundtrack", error=""; public Bitmap artwork; public boolean loading=false;
 public boolean audioOnly=true; public String source=""; private int generation=0; private boolean resumeOnFocus; private boolean noisyRegistered;
 private final BroadcastReceiver noisy=new BroadcastReceiver(){public void onReceive(Context c,Intent i){pause();}};
 public void onCreate(){super.onCreate();current=this;audio=(AudioManager)getSystemService(AUDIO_SERVICE);
  focus=new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()).setOnAudioFocusChangeListener(change->{if(change==AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK){if(player!=null)player.setVolume(.2f);}else if(change<0){resumeOnFocus=change==AudioManager.AUDIOFOCUS_LOSS_TRANSIENT&&playing();pause();}else {if(player!=null)player.setVolume(1);if(resumeOnFocus){resumeOnFocus=false;play();}}}).build();
  session=new MediaSession(this,"Pulse Media Player");session.setCallback(new MediaSession.Callback(){public void onPlay(){play();} public void onPause(){pause();} public void onSeekTo(long p){seek((int)p);} public void onFastForward(){skip(10000);}public void onRewind(){skip(-10000);} public void onStop(){stopSelf();}});session.setActive(true);
  ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(new NotificationChannel("playback","Music playback",NotificationManager.IMPORTANCE_LOW));
  registerReceiver(noisy,new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY));noisyRegistered=true;
 }
 public int onStartCommand(Intent i,int flags,int id){startForeground(1,notification());if(i!=null){String a=i.getAction();if("load".equals(a))load(Uri.parse(i.getStringExtra("uri")),i.getStringExtra("title"));else if("toggle".equals(a))toggle();else if("back".equals(a))skip(-10000);else if("forward".equals(a))skip(10000);}return START_NOT_STICKY;}
 void load(Uri uri,String name){
  final int request=++generation; source=uri.toString();
  if(player!=null)player.release(); artwork=null; title=name; error="";loading=true;
  player=new androidx.media3.exoplayer.ExoPlayer.Builder(this).build();
  player.setTrackSelectionParameters(player.getTrackSelectionParameters().buildUpon().setTrackTypeDisabled(androidx.media3.common.C.TRACK_TYPE_VIDEO,audioOnly).build());
  player.addListener(new androidx.media3.common.Player.Listener(){
   public void onPlaybackStateChanged(int state){loading=state==androidx.media3.common.Player.STATE_BUFFERING;if(state==androidx.media3.common.Player.STATE_READY&&player.getPlayWhenReady())play();update();}
   public void onIsPlayingChanged(boolean active){update();}
   public void onPlayerError(androidx.media3.common.PlaybackException e){loading=false;error=e.getErrorCodeName()+": Check the URL, network or codec support.";Throwable cause=e;while(cause!=null){if(cause instanceof androidx.media3.datasource.HttpDataSource.InvalidResponseCodeException){error="Server returned HTTP "+((androidx.media3.datasource.HttpDataSource.InvalidResponseCodeException)cause).responseCode+". Use a direct playable media link.";break;}cause=cause.getCause();}update();}
   public void onMediaMetadataChanged(androidx.media3.common.MediaMetadata metadata){if(metadata.artworkData!=null){artwork=android.graphics.BitmapFactory.decodeByteArray(metadata.artworkData,0,metadata.artworkData.length);update();}}
  });
  player.setMediaItem(androidx.media3.common.MediaItem.fromUri(uri));player.prepare();play();update();
  new Thread(()->{Bitmap result=null;try{MediaMetadataRetriever r=new MediaMetadataRetriever();try{
   if("https".equals(uri.getScheme()))return;
   r.setDataSource(this,uri);byte[] bytes=r.getEmbeddedPicture();
   result=bytes!=null?android.graphics.BitmapFactory.decodeByteArray(bytes,0,bytes.length):r.getFrameAtTime(0);
  }finally{r.release();}}catch(Exception ignored){}
  final Bitmap image=result;new Handler(getMainLooper()).post(()->{if(request==generation){artwork=image;update();}});
  }).start();
 }
 public void setAudioOnly(boolean value){audioOnly=value;if(player!=null){player.setTrackSelectionParameters(player.getTrackSelectionParameters().buildUpon().setTrackTypeDisabled(androidx.media3.common.C.TRACK_TYPE_VIDEO,value).build());if(value)player.clearVideoSurface();}update();}
 public void setSurface(android.view.Surface surface){if(player!=null)player.setVideoSurface(surface);}
 public boolean playing(){try{return player!=null&&player.isPlaying();}catch(Exception e){return false;}}
 public int duration(){try{return player==null?0:(int)Math.max(0,player.getDuration());}catch(Exception e){return 0;}}
 public int position(){try{return player==null?0:(int)player.getCurrentPosition();}catch(Exception e){return 0;}}
 public void play(){if(player==null)return;if(audio.requestAudioFocus(focus)==AudioManager.AUDIOFOCUS_REQUEST_GRANTED){try{player.play();}catch(Exception ignored){}update();}}
 public void pause(){if(player!=null)try{player.pause();}catch(Exception ignored){}update();}
 public void toggle(){if(player!=null&&player.getPlayWhenReady())pause();else play();}
 public void seek(int p){if(player!=null&&!loading){try{player.seekTo(Math.max(0,Math.min(duration(),p)));}catch(Exception ignored){}update();}}
 public void skip(int delta){seek(position()+delta);}
 void update(){session.setMetadata(new MediaMetadata.Builder().putString(MediaMetadata.METADATA_KEY_TITLE,title).putString(MediaMetadata.METADATA_KEY_ARTIST,"Pulse Media Player").putLong(MediaMetadata.METADATA_KEY_DURATION,duration()).putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART,artwork).build());session.setPlaybackState(new PlaybackState.Builder().setActions(PlaybackState.ACTION_PLAY|PlaybackState.ACTION_PAUSE|PlaybackState.ACTION_PLAY_PAUSE|PlaybackState.ACTION_SEEK_TO|PlaybackState.ACTION_REWIND|PlaybackState.ACTION_FAST_FORWARD).setState(playing()?PlaybackState.STATE_PLAYING:PlaybackState.STATE_PAUSED,position(),playing()?1:0).build());((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).notify(1,notification());}
 PendingIntent action(String a){return PendingIntent.getService(this,a.hashCode(),new Intent(this,PlaybackService.class).setAction(a),PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_UPDATE_CURRENT);}
 Notification notification(){PendingIntent open=PendingIntent.getActivity(this,0,new Intent(this,MainActivity.class),PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_UPDATE_CURRENT);return new Notification.Builder(this,"playback").setSmallIcon(android.R.drawable.ic_media_play).setContentTitle(title).setContentText(!error.isEmpty()?error:loading?"Loading media…":"Pulse Media Player • Background playback").setLargeIcon(artwork).setContentIntent(open).setVisibility(Notification.VISIBILITY_PUBLIC).setOngoing(playing()).addAction(new Notification.Action.Builder(android.R.drawable.ic_media_rew,"Back 10 seconds",action("back")).build()).addAction(new Notification.Action.Builder(playing()?android.R.drawable.ic_media_pause:android.R.drawable.ic_media_play,playing()?"Pause":"Play",action("toggle")).build()).addAction(new Notification.Action.Builder(android.R.drawable.ic_media_ff,"Forward 10 seconds",action("forward")).build()).setStyle(new Notification.MediaStyle().setMediaSession(session.getSessionToken()).setShowActionsInCompactView(0,1,2)).build();}
 public IBinder onBind(Intent i){return null;}
 public void onDestroy(){if(player!=null)player.release();session.release();audio.abandonAudioFocusRequest(focus);if(noisyRegistered)unregisterReceiver(noisy);current=null;super.onDestroy();}
}
