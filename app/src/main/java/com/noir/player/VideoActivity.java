package com.noir.player;

import android.app.Activity;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import android.graphics.Color;
import androidx.media3.common.Player;
import androidx.media3.common.VideoSize;

/** Shares the service player; detaches video on lock while audio continues. */
public class VideoActivity extends Activity implements SurfaceHolder.Callback {
 SurfaceView surface; FrameLayout viewport; int videoWidth=16,videoHeight=9;
 final Player.Listener listener=new Player.Listener(){public void onVideoSizeChanged(VideoSize size){videoWidth=size.width;videoHeight=size.height;fit();}};
 protected void onCreate(Bundle b){
  super.onCreate(b);LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setBackgroundColor(Color.BLACK);
  viewport=new FrameLayout(this);surface=new SurfaceView(this);surface.getHolder().addCallback(this);viewport.addView(surface,new FrameLayout.LayoutParams(-1,-1,Gravity.CENTER));root.addView(viewport,new LinearLayout.LayoutParams(-1,0,1));viewport.addOnLayoutChangeListener((v,l,t,r,bottom,ol,ot,or,ob)->fit());
  Button audio=new Button(this);audio.setText("Audio only");audio.setOnClickListener(v->{if(PlaybackService.current!=null)PlaybackService.current.setAudioOnly(true);finish();});root.addView(audio);
  Button pause=new Button(this);pause.setText("Play / Pause");pause.setOnClickListener(v->{if(PlaybackService.current!=null)PlaybackService.current.toggle();});root.addView(pause);setContentView(root);
  if(PlaybackService.current!=null&&PlaybackService.current.player!=null)PlaybackService.current.player.addListener(listener);
 }
 void fit(){if(videoWidth<=0||videoHeight<=0||viewport.getWidth()==0||viewport.getHeight()==0)return;float factor=Math.min(viewport.getWidth()/(float)videoWidth,viewport.getHeight()/(float)videoHeight);FrameLayout.LayoutParams p=(FrameLayout.LayoutParams)surface.getLayoutParams();int w=(int)(videoWidth*factor),h=(int)(videoHeight*factor);if(p.width!=w||p.height!=h){p.width=w;p.height=h;p.gravity=Gravity.CENTER;surface.setLayoutParams(p);}}
 public void surfaceCreated(SurfaceHolder h){attach();}
 public void surfaceChanged(SurfaceHolder h,int f,int w,int height){attach();}
 public void surfaceDestroyed(SurfaceHolder h){if(PlaybackService.current!=null)PlaybackService.current.setSurface(null);}
 void attach(){if(PlaybackService.current!=null){PlaybackService.current.setAudioOnly(false);PlaybackService.current.setSurface(surface.getHolder().getSurface());}}
 protected void onResume(){super.onResume();if(surface!=null&&surface.getHolder().getSurface().isValid())attach();}
 protected void onStop(){super.onStop();if(PlaybackService.current!=null)PlaybackService.current.setAudioOnly(true);}
 protected void onDestroy(){if(PlaybackService.current!=null&&PlaybackService.current.player!=null)PlaybackService.current.player.removeListener(listener);super.onDestroy();}
}
