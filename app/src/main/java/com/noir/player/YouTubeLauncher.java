package com.noir.player;

import android.app.Activity;
import android.content.*;
import android.net.Uri;

/** Launch official apps with a browser fallback; never embeds or extracts media. */
public final class YouTubeLauncher {
 private YouTubeLauncher() {}
 public static boolean open(Context context,String id,boolean music){
  if(id==null||!id.matches("[A-Za-z0-9_-]{11}"))return false;
  String first=music?"com.google.android.apps.youtube.music":"com.google.android.youtube";
  String second=music?"com.google.android.youtube":"com.google.android.apps.youtube.music";
  String[] packages={first,second,null};
  for(String pkg:packages){
   boolean useMusic="com.google.android.apps.youtube.music".equals(pkg)||(pkg==null&&music);
   Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse("https://"+(useMusic?"music.youtube.com":"www.youtube.com")+"/watch?v="+id));
   if(pkg!=null)intent.setPackage(pkg);
   if(!(context instanceof Activity))intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
   try{context.startActivity(intent);if(PlaybackService.current!=null)PlaybackService.current.pause();return true;}
   catch(ActivityNotFoundException|SecurityException ignored){/* Try next installed app. */}
  }
  return false;
 }
}
