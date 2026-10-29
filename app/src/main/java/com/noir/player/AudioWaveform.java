package com.noir.player;

/** Short time-domain trace from the loudest decoded PCM channel, without phase cancellation. */
final class AudioWaveform {
 static final int POINTS=64;
 private AudioWaveform(){}
 static float[] calculate(double[][] samples){
  float[] trace=new float[POINTS];double[] selected=null;double loudest=0;
  for(double[] channel:samples){double power=0;for(double value:channel)power+=value*value;if(power>loudest){loudest=power;selected=channel;}}
  if(selected==null)return trace;
  for(int i=0;i<POINTS;i++){int start=i*selected.length/POINTS,end=(i+1)*selected.length/POINTS;double sum=0;for(int j=start;j<end;j++)sum+=selected[j];trace[i]=(float)Math.max(-1,Math.min(1,sum/Math.max(1,end-start)));}
  return trace;
 }
}
