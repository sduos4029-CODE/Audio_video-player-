package com.noir.player;
/** Keeps thin strokes by considering every pixel in each matrix cell. */
public final class DotSampler {
 private DotSampler() {}
 public static int color(int[] pixels,int width,int height,int x,int y,int step){
  int result=0xff000000,brightest=0;
  for(int yy=y;yy<Math.min(y+step,height);yy++)for(int xx=x;xx<Math.min(x+step,width);xx++){
   int value=pixels[yy*width+xx];int brightness=((value>>>16)&255)*54+((value>>>8)&255)*183+(value&255)*19;
   if(brightness>brightest){brightest=brightness;result=value;}
  }
  return result;
 }
}
