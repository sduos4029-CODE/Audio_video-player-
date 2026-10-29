package com.noir.player;

import android.graphics.Matrix;
import android.graphics.RectF;

/** Video-only geometry. Controls and subtitles never scale with the picture. */
final class VideoGeometry {
 static final int FIT=0,CROP=1,STRETCH=2;
 static final float MIN_ZOOM=.5f,MAX_ZOOM=4f;
 static float clampZoom(float value){return Float.isFinite(value)?Math.max(MIN_ZOOM,Math.min(MAX_ZOOM,value)):1f;}
 static float sourceAspect(int width,int height,float pixelRatio,int rotation){if(width<=0||height<=0)return 16/9f;float ratio=Float.isFinite(pixelRatio)&&pixelRatio>0?pixelRatio:1;float aspect=width*ratio/height;int turn=((rotation%360)+360)%360;return turn==90||turn==270?1/aspect:aspect;}
 /** The GPU graph already letterboxes its picture. Transform that picture, not its black bars. */
 static RectF pictureInOutput(int width,int height,float aspect,boolean gpuFitted){return gpuFitted?bounds(width,height,aspect,FIT,1,0,0):new RectF(0,0,width,height);}
 static Matrix textureTransform(int width,int height,float aspect,int mode,float zoom,float panX,float panY,boolean gpuFitted){Matrix matrix=new Matrix();if(width>0&&height>0)matrix.setRectToRect(pictureInOutput(width,height,aspect,gpuFitted),bounds(width,height,aspect,mode,zoom,panX,panY),Matrix.ScaleToFit.FILL);return matrix;}
 static RectF bounds(int width,int height,float aspect,int mode,float zoom,float panX,float panY){
  if(width<=0||height<=0)return new RectF();
  if(!Float.isFinite(aspect)||aspect<=0)aspect=16/9f;
  float w=width,h=height;
  if(mode!=STRETCH){
   boolean wider=width/(float)height>aspect;
   if((mode==CROP)==wider)h=w/aspect;else w=h*aspect;
  }
  w*=clampZoom(zoom);h*=clampZoom(zoom);
  float maxX=Math.max(0,(w-width)/2),maxY=Math.max(0,(h-height)/2);
  float x=Float.isFinite(panX)?Math.max(-maxX,Math.min(maxX,panX*width)):0;
  float y=Float.isFinite(panY)?Math.max(-maxY,Math.min(maxY,panY*height)):0;
  float left=(width-w)/2+x,top=(height-h)/2+y;
  return new RectF(left,top,left+w,top+h);
 }
}
