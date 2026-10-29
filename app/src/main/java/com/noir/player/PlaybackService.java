package com.noir.player;

import android.app.*;
import android.content.*;
import android.graphics.Bitmap;
import android.media.*;
import android.media.session.*;
import android.os.*;
import android.net.Uri;

@android.annotation.SuppressLint("UnsafeOptInUsageError")
public class PlaybackService extends Service {
 PlaybackQueue queue;
 final SketchEffect sketchEffect=new SketchEffect(false);boolean videoDecodingEnabled,useVideoEffects=true;public String effectNotice="";
 final NativeSpectrumCapture spectrumCapture=new NativeSpectrumCapture();
 public float[] frequencyBands(){return spectrumCapture.levels();}
 public float[] waveformSamples(){return spectrumCapture.waveform();}
 public static PlaybackService current;
 androidx.media3.exoplayer.ExoPlayer player; MediaSession session; AudioManager audio; AudioFocusRequest focus;
 public String title="Choose your soundtrack", error=""; public Bitmap artwork; public boolean loading=false;
 public String artist="";public boolean hasVideo;public boolean audioOnly=true;public boolean videoFrameReady;public float videoAspect=16/9f;private Object videoOwner;private java.lang.ref.WeakReference<android.view.TextureView> videoTexture=new java.lang.ref.WeakReference<>(null);private boolean metadataHasVideo; public String source=""; private int generation=0; private boolean resumeOnFocus; private boolean noisyRegistered;
 private final BroadcastReceiver noisy=new BroadcastReceiver(){public void onReceive(Context c,Intent i){pause();}};
 public void onCreate(){super.onCreate();current=this;sketchEffect.enabled=getSharedPreferences("noir",0).getBoolean("video_sketch",false);sketchEffect.style=SketchStyle.valid(getSharedPreferences("noir",0).getInt("video_sketch_style",SketchStyle.FINE_PENCIL));audio=(AudioManager)getSystemService(AUDIO_SERVICE);
  focus=new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()).setOnAudioFocusChangeListener(change->{if(change==AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK){if(player!=null)player.setVolume(.2f);}else if(change<0){resumeOnFocus=change==AudioManager.AUDIOFOCUS_LOSS_TRANSIENT&&playing();pause();}else {if(player!=null)player.setVolume(1);if(resumeOnFocus){resumeOnFocus=false;play();}}}).build();
  session=new MediaSession(this,"Pulse Media Player");session.setCallback(new MediaSession.Callback(){public void onPlay(){play();} public void onPause(){pause();} public void onSeekTo(long p){seek((int)p);} public void onFastForward(){skip(10000);}public void onRewind(){skip(-10000);} public void onSkipToPrevious(){previousTrack();}public void onSkipToNext(){nextTrack();} public void onStop(){stopSelf();}});session.setActive(true);
  ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(new NotificationChannel("playback","Music playback",NotificationManager.IMPORTANCE_LOW));
  registerReceiver(noisy,new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY));noisyRegistered=true;
 }
 public int onStartCommand(Intent i,int flags,int id){startForeground(1,notification());if(i!=null){String a=i.getAction();if("load".equals(a)){String uri=i.getStringExtra("uri"),name=i.getStringExtra("title");queue=new PlaybackQueue(i.getBooleanExtra("library_queue",false)?LibraryStore.read(getSharedPreferences("noir",0),"playback_queue"):java.util.Collections.emptyList(),uri,name);load(Uri.parse(uri),name);}else if("toggle".equals(a))toggle();else if("back".equals(a))skip(-10000);else if("forward".equals(a))skip(10000);else if("previous".equals(a))previousTrack();else if("next".equals(a))nextTrack();}return START_NOT_STICKY;}
 void load(Uri uri,String name){load(uri,name,0,true);}
 void load(Uri uri,String name,long startPosition,boolean startPlaying){
  if(WebPlaybackService.current!=null)WebPlaybackService.current.suspendForNative();session.setActive(true);
  final int request=++generation; source=uri.toString();
  if(player!=null)player.release(); artwork=null;artist="";hasVideo=false;metadataHasVideo=false;videoFrameReady=false;title=name; error="";loading=true;
  player=createPlayer();
  if(useVideoEffects)player.setVideoEffects(java.util.Collections.singletonList(sketchEffect));
  player.setTrackSelectionParameters(player.getTrackSelectionParameters().buildUpon().setTrackTypeDisabled(androidx.media3.common.C.TRACK_TYPE_VIDEO,!videoDecodingEnabled).build());android.view.TextureView output=videoTexture.get();if(videoDecodingEnabled&&output!=null)player.setVideoTextureView(output);
  player.addListener(new androidx.media3.common.Player.Listener(){
   public void onPlaybackStateChanged(int state){if(request!=generation)return;if(state==androidx.media3.common.Player.STATE_ENDED){onTrackEnded(request);return;}loading=state==androidx.media3.common.Player.STATE_BUFFERING;update();}
   public void onIsPlayingChanged(boolean active){if(request!=generation)return;spectrumCapture.setActive(active);update();}
   public void onPlayerError(androidx.media3.common.PlaybackException e){
    if(request!=generation)return;
    if(useVideoEffects&&(e.errorCode==androidx.media3.common.PlaybackException.ERROR_CODE_VIDEO_FRAME_PROCESSOR_INIT_FAILED||e.errorCode==androidx.media3.common.PlaybackException.ERROR_CODE_VIDEO_FRAME_PROCESSING_FAILED)){
     final long savedPosition=position();final boolean resume=player.getPlayWhenReady();useVideoEffects=false;sketchEffect.enabled=false;getSharedPreferences("noir",0).edit().putBoolean("video_sketch",false).apply();effectNotice="Sketch is unavailable on this device. Normal video rendering was restored.";
     new Handler(getMainLooper()).post(()->{if(request==generation)load(uri,name,savedPosition,resume);});return;
    }
    loading=false;error=PlaybackDetails.failure(e);update();
   }

   public void onTracksChanged(androidx.media3.common.Tracks tracks){if(request!=generation)return;hasVideo=metadataHasVideo;for(androidx.media3.common.Tracks.Group group:tracks.getGroups())if(group.getType()==androidx.media3.common.C.TRACK_TYPE_VIDEO)hasVideo=true;}
   public void onVideoSizeChanged(androidx.media3.common.VideoSize size){if(request==generation&&size.width>0&&size.height>0)videoAspect=size.width*size.pixelWidthHeightRatio/size.height;}
   public void onRenderedFirstFrame(){if(request==generation&&videoOwner!=null)videoFrameReady=true;}
   public void onMediaMetadataChanged(androidx.media3.common.MediaMetadata metadata){if(request!=generation)return;if(metadata.artist!=null)artist=metadata.artist.toString();if(metadata.artworkData!=null){artwork=Artwork.decode(metadata.artworkData,512);update();}}
  });
  if(startPosition>0)player.setMediaItem(androidx.media3.common.MediaItem.fromUri(uri),startPosition);else player.setMediaItem(androidx.media3.common.MediaItem.fromUri(uri));player.prepare();if(startPlaying)play();update();
  new Thread(()->{Bitmap result=null;boolean video=false;try{MediaMetadataRetriever r=new MediaMetadataRetriever();try{
   if("https".equals(uri.getScheme()))return;
   r.setDataSource(this,uri);video="yes".equalsIgnoreCase(r.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO));byte[] bytes=r.getEmbeddedPicture();
   result=bytes!=null?Artwork.decode(bytes,512):Build.VERSION.SDK_INT>=27?r.getScaledFrameAtTime(0,MediaMetadataRetriever.OPTION_CLOSEST_SYNC,512,512):null;
  }finally{r.release();}}catch(Exception ignored){}
  final Bitmap image=result;final boolean containsVideo=video;new Handler(getMainLooper()).post(()->{if(request==generation){if(image!=null)artwork=image;metadataHasVideo=containsVideo;hasVideo|=containsVideo;update();}});
  }).start();
 }
 public void suspendForWeb(){pause();session.setActive(false);stopForeground(STOP_FOREGROUND_REMOVE);}
 /** Media3 owns the TextureView callbacks, including resize and surface recreation. */
 androidx.media3.exoplayer.ExoPlayer createPlayer(){return new androidx.media3.exoplayer.ExoPlayer.Builder(this,new androidx.media3.exoplayer.DefaultRenderersFactory(this){protected androidx.media3.exoplayer.audio.AudioSink buildAudioSink(Context context,boolean floats,boolean playbackParams){return new androidx.media3.exoplayer.audio.DefaultAudioSink.Builder(context).setEnableFloatOutput(false).setEnableAudioTrackPlaybackParams(playbackParams).setAudioProcessors(new androidx.media3.common.audio.AudioProcessor[]{new androidx.media3.exoplayer.audio.TeeAudioProcessor(spectrumCapture)}).build();}}.setEnableDecoderFallback(true)).build();}
 /** True here means background decoding is stopped, not a foreground cover switch. */
 public void setAudioOnly(boolean value){
  boolean changed=audioOnly!=value||videoDecodingEnabled==value;
  audioOnly=value;if(!changed)return;
  android.view.TextureView output=videoTexture.get();
  if(!value&&output!=null&&player!=null)player.setVideoTextureView(output);
  setVideoDecodingEnabled(!value);
  if(value){videoFrameReady=false;if(player!=null&&output!=null)player.clearVideoTextureView(output);}
  update();
 }
 private void setVideoDecodingEnabled(boolean value){if(videoDecodingEnabled==value)return;videoDecodingEnabled=value;if(player!=null)player.setTrackSelectionParameters(player.getTrackSelectionParameters().buildUpon().setTrackTypeDisabled(androidx.media3.common.C.TRACK_TYPE_VIDEO,!value).build());}
 public void attachVideoTextureView(Object owner,android.view.TextureView view){setForegroundOutput(owner,view,false);}
 public void keepVideoWarm(Object owner,android.view.TextureView view){setForegroundOutput(owner,view,true);}
 private void setForegroundOutput(Object owner,android.view.TextureView view,boolean audioMode){
  if(owner==null||view==null)return;
  boolean same=owner==videoOwner&&view==videoTexture.get();if(same&&audioOnly==audioMode&&videoDecodingEnabled)return;
  boolean changed=audioOnly!=audioMode;audioOnly=audioMode;
  if(!same){videoOwner=owner;videoTexture=new java.lang.ref.WeakReference<>(view);videoFrameReady=false;if(player!=null)player.setVideoTextureView(view);}
  setVideoDecodingEnabled(true);if(changed)update();
 }
 public float currentVideoAspect(){androidx.media3.common.Format format=player==null?null:player.getVideoFormat();if(format!=null&&format.width>0&&format.height>0)return VideoGeometry.sourceAspect(format.width,format.height,format.pixelWidthHeightRatio,format.rotationDegrees);return videoAspect;}
 public int sketchStyle(){return sketchEffect.style;}
 public void setSketchStyle(int value){if(!useVideoEffects)return;sketchEffect.style=SketchStyle.valid(value);setSketchEnabled(true);getSharedPreferences("noir",0).edit().putInt("video_sketch_style",sketchEffect.style).apply();}
 public boolean sketchEnabled(){return useVideoEffects&&sketchEffect.enabled;}
 public boolean canSketch(){return useVideoEffects;}
 public void setSketchEnabled(boolean value){if(value&&!useVideoEffects)return;sketchEffect.enabled=value;if(player!=null&&!player.getPlayWhenReady())player.seekTo(player.getCurrentPosition());getSharedPreferences("noir",0).edit().putBoolean("video_sketch",value).apply();}

 public boolean releaseVideoSurface(Object owner){if(owner!=videoOwner||owner==null)return false;android.view.TextureView output=videoTexture.get();if(player!=null&&output!=null)player.clearVideoTextureView(output);videoOwner=null;videoTexture.clear();videoFrameReady=false;return true;}
 public boolean isVideoSurfaceOwner(Object owner){return owner!=null&&owner==videoOwner&&videoTexture.get()!=null;}
 public boolean hasVideoSurface(){return videoOwner!=null&&videoTexture.get()!=null;}

 public boolean playing(){try{return player!=null&&player.isPlaying();}catch(Exception e){return false;}}
 public int duration(){try{return player==null?0:(int)Math.max(0,player.getDuration());}catch(Exception e){return 0;}}
 public int position(){try{return player==null?0:(int)player.getCurrentPosition();}catch(Exception e){return 0;}}
 public void play(){if(player==null)return;if(WebPlaybackService.current!=null)WebPlaybackService.current.suspendForNative();session.setActive(true);startForeground(1,notification());if(audio.requestAudioFocus(focus)==AudioManager.AUDIOFOCUS_REQUEST_GRANTED){try{player.play();}catch(Exception ignored){}update();}}
 public void pause(){spectrumCapture.setActive(false);if(player!=null)try{player.pause();}catch(Exception ignored){}update();}
 public void toggle(){if(player!=null&&player.getPlayWhenReady())pause();else play();}
 public void seek(int p){if(player!=null&&!loading){try{player.seekTo(Math.max(0,Math.min(duration(),p)));}catch(Exception ignored){}update();}}
 public void skip(int delta){seek(position()+delta);}
 public boolean hasPreviousTrack(){return queue!=null&&queue.hasPrevious();}
 public boolean hasNextTrack(){return queue!=null&&queue.hasNext();}
 public void previousTrack(){moveTrack(-1);}
 public void nextTrack(){moveTrack(1);}
 private void moveTrack(int direction){LibraryStore.Track next=queue==null?null:queue.move(direction);if(next!=null)load(Uri.parse(next.uri),next.title);}
 void onTrackEnded(int request){if(request!=generation)return;loading=false;spectrumCapture.setActive(false);update();new Handler(getMainLooper()).post(()->{if(request==generation)nextTrack();});}
 void update(){session.setMetadata(new MediaMetadata.Builder().putString(MediaMetadata.METADATA_KEY_TITLE,title).putString(MediaMetadata.METADATA_KEY_ARTIST,"Pulse Media Player").putLong(MediaMetadata.METADATA_KEY_DURATION,duration()).putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART,artwork).build());session.setPlaybackState(new PlaybackState.Builder().setActions(PlaybackState.ACTION_PLAY|PlaybackState.ACTION_PAUSE|PlaybackState.ACTION_PLAY_PAUSE|PlaybackState.ACTION_SEEK_TO|PlaybackState.ACTION_REWIND|PlaybackState.ACTION_FAST_FORWARD|(hasPreviousTrack()?PlaybackState.ACTION_SKIP_TO_PREVIOUS:0)|(hasNextTrack()?PlaybackState.ACTION_SKIP_TO_NEXT:0)).setState(playing()?PlaybackState.STATE_PLAYING:PlaybackState.STATE_PAUSED,position(),playing()?1:0).build());((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).notify(1,notification());}
 PendingIntent action(String a){return PendingIntent.getService(this,a.hashCode(),new Intent(this,PlaybackService.class).setAction(a),PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_UPDATE_CURRENT);}
 Notification notification(){
  PendingIntent open=PendingIntent.getActivity(this,0,new Intent(this,MainActivity.class),PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_UPDATE_CURRENT);
  Notification.Builder builder=new Notification.Builder(this,"playback").setSmallIcon(android.R.drawable.ic_media_play).setContentTitle(title).setContentText(!error.isEmpty()?error:loading?"Loading media…":"Pulse Media Player • Background playback").setLargeIcon(artwork).setContentIntent(open).setVisibility(Notification.VISIBILITY_PUBLIC).setOngoing(playing());
  int index=0,previous=-1,next=-1;
  if(hasPreviousTrack()){previous=index++;builder.addAction(new Notification.Action.Builder(android.R.drawable.ic_media_previous,"Previous track",action("previous")).build());}
  int rewind=index++;builder.addAction(new Notification.Action.Builder(android.R.drawable.ic_media_rew,"Back 10 seconds",action("back")).build());
  int toggle=index++;builder.addAction(new Notification.Action.Builder(playing()?android.R.drawable.ic_media_pause:android.R.drawable.ic_media_play,playing()?"Pause":"Play",action("toggle")).build());
  int forward=index++;builder.addAction(new Notification.Action.Builder(android.R.drawable.ic_media_ff,"Forward 10 seconds",action("forward")).build());
  if(hasNextTrack()){next=index;builder.addAction(new Notification.Action.Builder(android.R.drawable.ic_media_next,"Next track",action("next")).build());}
  return builder.setStyle(new Notification.MediaStyle().setMediaSession(session.getSessionToken()).setShowActionsInCompactView(previous>=0?previous:rewind,toggle,next>=0?next:forward)).build();
 }

 public IBinder onBind(Intent i){return null;}
 public void onDestroy(){generation++;videoOwner=null;videoTexture.clear();spectrumCapture.close();if(player!=null)player.release();session.release();audio.abandonAudioFocusRequest(focus);if(noisyRegistered)unregisterReceiver(noisy);current=null;super.onDestroy();}
}
