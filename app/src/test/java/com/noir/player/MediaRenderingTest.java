package com.noir.player;
import org.junit.Test;
import static org.junit.Assert.*;
public class MediaRenderingTest {
 @Test public void preservesThinStrokeBetweenSamplingPoints(){int[] pixels=new int[16];pixels[6]=0xffffffff;assertEquals(0xffffffff,DotSampler.color(pixels,4,4,0,0,4));}
 @Test public void clipsLastCellToBitmapBounds(){int[] pixels=new int[9];pixels[8]=0xffff343b;assertEquals(0xffff343b,DotSampler.color(pixels,3,3,2,2,4));}
 @Test public void keepsBlackBackgroundBlack(){assertEquals(0xff000000,DotSampler.color(new int[16],4,4,0,0,4));}
 @Test public void parsesYouTubeVariants(){String id="dQw4w9WgXcQ";assertEquals(id,MediaLinks.youtubeId("https://youtu.be/"+id+"?si=test"));assertEquals(id,MediaLinks.youtubeId("https://www.youtube.com/watch?feature=share&v="+id));assertEquals(id,MediaLinks.youtubeId("https://youtube.com/shorts/"+id));assertEquals(id,MediaLinks.youtubeId("https://m.youtube.com/live/"+id));}
 @Test public void rejectsSpoofedHostAndInvalidIds(){assertNull(MediaLinks.youtubeId("https://youtube.com.evil.test/watch?v=dQw4w9WgXcQ"));assertNull(MediaLinks.youtubeId("https://youtube.com/watch?v=<script>"));assertNull(MediaLinks.youtubeId("http://youtu.be/dQw4w9WgXcQ"));assertNull(MediaLinks.youtubeId("https://example.com/song.mp4"));}
}
