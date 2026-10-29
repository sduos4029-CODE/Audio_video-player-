package com.noir.player;
import android.content.Context;
import android.graphics.*;
import android.view.View;
/** Normal mode uses a single smooth line from decoded audio PCM samples. */
public class SpectrumView extends View {
 private static final float[] SILENCE=new float[AudioSpectrum.BANDS];final Paint paint=new Paint(3);final Path path=new Path();int color=0xffffffff;
 public SpectrumView(Context context){super(context);setContentDescription("Playing audio waveform, smooth line");}
 protected void onDraw(Canvas canvas){super.onDraw(canvas);PlaybackService service=PlaybackService.current;float[] values=service!=null&&service.playing()?service.waveformSamples():SILENCE;float middle=getHeight()/2f;WaveformPainter.smooth(canvas,paint,path,values,0,middle,getWidth()-1f,Math.max(0,middle-3),color,getResources().getDisplayMetrics().density*1.5f);if(getVisibility()==VISIBLE&&getWindowVisibility()==VISIBLE)postInvalidateDelayed(50);}
}
