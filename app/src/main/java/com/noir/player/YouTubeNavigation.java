package com.noir.player;
import java.net.URI;
import java.util.Locale;
/** Main-frame navigation stays on supported HTTPS sites, never in another app. */
public final class YouTubeNavigation {
 private YouTubeNavigation(){}
 public static boolean allowed(String url){
  try{URI uri=new URI(url);if(!"https".equalsIgnoreCase(uri.getScheme())||uri.getUserInfo()!=null)return false;String host=uri.getHost();if(host==null)return false;host=host.toLowerCase(Locale.ROOT);return host.equals("youtube.com")||host.endsWith(".youtube.com")||host.equals("youtu.be")||host.equals("accounts.google.com")||host.equals("consent.google.com")||host.equals("myaccount.google.com");}catch(Exception e){return false;}
 }
 public static String startUrl(String id,boolean music){String home=music?"https://music.youtube.com/":"https://m.youtube.com/";return id!=null&&id.matches("[A-Za-z0-9_-]{11}")?home+"watch?v="+id:home;}
}
