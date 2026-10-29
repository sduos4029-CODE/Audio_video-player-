package com.noir.player;
import org.junit.Test;
import static org.junit.Assert.*;
import java.nio.*;
import java.util.*;
import androidx.media3.common.C;

public class SpectrumAndClockTest {
 static double[] tone(double frequency,int rate){double[] data=new double[AudioSpectrum.SIZE];for(int i=0;i<data.length;i++)data[i]=.7*Math.sin(2*Math.PI*frequency*i/rate);return data;}
 static int peak(float[] values){int best=0;for(int i=1;i<values.length;i++)if(values[i]>values[best])best=i;return best;}
 @Test public void frequencyPeakTracks440And4000Hz(){for(double frequency:new double[]{440,4000}){float[] values=AudioSpectrum.calculate(new double[][]{tone(frequency,48000)},48000);assertTrue(values[peak(values)]>.8);assertEquals(frequency,AudioSpectrum.centerFrequency(peak(values),48000),frequency*.22);}}
 @Test public void silenceAndDcHaveNoInventedWave(){double[] dc=new double[AudioSpectrum.SIZE];Arrays.fill(dc,.75);for(double[] channel:new double[][]{new double[AudioSpectrum.SIZE],dc})for(float value:AudioSpectrum.calculate(new double[][]{channel},44100))assertEquals(0,value,0);}
 @Test public void antiPhaseStereoDoesNotCancelSpectrum(){double[] left=tone(1200,48000),right=left.clone();for(int i=0;i<right.length;i++)right[i]=-right[i];float[] mono=AudioSpectrum.calculate(new double[][]{left},48000),stereo=AudioSpectrum.calculate(new double[][]{left,right},48000);assertEquals(peak(mono),peak(stereo));assertEquals(mono[peak(mono)],stereo[peak(stereo)],.0001);}
 @Test public void teeReadsRealPcmWithoutConsumingPlaybackBuffer()throws Exception{NativeSpectrumCapture capture=new NativeSpectrumCapture();try{capture.flush(48000,2,C.ENCODING_PCM_16BIT);capture.setActive(true);ByteBuffer pcm=ByteBuffer.allocate(AudioSpectrum.SIZE*4).order(ByteOrder.LITTLE_ENDIAN);for(double value:tone(1000,48000)){short sample=(short)(value*32767);pcm.putShort(sample).putShort((short)-sample);}pcm.flip();capture.handleBuffer(pcm);assertEquals(0,pcm.position());long deadline=System.nanoTime()+3_000_000_000L;float[] levels=capture.levels();while(levels[peak(levels)]==0&&System.nanoTime()<deadline){Thread.sleep(5);levels=capture.levels();}assertEquals(1000,AudioSpectrum.centerFrequency(peak(levels),48000),220);assertTrue(levels[peak(levels)]>.8);boolean measured=false;for(float sample:capture.waveform())measured|=Math.abs(sample)>.1;assertTrue(measured);capture.setActive(false);for(float level:capture.levels())assertEquals(0,level,0);for(float sample:capture.waveform())assertEquals(0,sample,0);}finally{capture.close();}}
 @Test public void clockContainsOnlyCleanAsciiAndHonors24HourSetting(){TimeZone utc=TimeZone.getTimeZone("UTC");Calendar calendar=Calendar.getInstance(utc);calendar.set(2026,Calendar.OCTOBER,29,9,5,0);String morning=ClockText.format(calendar.getTime(),false,utc);assertEquals("9:05 AM",morning);calendar.set(Calendar.HOUR_OF_DAY,21);assertEquals("9:05 PM",ClockText.format(calendar.getTime(),false,utc));assertEquals("21:05",ClockText.format(calendar.getTime(),true,utc));assertTrue(morning.matches("[0-9: APM]+"));}
}
