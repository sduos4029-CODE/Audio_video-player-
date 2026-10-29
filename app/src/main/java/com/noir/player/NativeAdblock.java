package com.noir.player;

/** JNI entry points for the pinned MPL-2.0 adblock-rust engine. No network or storage. */
public final class NativeAdblock {
 public static final boolean AVAILABLE;
 static {boolean loaded=false;try{System.loadLibrary("pulse_adblock");loaded=true;}catch(UnsatisfiedLinkError|SecurityException ignored){}AVAILABLE=loaded;}
 private NativeAdblock(){}
 public static native boolean initialize(String rules,String resources);
 public static native String matchRequest(String url,String source,String kind,String method);
 public static native String pageResources(String url);
}
