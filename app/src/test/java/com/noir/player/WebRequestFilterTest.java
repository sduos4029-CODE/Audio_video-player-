package com.noir.player;
import org.junit.Test;
import static org.junit.Assert.*;
public class WebRequestFilterTest {
 @Test public void matchesKnownAdHostsAndStatsPaths(){assertTrue(WebRequestFilter.blocked("https://pubads.g.doubleclick.net/gampad/ads"));assertTrue(WebRequestFilter.blocked("https://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js"));assertTrue(WebRequestFilter.blocked("https://www.youtube.com/api/stats/ads?x=1"));}
 @Test public void preservesMediaPlayerAndLoginEndpoints(){assertFalse(WebRequestFilter.blocked("https://rr1.googlevideo.com/videoplayback?source=youtube"));assertFalse(WebRequestFilter.blocked("https://www.youtube.com/youtubei/v1/player"));assertFalse(WebRequestFilter.blocked("https://accounts.google.com/ServiceLogin"));assertFalse(WebRequestFilter.blocked("https://notdoubleclick.net/video"));}
}
