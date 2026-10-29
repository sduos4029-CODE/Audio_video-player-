package com.noir.player;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

/** One bounded capture request. Cancelled popups never receive late frames. */
final class SketchPreviewRequest {
 interface Callback {void ready(Bitmap[] frames);void failed();}
 final Callback callback;final AtomicBoolean cancelled=new AtomicBoolean(),completed=new AtomicBoolean();
 volatile boolean hdr;
 SketchPreviewRequest(Callback callback){this.callback=callback;}
 void cancel(){cancelled.set(true);}
 void finish(Bitmap[] frames){if(!completed.compareAndSet(false,true)){discard(frames);return;}new Handler(Looper.getMainLooper()).post(()->{if(cancelled.get())discard(frames);else if(frames==null)callback.failed();else callback.ready(frames);});}
 static void discard(Bitmap[] frames){if(frames!=null)for(Bitmap frame:frames)if(frame!=null&&!frame.isRecycled())frame.recycle();}
 static Bitmap fromRgba(ByteBuffer rgba,int width,int height){rgba.rewind();int[] colors=new int[width*height];for(int y=0;y<height;y++)for(int x=0;x<width;x++){int r=rgba.get()&255,g=rgba.get()&255,b=rgba.get()&255,a=rgba.get()&255;colors[(height-y-1)*width+x]=(a<<24)|(r<<16)|(g<<8)|b;}return Bitmap.createBitmap(colors,width,height,Bitmap.Config.ARGB_8888);}
}
