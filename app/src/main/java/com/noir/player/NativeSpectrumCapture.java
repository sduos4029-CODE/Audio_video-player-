package com.noir.player;

import androidx.media3.common.C;
import androidx.media3.exoplayer.audio.TeeAudioProcessor;
import java.nio.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/** Receives decoded playback PCM, not microphone/system audio. FFT stays off the audio thread. */
@android.annotation.SuppressLint("UnsafeOptInUsageError")
final class NativeSpectrumCapture implements TeeAudioProcessor.AudioBufferSink {
 private final ExecutorService worker=Executors.newSingleThreadExecutor();private final AtomicBoolean busy=new AtomicBoolean();private volatile float[] bands=new float[AudioSpectrum.BANDS],trace=new float[AudioWaveform.POINTS];private volatile boolean active;private volatile long publishedNanos;private volatile int generation;
 private int rate,channels,sourceChannels,encoding,bytes,cursor;private long submittedNanos;private double[][] capture=new double[0][],work=new double[0][];
 public synchronized void flush(int sampleRate,int channelCount,int pcmEncoding){generation++;rate=sampleRate;sourceChannels=Math.max(1,channelCount);channels=Math.min(8,sourceChannels);encoding=pcmEncoding;bytes=encoding==C.ENCODING_PCM_8BIT?1:encoding==C.ENCODING_PCM_16BIT||encoding==C.ENCODING_PCM_16BIT_BIG_ENDIAN?2:encoding==C.ENCODING_PCM_24BIT||encoding==C.ENCODING_PCM_24BIT_BIG_ENDIAN?3:encoding==C.ENCODING_PCM_32BIT||encoding==C.ENCODING_PCM_32BIT_BIG_ENDIAN||encoding==C.ENCODING_PCM_FLOAT?4:0;cursor=0;submittedNanos=0;capture=new double[channels][AudioSpectrum.SIZE];work=new double[channels][AudioSpectrum.SIZE];bands=new float[AudioSpectrum.BANDS];trace=new float[AudioWaveform.POINTS];}
 public synchronized void handleBuffer(ByteBuffer source){if(!active||bytes==0||rate<=0)return;ByteBuffer b=source.asReadOnlyBuffer();boolean big=encoding==C.ENCODING_PCM_16BIT_BIG_ENDIAN||encoding==C.ENCODING_PCM_24BIT_BIG_ENDIAN||encoding==C.ENCODING_PCM_32BIT_BIG_ENDIAN;b.order(big?ByteOrder.BIG_ENDIAN:ByteOrder.LITTLE_ENDIAN);
  while(b.remaining()>=sourceChannels*bytes){for(int ch=0;ch<channels;ch++){double sample;if(encoding==C.ENCODING_PCM_FLOAT)sample=b.getFloat();else if(bytes==1)sample=((b.get()&255)-128)/128.0;else if(bytes==2)sample=b.getShort()/32768.0;else if(bytes==4)sample=b.getInt()/2147483648.0;else{int a=b.get()&255,c=b.get()&255,d=b.get()&255;int value=big?(a<<16)|(c<<8)|d:a|(c<<8)|(d<<16);sample=((value<<8)>>8)/8388608.0;}capture[ch][cursor]=Double.isFinite(sample)?Math.max(-1,Math.min(1,sample)):0;}b.position(b.position()+(sourceChannels-channels)*bytes);
   if(++cursor==AudioSpectrum.SIZE){cursor=0;if(System.nanoTime()-submittedNanos>=40_000_000L&&busy.compareAndSet(false,true)){submittedNanos=System.nanoTime();for(int ch=0;ch<channels;ch++)System.arraycopy(capture[ch],0,work[ch],0,AudioSpectrum.SIZE);final double[][] input=work;final int sampleRate=rate,version=generation;try{worker.execute(()->{try{float[] result=AudioSpectrum.calculate(input,sampleRate),wave=AudioWaveform.calculate(input);if(active&&version==generation){float[] previous=bands;for(int i=0;i<result.length;i++)result[i]=Math.max(result[i],previous[i]*.8f);bands=result;trace=wave;publishedNanos=System.nanoTime();}}finally{busy.set(false);}});}catch(RejectedExecutionException e){busy.set(false);}}}
  }
 }
 void setActive(boolean value){active=value;if(!value){generation++;bands=new float[AudioSpectrum.BANDS];trace=new float[AudioWaveform.POINTS];}}
 float[] levels(){return active&&System.nanoTime()-publishedNanos<1_000_000_000L?bands:new float[AudioSpectrum.BANDS];}
 float[] waveform(){return active&&System.nanoTime()-publishedNanos<1_000_000_000L?trace:new float[AudioWaveform.POINTS];}
 void close(){setActive(false);worker.shutdownNow();}
}
