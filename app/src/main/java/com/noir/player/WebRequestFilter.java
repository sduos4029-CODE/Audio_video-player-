package com.noir.player;
import java.net.URI;
import java.util.Locale;
/** Small, conservative browser filter. It cannot identify all same-origin video ads. */
public final class WebRequestFilter {
 private static final String[] HOSTS={"doubleclick.net","googlesyndication.com","googleadservices.com","adservice.google.com"};
 private WebRequestFilter(){}
 public static boolean blocked(String url){try{URI u=new URI(url);String host=u.getHost();if(host==null)return false;host=host.toLowerCase(Locale.ROOT);for(String domain:HOSTS)if(host.equals(domain)||host.endsWith("."+domain))return true;String path=u.getPath();return (host.equals("youtube.com")||host.endsWith(".youtube.com"))&&path!=null&&(path.startsWith("/pagead/")||path.equals("/api/stats/ads"));}catch(Exception e){return false;}}
}
