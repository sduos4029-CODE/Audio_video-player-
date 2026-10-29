package com.noir.player;

/** Hann-windowed radix-2 FFT; channel powers are combined without phase cancellation. */
public final class AudioSpectrum {
 public static final int SIZE=1024,BANDS=64;
 private static final double[] WINDOW=new double[SIZE];static{for(int i=0;i<SIZE;i++)WINDOW[i]=.5-.5*Math.cos(2*Math.PI*i/(SIZE-1));}
 private AudioSpectrum(){}
 public static float[] calculate(double[][] samples,int rate){float[] output=new float[BANDS];if(rate<=0||samples.length==0)return output;double[] power=new double[SIZE/2];int channels=0;
  for(double[] channel:samples){if(channel.length<SIZE)continue;channels++;double[] real=new double[SIZE],imag=new double[SIZE];double mean=0;for(int i=0;i<SIZE;i++)mean+=channel[i]/SIZE;for(int i=0;i<SIZE;i++)real[i]=(channel[i]-mean)*WINDOW[i];
   for(int i=1,j=0;i<SIZE;i++){int bit=SIZE>>1;for(;(j&bit)!=0;bit>>=1)j^=bit;j^=bit;if(i<j){double temp=real[i];real[i]=real[j];real[j]=temp;}}
   for(int length=2;length<=SIZE;length<<=1){double angle=-2*Math.PI/length,cos=Math.cos(angle),sin=Math.sin(angle);for(int start=0;start<SIZE;start+=length){double wr=1,wi=0;for(int j=0;j<length/2;j++){int even=start+j,odd=even+length/2;double tr=wr*real[odd]-wi*imag[odd],ti=wr*imag[odd]+wi*real[odd];real[odd]=real[even]-tr;imag[odd]=imag[even]-ti;real[even]+=tr;imag[even]+=ti;double next=wr*cos-wi*sin;wi=wr*sin+wi*cos;wr=next;}}}
   for(int i=1;i<SIZE/2;i++)power[i]+=real[i]*real[i]+imag[i]*imag[i];
  }
  if(channels==0)return output;double maxFrequency=Math.min(18000,rate/2.0);
  for(int band=0;band<BANDS;band++){double low=40*Math.pow(maxFrequency/40,band/(double)BANDS),high=40*Math.pow(maxFrequency/40,(band+1)/(double)BANDS);int from=Math.max(1,(int)Math.floor(low*SIZE/rate)),to=Math.min(SIZE/2,(int)Math.ceil(high*SIZE/rate));double peak=0;for(int i=from;i<Math.max(from+1,to)&&i<power.length;i++)peak=Math.max(peak,power[i]/channels);double amplitude=4*Math.sqrt(peak)/SIZE;double db=20*Math.log10(Math.max(1e-12,amplitude));output[band]=(float)Math.max(0,Math.min(1,(db+72)/72));}
  return output;
 }
 public static double centerFrequency(int band,int rate){return 40*Math.pow(Math.min(18000,rate/2.0)/40,(band+.5)/BANDS);}
}
