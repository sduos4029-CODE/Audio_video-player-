package com.noir.player;

import android.content.Context;
import android.view.*;
import android.widget.FrameLayout;

/** Symmetric edge actions with a centered transport group and 48dp targets. */
// Built only in code with the player's real controls; this is never XML-inflated.
@android.annotation.SuppressLint("ViewConstructor")
final class VideoTransportBar extends FrameLayout {
 final View center,lock,fit;
 VideoTransportBar(Context context,View center,View lock,View fit){super(context);this.center=center;this.lock=lock;this.fit=fit;addView(center,new LayoutParams(dp(288),dp(64),Gravity.CENTER));addView(lock,new LayoutParams(dp(48),dp(48),Gravity.START|Gravity.CENTER_VERTICAL));addView(fit,new LayoutParams(dp(48),dp(48),Gravity.END|Gravity.CENTER_VERTICAL));}
 int dp(int value){return Math.round(value*getResources().getDisplayMetrics().density);}
 @Override protected void onMeasure(int width,int height){int available=MeasureSpec.getSize(width);boolean narrow=available<dp(336);LayoutParams cp=(LayoutParams)center.getLayoutParams();cp.width=Math.min(available-(narrow?0:dp(96)),dp(288));cp.gravity=Gravity.CENTER_HORIZONTAL|(narrow?Gravity.BOTTOM:Gravity.CENTER_VERTICAL);((LayoutParams)lock.getLayoutParams()).gravity=Gravity.START|(narrow?Gravity.TOP:Gravity.CENTER_VERTICAL);((LayoutParams)fit.getLayoutParams()).gravity=Gravity.END|(narrow?Gravity.TOP:Gravity.CENTER_VERTICAL);super.onMeasure(width,MeasureSpec.makeMeasureSpec(dp(narrow?112:64),MeasureSpec.EXACTLY));}
}
