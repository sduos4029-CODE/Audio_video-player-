package com.noir.player;
import org.junit.Test;
import static org.junit.Assert.*;
public class YouTubeNavigationTest {
 @Test public void permitsInAppVideoMusicAndConsentPages(){assertTrue(YouTubeNavigation.allowed("https://m.youtube.com/watch?v=dQw4w9WgXcQ"));assertTrue(YouTubeNavigation.allowed("https://music.youtube.com/"));assertTrue(YouTubeNavigation.allowed("https://accounts.google.com/"));}
 @Test public void blocksExternalAppSchemes(){for(String url:new String[]{"intent://watch?v=123","vnd.youtube:dQw4w9WgXcQ","market://details?id=com.google.android.youtube","file:///etc/passwd","javascript:alert(1)","http://youtube.com/"})assertFalse(url,YouTubeNavigation.allowed(url));}
 @Test public void blocksSpoofedHostsAndCredentials(){assertFalse(YouTubeNavigation.allowed("https://youtube.com.evil.test/watch"));assertFalse(YouTubeNavigation.allowed("https://youtube.com@evil.test/"));assertFalse(YouTubeNavigation.allowed("https://name@youtube.com/"));}
 @Test public void normalizesStartUrlsWithoutInjectingInput(){assertEquals("https://music.youtube.com/watch?v=dQw4w9WgXcQ",YouTubeNavigation.startUrl("dQw4w9WgXcQ",true));assertEquals("https://m.youtube.com/",YouTubeNavigation.startUrl("bad&id",false));assertEquals("https://music.youtube.com/",YouTubeNavigation.startUrl(null,true));}
}
