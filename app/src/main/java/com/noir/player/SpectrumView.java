package com.noir.player;
import android.content.Context;
import android.graphics.*;
import android.view.View;
/** Native player's line envelope comes directly from decoded audio frequency bands. */
public class SpectrumView extends View {
 private static final float[] SILENCE=new float[AudioSpectrum.BANDS];final Paint paint=new Paint(3);final Path path=new Path();int color=0xffffffff;
 public SpectrumView(Context context){super(context);setContentDescription("Playing audio frequency spectrum");}
 protected void onDraw(Canvas canvas){super.onDraw(canvas);PlaybackService service=PlaybackService.current;float[] values=service!=null&&service.playing()?service.frequencyBands():SILENCE;float middle=getHeight()/2f;paint.setColor(color);paint.setStrokeWidth(getResources().getDisplayMetrics().density*1.5f);paint.setStyle(Paint.Style.STROKE);for(int direction=-1;direction<=1;direction+=2){path.reset();for(int i=0;i<values.length;i++){float x=i*(getWidth()-1f)/(values.length-1),y=middle+direction*values[i]*(middle-3);if(i==0)path.moveTo(x,y);else path.lineTo(x,y);}canvas.drawPath(path,paint);}if(getVisibility()==VISIBLE&&getWindowVisibility()==VISIBLE)postInvalidateDelayed(50);}
}
