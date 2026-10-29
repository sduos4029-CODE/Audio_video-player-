package com.noir.player;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import java.io.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk=28)
public class ComboThemeTest {
 @Test @GraphicsMode(GraphicsMode.Mode.NATIVE) @Config(qualifiers="w360dp-h800dp-xxhdpi") public void combosUseSeparatePrimarySecondaryAndWhiteRoles()throws Exception{try(var controller=Robolectric.buildActivity(MainActivity.class).setup()){MainActivity a=controller.get();for(int theme:new int[]{5,6}){a.prefs.edit().putInt("theme",theme).putBoolean("filled",true).apply();a.normalPlayer.render();PlayerPalette p=PlayerPalette.forIndex(theme);assertEquals(p.secondary,a.normalPlayer.seek.getProgressTintList().getDefaultColor());assertEquals(p.secondary,a.normalPlayer.volume.getProgressTintList().getDefaultColor());assertEquals(Color.WHITE,a.normalPlayer.seek.getThumbTintList().getDefaultColor());assertEquals(Color.WHITE,a.normalPlayer.play.getCurrentTextColor());assertEquals(p.primary,((GradientDrawable)a.normalPlayer.play.getBackground()).getColor().getDefaultColor());assertEquals(p.secondary,a.normalPlayer.spectrum.color);
  a.normalPlayer.measure(View.MeasureSpec.makeMeasureSpec(1080,View.MeasureSpec.EXACTLY),View.MeasureSpec.makeMeasureSpec(2200,View.MeasureSpec.EXACTLY));a.normalPlayer.layout(0,0,1080,2200);Bitmap bitmap=Bitmap.createBitmap(1080,2200,Bitmap.Config.ARGB_8888);a.normalPlayer.draw(new Canvas(bitmap));File dir=new File("build/player-screenshots");dir.mkdirs();try(FileOutputStream out=new FileOutputStream(new File(dir,"combo-"+theme+"-v1.7.png"))){bitmap.compress(Bitmap.CompressFormat.PNG,100,out);}
  a.prefs.edit().putBoolean("filled",false).apply();a.normalPlayer.render();assertEquals(p.primary,a.normalPlayer.play.getCurrentTextColor());assertEquals(Color.BLACK,((GradientDrawable)a.normalPlayer.play.getBackground()).getColor().getDefaultColor());}}}
}
