import com.noir.player.NativeAdblock;
import java.nio.file.*;

/** Runs the actual JNI library in a host JVM, independently of Android test shadows. */
public final class NativeAdblockSmoke {
 static void check(boolean value,String message){if(!value)throw new AssertionError(message);}
 static boolean blocked(String url,String source,String kind){String result=NativeAdblock.matchRequest(url,source,kind,"GET");return result.contains("\"blocked\":true");}
 public static void main(String[] args)throws Exception{
  check(NativeAdblock.AVAILABLE,"Native library must load");
  check(NativeAdblock.initialize("||ads.example^\n@@||ads.example/allowed.js$script", "[]"),"Fixture engine initialization");
  check(blocked("https://ads.example/ad.js","https://m.youtube.com/","script"),"Ad must be blocked");
  check(!blocked("https://ads.example/allowed.js","https://m.youtube.com/","script"),"Exception must be respected");
  Path root=Path.of(args[0]);long start=System.nanoTime();
  check(NativeAdblock.initialize(Files.readString(root.resolve("rules.txt")),Files.readString(root.resolve("resources.json"))),"Bundled rules must initialize");
  long millis=(System.nanoTime()-start)/1_000_000;
  check(blocked("https://pubads.g.doubleclick.net/gampad/ads","https://m.youtube.com/","xmlhttprequest"),"Bundled list must block known ad request");
  check(!blocked("https://rr1.googlevideo.com/videoplayback?source=youtube&id=fixture","https://m.youtube.com/","media"),"Normal media must stay available");
  check(!blocked("https://www.youtube.com/youtubei/v1/player?prettyPrint=false","https://m.youtube.com/","xmlhttprequest"),"Player API must stay available");
  check(!blocked("https://i.ytimg.com/vi/fixture/hqdefault.jpg","https://m.youtube.com/","image"),"Thumbnails must stay available");
  check(!blocked("https://accounts.google.com/ServiceLogin","https://accounts.google.com/","document"),"Login page must stay available");
  for(String host:new String[]{"m.youtube.com","www.youtube.com","music.youtube.com"}){
   String page=NativeAdblock.pageResources("https://"+host+"/watch?v=fixture");
   check(page.contains("injected_script")&&page.contains("jsonPrune"),"Actual page scriptlets must be resolved for "+host);
  }
  System.out.println("Native JNI smoke checks passed; bundled initialization "+millis+" ms on this host. Live ads are not tested.");
 }
}
