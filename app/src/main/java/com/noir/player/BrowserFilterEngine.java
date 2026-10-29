package com.noir.player;

import android.content.Context;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
import org.json.*;

/** Asset-pinned network filtering. Waits for initialization only on WebView's request thread. */
final class BrowserFilterEngine {
 private static CompletableFuture<Boolean> ready;
 private volatile String state="Loading network rules";
 BrowserFilterEngine(Context context){Context app=context.getApplicationContext();synchronized(BrowserFilterEngine.class){if(ready==null)ready=CompletableFuture.supplyAsync(()->{if(!NativeAdblock.AVAILABLE)return false;try{return NativeAdblock.initialize(asset(app,"adblock/rules.txt",8_000_000),asset(app,"adblock/resources.json",3_000_000));}catch(IOException|RuntimeException|LinkageError e){return false;}});}}
 static String asset(Context context,String name,int limit)throws IOException{try(InputStream in=context.getAssets().open(name);ByteArrayOutputStream out=new ByteArrayOutputStream()){byte[] buffer=new byte[8192];int n;while((n=in.read(buffer))!=-1){if(out.size()+n>limit)throw new IOException("Asset too large");out.write(buffer,0,n);}return out.toString(StandardCharsets.UTF_8.name());}}
 WebResourceResponse response(WebResourceRequest request,String page){
  if(request.isForMainFrame()||!YouTubeNavigation.mediaPage(page))return null;
  String url=request.getUrl().toString();String host=request.getUrl().getHost();if(host==null||host.equals("accounts.google.com")||host.equals("oauth2.googleapis.com"))return null;
  boolean loaded=false;try{loaded=ready.get(8,TimeUnit.SECONDS);}catch(InterruptedException e){Thread.currentThread().interrupt();}catch(ExecutionException|TimeoutException ignored){}
  state=loaded?"adblock-rust 0.13.5 ready":"Native engine unavailable; fallback rules active";
  if(!loaded)return WebRequestFilter.blocked(url)?empty():null;
  try{Map<String,String> headers=request.getRequestHeaders();String source=page;for(Map.Entry<String,String> header:headers.entrySet())if(header.getKey().equalsIgnoreCase("Referer")&&YouTubeNavigation.mediaPage(header.getValue()))source=header.getValue();
   JSONObject result=new JSONObject(NativeAdblock.matchRequest(url,source,resourceType(request),request.getMethod()));if(!result.optBoolean("blocked",false))return null;
   String redirect=result.optString("redirect","");WebResourceResponse replacement=dataResponse(redirect);return replacement!=null?replacement:empty();
  }catch(JSONException|RuntimeException|LinkageError e){return null;}
 }
 String status(){if(ready!=null&&ready.isDone()&&state.startsWith("Loading"))state=Boolean.TRUE.equals(ready.getNow(false))?"adblock-rust 0.13.5 ready":"Native engine unavailable; fallback rules active";return state;}
 static WebResourceResponse empty(){return new WebResourceResponse("text/plain","UTF-8",204,"No Content",Collections.emptyMap(),new ByteArrayInputStream(new byte[0]));}
 static String resourceType(WebResourceRequest request){
  String destination="",accept="";for(Map.Entry<String,String> h:request.getRequestHeaders().entrySet()){if(h.getKey().equalsIgnoreCase("Sec-Fetch-Dest"))destination=h.getValue();if(h.getKey().equalsIgnoreCase("Accept"))accept=h.getValue();}
  switch(destination){case "script":return "script";case "style":return "stylesheet";case "image":return "image";case "font":return "font";case "video":case "audio":return "media";case "iframe":case "frame":return "subdocument";}
  String path=request.getUrl().getPath();if(path==null)return "other";path=path.toLowerCase(Locale.ROOT);
  if(path.endsWith(".js")||path.endsWith(".mjs"))return "script";if(path.endsWith(".css"))return "stylesheet";if(path.matches(".*\\.(png|jpe?g|gif|webp|svg|avif)$")||accept.startsWith("image/"))return "image";if(path.matches(".*\\.(woff2?|ttf|otf)$"))return "font";if(path.contains("videoplayback")||path.matches(".*\\.(mp4|webm|mp3|m4a|ogg|m3u8|mpd)$"))return "media";if(path.startsWith("/youtubei/")||accept.contains("application/json")||request.getMethod().equals("POST"))return "xmlhttprequest";return "other";
 }
 static WebResourceResponse dataResponse(String value){if(value==null||!value.startsWith("data:")||value.length()>1_400_000)return null;int comma=value.indexOf(',');if(comma<0)return null;String header=value.substring(5,comma);if(!header.endsWith(";base64"))return null;String mime=header.substring(0,header.length()-7);try{byte[] bytes=android.util.Base64.decode(value.substring(comma+1),android.util.Base64.DEFAULT);return new WebResourceResponse(mime,"UTF-8",200,"OK",Collections.emptyMap(),new ByteArrayInputStream(bytes));}catch(IllegalArgumentException e){return null;}}
}
