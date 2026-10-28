package com.noir.player;
import android.graphics.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import java.io.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk=28)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
public class PlayerScreenTest {
 @Test public void drawsReadableDotControlsAndNormalScreen() throws Exception {
  try(org.robolectric.android.controller.ActivityController<MainActivity> controller=Robolectric.buildActivity(MainActivity.class).setup()){
   MainActivity activity=controller.get();activity.prefs.edit().putBoolean("dots",true).putBoolean("lock",true).commit();
   PlayerView view=activity.view;view.layout(0,0,1080,2220);
   Bitmap dot=Bitmap.createBitmap(1080,2220,Bitmap.Config.ARGB_8888);view.draw(new Canvas(dot));
   // Both complete circular button outlines survive the matrix conversion.
   assertTrue("Back button outline must survive",lit(dot,195,1620,340,1775)>300);
   assertTrue("Forward button outline must survive",lit(dot,745,1620,885,1775)>300);
   assertTrue("Elapsed time must be visible",lit(dot,70,1485,230,1560)>100);
   assertTrue("Total time must be visible",lit(dot,830,1485,1010,1560)>100);
   File dir=new File("build/player-screenshots");dir.mkdirs();
   try(FileOutputStream out=new FileOutputStream(new File(dir,"dot-matrix.png"))){dot.compress(Bitmap.CompressFormat.PNG,100,out);}
   activity.prefs.edit().putBoolean("dots",false).commit();Bitmap normal=Bitmap.createBitmap(1080,2220,Bitmap.Config.ARGB_8888);view.draw(new Canvas(normal));
   try(FileOutputStream out=new FileOutputStream(new File(dir,"normal.png"))){normal.compress(Bitmap.CompressFormat.PNG,100,out);}
  }
 }
 int lit(Bitmap bitmap,int left,int top,int right,int bottom){int count=0;for(int y=top;y<bottom;y++)for(int x=left;x<right;x++){int pixel=bitmap.getPixel(x,y);if(Color.red(pixel)+Color.green(pixel)+Color.blue(pixel)>200)count++;}return count;}
}
