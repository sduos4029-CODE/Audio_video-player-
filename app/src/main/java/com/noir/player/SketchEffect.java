package com.noir.player;

import android.content.Context;
import android.opengl.GLES20;
import androidx.media3.common.VideoFrameProcessingException;
import androidx.media3.common.util.*;
import androidx.media3.effect.*;
import java.io.IOException;
import android.graphics.Bitmap;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.atomic.AtomicReference;

/** A per-frame GPU pencil effect. The switch is read on the GL thread. */
@android.annotation.SuppressLint("UnsafeOptInUsageError")
final class SketchEffect implements GlEffect {
 volatile boolean enabled;
 volatile int style=SketchStyle.FINE_PENCIL;
 final AtomicReference<SketchPreviewRequest> previewRequest=new AtomicReference<>();
 SketchEffect(boolean enabled){this.enabled=enabled;}
 SketchPreviewRequest requestPreviews(SketchPreviewRequest.Callback callback){SketchPreviewRequest next=new SketchPreviewRequest(callback),old=previewRequest.getAndSet(next);if(old!=null)old.cancel();return next;}
 void cancelPreview(SketchPreviewRequest request){if(request!=null){request.cancel();previewRequest.compareAndSet(request,null);}}
 @Override public GlShaderProgram toGlShaderProgram(Context context,boolean useHdr)throws VideoFrameProcessingException{return new Program(context,useHdr,this);}
 static final class Program extends BaseGlShaderProgram {
  final GlProgram program;final SketchEffect effect;final boolean hdr;int inputWidth,inputHeight;
  Program(Context context,boolean hdr,SketchEffect effect)throws VideoFrameProcessingException{
   super(hdr,1);this.effect=effect;this.hdr=hdr;
   try{
    program=new GlProgram(context,"shaders/pulse_sketch.vert","shaders/pulse_sketch.frag");
    program.setBufferAttribute("aPosition",GlUtil.getNormalizedCoordinateBounds(),GlUtil.HOMOGENEOUS_COORDINATE_VECTOR_SIZE);
    program.setFloatsUniform("uLuma",hdr?new float[]{.2627f,.6780f,.0593f}:new float[]{.2126f,.7152f,.0722f});
   }catch(IOException|GlUtil.GlException e){throw new VideoFrameProcessingException(e);}
  }
  @Override public Size configure(int width,int height){inputWidth=width;inputHeight=height;program.setFloatsUniform("uPixel",new float[]{1f/width,1f/height});return new Size(width,height);}
  @Override public void drawFrame(int texture,long timeUs)throws VideoFrameProcessingException{
   SketchPreviewRequest request=effect.previewRequest.getAndSet(null);if(request!=null&&!request.cancelled.get())capturePreviews(texture,request);
   try{program.use();program.setSamplerTexIdUniform("uTexSampler",texture,0);program.setFloatUniform("uEnabled",effect.enabled?1:0);program.setFloatUniform("uStyle",effect.style);program.setFloatUniform("uPreview",0);program.bindAttributesAndUniforms();GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);GlUtil.checkGlError();}
   catch(GlUtil.GlException e){throw new VideoFrameProcessingException(e,timeUs);}
  }
  /** Capture once from the unfiltered input, never re-filter a styled screenshot. */
  void capturePreviews(int input,SketchPreviewRequest request){
   int[] framebuffer=new int[1],target=new int[1],oldFramebuffer=new int[1],oldViewport=new int[4],oldActive=new int[1],oldTexture=new int[1],oldProgram=new int[1];Bitmap[] frames=new Bitmap[7];
   GLES20.glGetIntegerv(GLES20.GL_FRAMEBUFFER_BINDING,oldFramebuffer,0);GLES20.glGetIntegerv(GLES20.GL_VIEWPORT,oldViewport,0);GLES20.glGetIntegerv(GLES20.GL_ACTIVE_TEXTURE,oldActive,0);GLES20.glGetIntegerv(GLES20.GL_CURRENT_PROGRAM,oldProgram,0);GLES20.glActiveTexture(GLES20.GL_TEXTURE0);GLES20.glGetIntegerv(GLES20.GL_TEXTURE_BINDING_2D,oldTexture,0);
   try{
    float scale=Math.min(1,Math.min(256f/inputWidth,144f/inputHeight));int width=Math.max(1,Math.round(inputWidth*scale)),height=Math.max(1,Math.round(inputHeight*scale));
    GLES20.glGenTextures(1,target,0);GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,target[0]);GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_LINEAR);GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D,0,GLES20.GL_RGBA,width,height,0,GLES20.GL_RGBA,GLES20.GL_UNSIGNED_BYTE,null);
    GLES20.glGenFramebuffers(1,framebuffer,0);GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,framebuffer[0]);GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER,GLES20.GL_COLOR_ATTACHMENT0,GLES20.GL_TEXTURE_2D,target[0],0);if(GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER)!=GLES20.GL_FRAMEBUFFER_COMPLETE)throw new IllegalStateException("Preview target unavailable");GLES20.glViewport(0,0,width,height);
    ByteBuffer rgba=ByteBuffer.allocateDirect(width*height*4).order(ByteOrder.nativeOrder());
    for(int style=0;style<frames.length;style++){
     if(request.cancelled.get()){SketchPreviewRequest.discard(frames);return;}
     program.use();program.setSamplerTexIdUniform("uTexSampler",input,0);program.setFloatUniform("uEnabled",style==0?0:1);program.setFloatUniform("uStyle",Math.max(1,style));program.setFloatUniform("uPreview",1);program.bindAttributesAndUniforms();GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);rgba.rewind();GLES20.glReadPixels(0,0,width,height,GLES20.GL_RGBA,GLES20.GL_UNSIGNED_BYTE,rgba);GlUtil.checkGlError();frames[style]=SketchPreviewRequest.fromRgba(rgba,width,height);
    }
    request.hdr=hdr;request.finish(frames);
   }catch(GlUtil.GlException|RuntimeException|OutOfMemoryError error){SketchPreviewRequest.discard(frames);request.finish(null);}
   finally{GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,oldFramebuffer[0]);GLES20.glViewport(oldViewport[0],oldViewport[1],oldViewport[2],oldViewport[3]);if(framebuffer[0]!=0)GLES20.glDeleteFramebuffers(1,framebuffer,0);if(target[0]!=0)GLES20.glDeleteTextures(1,target,0);GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,oldTexture[0]);GLES20.glActiveTexture(oldActive[0]);GLES20.glUseProgram(oldProgram[0]);}
  }
  @Override public void release()throws VideoFrameProcessingException{SketchPreviewRequest request=effect.previewRequest.getAndSet(null);if(request!=null)request.finish(null);try{super.release();}finally{try{program.delete();}catch(GlUtil.GlException e){throw new VideoFrameProcessingException(e);}}}
 }
}
