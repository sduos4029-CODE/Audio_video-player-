package com.noir.player;
import android.graphics.*;
/** Bound decoded cover dimensions before allocating a bitmap from file metadata. */
public final class Artwork {
 private Artwork(){}
 public static Bitmap decode(byte[] data,int target){if(data==null||data.length>8*1024*1024)return null;BitmapFactory.Options o=new BitmapFactory.Options();o.inJustDecodeBounds=true;BitmapFactory.decodeByteArray(data,0,data.length,o);if(o.outWidth<=0||o.outHeight<=0)return null;o.inSampleSize=1;while(o.outWidth/o.inSampleSize>target||o.outHeight/o.inSampleSize>target)o.inSampleSize*=2;o.inJustDecodeBounds=false;return BitmapFactory.decodeByteArray(data,0,data.length,o);}
}
