package com.noir.player;
import android.content.Context;
import android.webkit.WebView;
/** Retains the web media surface for user-enabled background playback. */
public class BackgroundWebView extends WebView {
 boolean backgroundEnabled=true;
 public BackgroundWebView(Context context){super(context);}
 protected void onWindowVisibilityChanged(int visibility){super.onWindowVisibilityChanged(backgroundEnabled?VISIBLE:visibility);}
}
