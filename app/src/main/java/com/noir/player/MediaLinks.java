package com.noir.player;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/** Parses provider links without treating a web page as a media stream. */
public final class MediaLinks {
 private MediaLinks() {}
 public static String youtubeId(String input) {
  try {
   URI uri=new URI(input.trim());
   if(!"https".equalsIgnoreCase(uri.getScheme())) return null;
   String host=uri.getHost(); if(host==null)return null;
   host=host.toLowerCase(java.util.Locale.ROOT);String id=null;
   if(host.equals("youtu.be")) id=uri.getPath().substring(1).split("/")[0];
   else if(host.equals("youtube.com")||host.equals("www.youtube.com")||host.equals("m.youtube.com")){
    String path=uri.getPath();
    if(path.startsWith("/shorts/")||path.startsWith("/embed/")||path.startsWith("/live/"))id=path.split("/")[2];
    else if(path.equals("/watch")&&uri.getRawQuery()!=null)for(String pair:uri.getRawQuery().split("&")){String[] kv=pair.split("=",2);if(kv.length==2&&kv[0].equals("v"))id=URLDecoder.decode(kv[1],StandardCharsets.UTF_8.name());}
   }
   return id!=null&&id.matches("[A-Za-z0-9_-]{11}")?id:null;
  }catch(Exception ignored){return null;}
 }
 public static boolean isYouTubeHost(String input){try{String h=new URI(input).getHost();return h!=null&&(h.equalsIgnoreCase("youtu.be")||h.equalsIgnoreCase("youtube.com")||h.toLowerCase(java.util.Locale.ROOT).endsWith(".youtube.com"));}catch(Exception e){return false;}}
}
