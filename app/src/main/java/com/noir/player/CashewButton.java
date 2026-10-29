package com.noir.player;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.*;
import android.graphics.drawable.*;
import android.widget.Button;

/** Mirrored, concave bean shapes with full rectangular accessible touch targets. */
public class CashewButton extends Button {
 final boolean next;
 int accent=Color.WHITE;boolean styled;PlayerIcons enabledIcon,disabledIcon;
 public CashewButton(Context context){this(context,false);}
 public CashewButton(Context context,boolean next){super(context);this.next=next;setText("");setContentDescription(next?"Next track":"Previous track");setMinWidth(0);setMinimumWidth(0);setPadding(0,0,0,0);setMinimumHeight(dp(48));updateColor(Color.WHITE);}
 int dp(int value){return Math.round(value*getResources().getDisplayMetrics().density);}
 void updateColor(int color){if(accent==color&&styled)return;accent=color;styled=true;enabledIcon=new PlayerIcons(next?PlayerIcons.Icon.NEXT:PlayerIcons.Icon.PREVIOUS,color);disabledIcon=new PlayerIcons(next?PlayerIcons.Icon.NEXT:PlayerIcons.Icon.PREVIOUS,0xff666666);setBackground(new RippleDrawable(ColorStateList.valueOf((color&0xffffff)|0x40000000),new Bean(next,color,false),new Bean(next,Color.WHITE,true)));invalidate();}
 @Override protected void onDraw(Canvas canvas){super.onDraw(canvas);int size=dp(23);float center=getWidth()*(next?.64f:.36f);PlayerIcons icon=isEnabled()?enabledIcon:disabledIcon;icon.setBounds((int)(center-size/2f),(getHeight()-size)/2,(int)(center+size/2f),(getHeight()+size)/2);icon.draw(canvas);}
 static final class Bean extends Drawable {
  final boolean mirror,mask;final int color;final Paint paint=new Paint(3);final Path path=new Path();
  Bean(boolean mirror,int color,boolean mask){this.mirror=mirror;this.color=color;this.mask=mask;}
  public void draw(Canvas c){Rect b=getBounds();c.save();c.translate(b.left,b.top);c.scale(b.width()/100f,b.height()/100f);if(mirror)c.scale(-1,1,50,50);path.reset();path.moveTo(34,7);path.cubicTo(11,-1,-1,28,7,58);path.cubicTo(16,94,50,106,78,85);path.cubicTo(104,66,98,30,80,23);path.cubicTo(65,17,57,29,62,41);path.cubicTo(68,57,56,66,46,57);path.cubicTo(32,45,49,18,34,7);path.close();paint.setStyle(Paint.Style.FILL);paint.setColor(mask?Color.WHITE:0xff1d1d1d);c.drawPath(path,paint);if(!mask){paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(2);paint.setColor(color);c.drawPath(path,paint);}c.restore();}
  public void setAlpha(int alpha){paint.setAlpha(alpha);}public void setColorFilter(ColorFilter f){paint.setColorFilter(f);}public int getOpacity(){return PixelFormat.TRANSLUCENT;}
 }
}
