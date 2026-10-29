package com.noir.player;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.*;

/** Full-screen video shares the service player and the home player's overlay controls. */
public class VideoActivity extends Activity {
 VideoPane pane;
 @Override protected void onCreate(Bundle state){super.onCreate(state);pane=new VideoPane(this,true);pane.onBack=this::finish;pane.onExpand=this::finish;pane.onRotate=this::rotateVideo;pane.setVideoRequested(state==null||state.getBoolean("video",true),true);setContentView(pane);if(getIntent().getBooleanExtra("landscape",false)&&state==null)setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);immersive();}
 void immersive(){if(Build.VERSION.SDK_INT>=30){WindowInsetsController c=getWindow().getInsetsController();if(c!=null){c.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);c.hide(WindowInsets.Type.systemBars());}}else getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
 void rotateVideo(){boolean landscape=getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE;setRequestedOrientation(landscape?ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT:ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);}
 @Override public void onConfigurationChanged(Configuration config){super.onConfigurationChanged(config);pane.requestLayout();immersive();}
 @Override protected void onResume(){super.onResume();pane.setHostActive(true);pane.showControls();immersive();}
 @Override protected void onPause(){pane.setHostActive(false);super.onPause();}
 @Override protected void onSaveInstanceState(Bundle state){state.putBoolean("video",pane.videoRequested);super.onSaveInstanceState(state);}
}
