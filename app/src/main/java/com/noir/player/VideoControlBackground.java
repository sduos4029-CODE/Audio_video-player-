package com.noir.player;

import android.graphics.*;
import android.graphics.drawable.Drawable;

/** Keeps the playback background circular even when its hit target is wider. */
final class VideoControlBackground extends Drawable {
 final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);final int color;final boolean filled;final float stroke;
 VideoControlBackground(int color,boolean filled,float stroke){this.color=color;this.filled=filled;this.stroke=stroke;}
 @Override public void draw(Canvas canvas){Rect b=getBounds();paint.setColor(color);paint.setStyle(filled?Paint.Style.FILL:Paint.Style.STROKE);paint.setStrokeWidth(stroke);canvas.drawCircle(b.exactCenterX(),b.exactCenterY(),Math.max(0,Math.min(b.width(),b.height())/2f-stroke),paint);}
 @Override public void setAlpha(int alpha){paint.setAlpha(alpha);invalidateSelf();}
 @Override public void setColorFilter(ColorFilter filter){paint.setColorFilter(filter);invalidateSelf();}
 @Override public int getOpacity(){return PixelFormat.TRANSLUCENT;}
}
