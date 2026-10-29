package com.noir.player;
import android.os.Build;
import android.view.*;
/** Apply system insets once, then consume them before WebView/children see them again. */
public final class SafeArea {
 private SafeArea(){}
 public static void install(View root){root.setOnApplyWindowInsetsListener((view,insets)->apply(root,insets));root.requestApplyInsets();}
 public static WindowInsets apply(View root,WindowInsets insets){
  if(Build.VERSION.SDK_INT>=30){int types=WindowInsets.Type.systemBars()|WindowInsets.Type.displayCutout();android.graphics.Insets bars=insets.getInsets(types),ime=insets.getInsets(WindowInsets.Type.ime());root.setPadding(bars.left,bars.top,bars.right,Math.max(bars.bottom,ime.bottom));return WindowInsets.CONSUMED;}
  root.setPadding(insets.getSystemWindowInsetLeft(),insets.getSystemWindowInsetTop(),insets.getSystemWindowInsetRight(),insets.getSystemWindowInsetBottom());return insets.consumeSystemWindowInsets();
 }
}
