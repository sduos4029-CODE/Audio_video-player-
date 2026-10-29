package com.noir.player;

import android.graphics.*;
import android.graphics.drawable.Drawable;

/** Small vector icons avoid font-dependent playback glyphs. */
public final class PlayerIcons extends Drawable {
 public enum Icon { PLAY, PAUSE, PREVIOUS, NEXT, BACK_10, FORWARD_10, AUDIO, VIDEO, ROTATE, EXPAND, RESIZE, RESIZE_FIT, RESIZE_CROP, RESIZE_STRETCH, BACK, MORE, CLOSE, LOCK, SKETCH }
 private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
 private final Path path=new Path();
 final Icon icon;final int color;
 public PlayerIcons(Icon icon,int color){this.icon=icon;this.color=color;}
 @Override public void draw(Canvas target){Rect bounds=getBounds();target.save();target.translate(bounds.left,bounds.top);target.scale(bounds.width()/24f,bounds.height()/24f);paint.setColor(color);paint.setStrokeWidth(1.8f);paint.setStrokeCap(Paint.Cap.ROUND);paint.setStrokeJoin(Paint.Join.ROUND);paint.setStyle(Paint.Style.STROKE);
  switch(icon){
   case PLAY: triangle(target,8,5,19,12,8,19);break;
   case PAUSE:paint.setStyle(Paint.Style.FILL);target.drawRoundRect(6,5,10,19,1,1,paint);target.drawRoundRect(14,5,18,19,1,1,paint);break;
   case PREVIOUS:case NEXT:{boolean next=icon==Icon.NEXT;if(!next){target.scale(-1,1,12,12);}triangle(target,5,5,16,12,5,19);target.drawLine(19,5,19,19,paint);break;}
   case BACK_10:case FORWARD_10:{boolean forward=icon==Icon.FORWARD_10;if(forward)target.scale(-1,1,12,12);target.drawArc(3,3,21,21,-140,285,false,paint);path.reset();path.moveTo(3,4);path.lineTo(3,10);path.lineTo(9,9);target.drawPath(path,paint);if(forward)target.scale(-1,1,12,12);paint.setStyle(Paint.Style.FILL);paint.setTypeface(Typeface.create("sans-serif-medium",Typeface.NORMAL));paint.setTextSize(9);paint.setTextAlign(Paint.Align.CENTER);target.drawText("10",12,15.5f,paint);break;}
   case AUDIO:target.drawArc(4,4,20,20,180,180,false,paint);target.drawLine(4,12,4,18,paint);target.drawLine(20,12,20,18,paint);target.drawRoundRect(3,13,7,20,1.5f,1.5f,paint);target.drawRoundRect(17,13,21,20,1.5f,1.5f,paint);break;
   case VIDEO:target.drawRoundRect(3,5,21,19,3,3,paint);triangle(target,10,9,15,12,10,15);break;
   case ROTATE:target.drawArc(3,3,21,21,205,235,false,paint);path.reset();path.moveTo(19,2);path.lineTo(21,8);path.lineTo(15,7);target.drawPath(path,paint);target.save();target.rotate(-28,12,12);target.drawRoundRect(8,6,16,18,1,1,paint);target.restore();break;
   case EXPAND:target.drawLine(4,9,4,4,paint);target.drawLine(4,4,9,4,paint);target.drawLine(15,4,20,4,paint);target.drawLine(20,4,20,9,paint);target.drawLine(4,15,4,20,paint);target.drawLine(4,20,9,20,paint);target.drawLine(15,20,20,20,paint);target.drawLine(20,20,20,15,paint);break;
   case RESIZE:target.drawRoundRect(6,7,18,17,1,1,paint);target.drawLine(3,10,3,4,paint);target.drawLine(3,4,9,4,paint);target.drawLine(15,20,21,20,paint);target.drawLine(21,20,21,14,paint);break;
   case RESIZE_FIT:case RESIZE_CROP:case RESIZE_STRETCH:{target.drawRoundRect(3,5,21,19,1,1,paint);if(icon==Icon.RESIZE_FIT){target.drawLine(7,6,7,18,paint);target.drawLine(17,6,17,18,paint);}else if(icon==Icon.RESIZE_CROP){target.drawLine(8,3,8,21,paint);target.drawLine(16,3,16,21,paint);}else{target.drawLine(6,12,18,12,paint);target.drawLine(6,12,9,9,paint);target.drawLine(6,12,9,15,paint);target.drawLine(18,12,15,9,paint);target.drawLine(18,12,15,15,paint);}break;}
   case BACK:target.drawLine(19,12,5,12,paint);target.drawLine(5,12,11,6,paint);target.drawLine(5,12,11,18,paint);break;
   case MORE:paint.setStyle(Paint.Style.FILL);for(int y=5;y<=19;y+=7)target.drawCircle(12,y,1.6f,paint);break;
   case LOCK:target.drawRoundRect(5,10,19,21,2,2,paint);target.drawArc(8,3,16,15,180,180,false,paint);paint.setStyle(Paint.Style.FILL);target.drawCircle(12,15,1.5f,paint);target.drawLine(12,15,12,18,paint);break;
   case SKETCH:target.save();target.rotate(40,12,12);target.drawRect(9,4,15,17,paint);path.reset();path.moveTo(9,17);path.lineTo(12,21);path.lineTo(15,17);target.drawPath(path,paint);target.drawLine(9,7,15,7,paint);target.restore();break;
   case CLOSE:target.drawLine(6,6,18,18,paint);target.drawLine(6,18,18,6,paint);break;
  }target.restore();
 }
 private void triangle(Canvas c,float ax,float ay,float bx,float by,float cx,float cy){path.reset();path.moveTo(ax,ay);path.lineTo(bx,by);path.lineTo(cx,cy);path.close();Paint.Style previous=paint.getStyle();paint.setStyle(Paint.Style.FILL);c.drawPath(path,paint);paint.setStyle(previous);}
 public void setAlpha(int alpha){paint.setAlpha(alpha);}
 public void setColorFilter(ColorFilter filter){paint.setColorFilter(filter);}
 public int getIntrinsicWidth(){return 24;}
 public int getIntrinsicHeight(){return 24;}
 public int getOpacity(){return PixelFormat.TRANSLUCENT;}
}
