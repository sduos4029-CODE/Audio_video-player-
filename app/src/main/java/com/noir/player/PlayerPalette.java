package com.noir.player;
/** Shared color roles keep combo themes consistent in normal and matrix players. */
public final class PlayerPalette {
 public final int primary,secondary,text,filledIcon;public final boolean combo;
 private PlayerPalette(int p,int s,boolean combo){primary=p;secondary=s;text=0xffffffff;this.combo=combo;filledIcon=combo?text:0xff000000;}
 public static PlayerPalette forIndex(int index){int[] singles={0xffffffff,0xffff343b,0xffffd43b,0xff269bff,0xffbf59ff};if(index==5)return new PlayerPalette(0xffff343b,0xffffd43b,true);if(index==6)return new PlayerPalette(0xff269bff,0xffbf59ff,true);return new PlayerPalette(singles[Math.max(0,Math.min(4,index))],singles[Math.max(0,Math.min(4,index))],false);}
}
