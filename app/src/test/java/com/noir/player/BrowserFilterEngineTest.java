package com.noir.player;
import android.net.Uri;
import android.webkit.*;
import java.util.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(sdk=28)
public class BrowserFilterEngineTest {
 static WebResourceRequest request(String url,Map<String,String> headers){return new WebResourceRequest(){public Uri getUrl(){return Uri.parse(url);}public boolean isForMainFrame(){return false;}public boolean isRedirect(){return false;}public boolean hasGesture(){return false;}public String getMethod(){return "GET";}public Map<String,String> getRequestHeaders(){return headers;}};}
 @Test public void requestContextUsesAvailableDestinationAndConservativeUrlHints(){assertEquals("media",BrowserFilterEngine.resourceType(request("https://rr.googlevideo.com/videoplayback",Collections.emptyMap())));assertEquals("script",BrowserFilterEngine.resourceType(request("https://youtube.com/asset",Map.of("Sec-Fetch-Dest","script"))));assertEquals("xmlhttprequest",BrowserFilterEngine.resourceType(request("https://youtube.com/youtubei/v1/player",Collections.emptyMap())));assertEquals("other",BrowserFilterEngine.resourceType(request("https://example.test/unknown",Collections.emptyMap())));}
 @Test public void replacementResourcesAreLocalAndBounded()throws Exception{WebResourceResponse response=BrowserFilterEngine.dataResponse("data:application/javascript;base64,dm9pZCAwOw==");assertNotNull(response);assertEquals("application/javascript",response.getMimeType());assertEquals("void 0;",new String(response.getData().readAllBytes(),java.nio.charset.StandardCharsets.UTF_8));assertNull(BrowserFilterEngine.dataResponse("https://remote.test/code.js"));assertNull(BrowserFilterEngine.dataResponse("data:text/plain,unencoded"));assertNull(BrowserFilterEngine.dataResponse("data:text/plain;base64,"+"A".repeat(1_400_001)));}
}
