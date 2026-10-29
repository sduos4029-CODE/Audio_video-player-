package com.noir.player;

/** Stable preference/shader IDs; 0 in the picker means Normal video. */
final class SketchStyle {
 static final int FINE_PENCIL=1,CHARCOAL=2,INK_HATCH=3,COLOR_PENCIL=4,WHITE_OUTLINE=5,COMIC=6;
 static int valid(int style){return style>=FINE_PENCIL&&style<=COMIC?style:FINE_PENCIL;}
 private SketchStyle(){}
}
