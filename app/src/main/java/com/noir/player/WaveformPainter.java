package com.noir.player;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

/** Mode-specific rendering: one curved PCM trace, or a mirrored frequency-dot envelope. */
final class WaveformPainter {
 private WaveformPainter(){}
 static void smooth(Canvas canvas,Paint paint,Path path,float[] samples,float left,float middle,float width,float amplitude,int color,float stroke){
  if(samples.length<2)return;
  paint.setColor(color);paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(stroke);paint.setStrokeCap(Paint.Cap.ROUND);paint.setStrokeJoin(Paint.Join.ROUND);
  path.reset();float step=width/(samples.length-1),x=left,y=middle-samples[0]*amplitude;path.moveTo(x,y);
  for(int i=1;i<samples.length;i++){float nextX=left+i*step,nextY=middle-samples[i]*amplitude,previousY=middle-samples[Math.max(0,i-2)]*amplitude,followingY=middle-samples[Math.min(samples.length-1,i+1)]*amplitude;float control1=Math.max(middle-amplitude,Math.min(middle+amplitude,y+(nextY-previousY)/6)),control2=Math.max(middle-amplitude,Math.min(middle+amplitude,nextY-(followingY-y)/6));path.cubicTo(x+step/3,control1,nextX-step/3,control2,nextX,nextY);x=nextX;y=nextY;}
  canvas.drawPath(path,paint);
 }
 static void dots(Canvas canvas,Paint paint,float[] bands,float left,float middle,float width,float amplitude,int color){
  if(bands.length<2)return;
  paint.setColor(color);paint.setStyle(Paint.Style.FILL);float spacing=2.5f;
  int columns=Math.max(1,(int)(width/spacing));
  for(int column=0;column<=columns;column++){float position=column*(bands.length-1f)/columns;int index=Math.min(bands.length-2,(int)position);float fraction=position-index,level=bands[index]*(1-fraction)+bands[index+1]*fraction;int rows=(int)(Math.max(0,Math.min(1,level))*amplitude/spacing);float x=left+column*width/columns;
   for(int row=-rows;row<=rows;row++)canvas.drawCircle(x,middle+row*spacing,.72f,paint);
  }
 }
}
