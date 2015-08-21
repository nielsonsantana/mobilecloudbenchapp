package com.image;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import com.timer.Timer;

public class ImageTransformLocal {
       private long time;
       private String errorMessage;

       // void
       public String localImageSizeFlip(InputStream is, File tempSaveImage1) {
    	   
    	   new Timer();
    	   Timer.reset();
           Timer.start();    

           Bitmap resizedBitmap = null;
           try { 
        	   Bitmap bMap = BitmapFactory.decodeStream(is);
        	   
        	   float scaleWidth = ((float) 100) / bMap.getWidth();
               float scaleHeight = ((float) 75) / bMap.getHeight();
	           
	           Matrix matrix = new Matrix();

	           matrix.postScale(scaleWidth, scaleHeight);
	           matrix.postRotate(180);
	           	           
	           resizedBitmap = Bitmap.createBitmap(bMap, 0, 0, bMap.getWidth(), bMap.getHeight(), matrix, true);

	           FileOutputStream out = new FileOutputStream(tempSaveImage1);
	           resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
	           
               } catch (Exception e) {
                   this.time = -1;
                   this.errorMessage = "LocalImageSizeFlip.localImageSizeFlip(): " + e;
               }
               
           Timer.stop();
           this.time = Timer.result();
           return resizedBitmap.getHeight() + "" + resizedBitmap.getWidth() + "";

       }

       public String errorMessage() {
               return this.errorMessage;
       }

       public long returnTime() {
               return this.time;
       }
}