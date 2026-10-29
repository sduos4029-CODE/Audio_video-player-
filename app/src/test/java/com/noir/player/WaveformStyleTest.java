package com.noir.player;

import android.graphics.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.*;
import java.io.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk=28)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class WaveformStyleTest {
 @Test public void pcmTracePreservesPolarityAndAvoidsStereoCancellation(){double[] left=SpectrumAndClockTest.tone(440,48000),right=left.clone();for(int i=0;i<right.length;i++)right[i]=-right[i];float[] mono=AudioWaveform.calculate(new double[][]{left}),stereo=AudioWaveform.calculate(new double[][]{left,right});assertArrayEquals(mono,stereo,0);boolean positive=false,negative=false;for(float value:stereo){positive|=value>.1;negative|=value<-.1;}assertTrue(positive);assertTrue(negative);for(float value:AudioWaveform.calculate(new double[][]{new double[1024]}))assertEquals(0,value,0);}
 @Test public void smoothTraceIsOneLineAndDotsHaveBlackGaps()throws Exception{
  double[][] pcm={SpectrumAndClockTest.tone(440,48000)};float[] trace=AudioWaveform.calculate(pcm),bands=AudioSpectrum.calculate(pcm,48000);Paint paint=new Paint(3);Path path=new Path();
  Bitmap smooth=Bitmap.createBitmap(1000,240,Bitmap.Config.ARGB_8888);Canvas lineCanvas=new Canvas(smooth);lineCanvas.drawColor(Color.BLACK);WaveformPainter.smooth(lineCanvas,paint,path,trace,20,120,960,100,0xffffd43b,3);save(smooth,"waveform-smooth-v1.9.png");assertTrue(lit(smooth,20,10,980,110)>30);assertTrue(lit(smooth,20,130,980,230)>30);
  Bitmap dots=Bitmap.createBitmap(1000,240,Bitmap.Config.ARGB_8888);Canvas dotCanvas=new Canvas(dots);dotCanvas.drawColor(Color.BLACK);dotCanvas.scale(3,3);WaveformPainter.dots(dotCanvas,paint,bands,5,40,320,35,0xffffd43b);save(dots,"waveform-dots-v1.9.png");assertTrue(lit(dots,0,0,1000,110)>30);assertTrue(lit(dots,0,130,1000,240)>30);assertEquals(Color.BLACK,dots.getPixel(19,120));
  Bitmap idle=Bitmap.createBitmap(1000,240,Bitmap.Config.ARGB_8888);Canvas idleCanvas=new Canvas(idle);idleCanvas.drawColor(Color.BLACK);WaveformPainter.smooth(idleCanvas,paint,path,new float[64],20,120,960,100,Color.WHITE,3);assertEquals(0,lit(idle,0,0,1000,115));assertEquals(0,lit(idle,0,125,1000,240));assertTrue(lit(idle,0,115,1000,125)>900);
 }
 static int lit(Bitmap b,int l,int t,int r,int bottom){int count=0;for(int y=t;y<bottom;y++)for(int x=l;x<r;x++)if((b.getPixel(x,y)&0xffffff)!=0)count++;return count;}
 static void save(Bitmap b,String name)throws IOException{File dir=new File("build/player-screenshots");dir.mkdirs();try(FileOutputStream out=new FileOutputStream(new File(dir,name))){b.compress(Bitmap.CompressFormat.PNG,100,out);}}
}
