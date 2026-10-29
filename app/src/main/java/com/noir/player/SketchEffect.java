package com.noir.player;

import android.content.Context;
import android.opengl.GLES20;
import androidx.media3.common.VideoFrameProcessingException;
import androidx.media3.common.util.*;
import androidx.media3.effect.*;
import java.io.IOException;

/** A per-frame GPU pencil effect. The switch is read on the GL thread. */
@android.annotation.SuppressLint("UnsafeOptInUsageError")
final class SketchEffect implements GlEffect {
 volatile boolean enabled;
 volatile int style=SketchStyle.FINE_PENCIL;
 SketchEffect(boolean enabled){this.enabled=enabled;}
 @Override public GlShaderProgram toGlShaderProgram(Context context,boolean useHdr)throws VideoFrameProcessingException{return new Program(context,useHdr,this);}
 static final class Program extends BaseGlShaderProgram {
  final GlProgram program;final SketchEffect effect;
  Program(Context context,boolean hdr,SketchEffect effect)throws VideoFrameProcessingException{
   super(hdr,1);this.effect=effect;
   try{
    program=new GlProgram(context,"shaders/pulse_sketch.vert","shaders/pulse_sketch.frag");
    program.setBufferAttribute("aPosition",GlUtil.getNormalizedCoordinateBounds(),GlUtil.HOMOGENEOUS_COORDINATE_VECTOR_SIZE);
    program.setFloatsUniform("uLuma",hdr?new float[]{.2627f,.6780f,.0593f}:new float[]{.2126f,.7152f,.0722f});
   }catch(IOException|GlUtil.GlException e){throw new VideoFrameProcessingException(e);}
  }
  @Override public Size configure(int width,int height){program.setFloatsUniform("uPixel",new float[]{1f/width,1f/height});return new Size(width,height);}
  @Override public void drawFrame(int texture,long timeUs)throws VideoFrameProcessingException{
   try{program.use();program.setSamplerTexIdUniform("uTexSampler",texture,0);program.setFloatUniform("uEnabled",effect.enabled?1:0);program.setFloatUniform("uStyle",effect.style);program.bindAttributesAndUniforms();GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);GlUtil.checkGlError();}
   catch(GlUtil.GlException e){throw new VideoFrameProcessingException(e,timeUs);}
  }
  @Override public void release()throws VideoFrameProcessingException{try{super.release();}finally{try{program.delete();}catch(GlUtil.GlException e){throw new VideoFrameProcessingException(e);}}}
 }
}
