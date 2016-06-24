package com.image2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Formatter.BigDecimalLayoutForm;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import com.timer.Timer;
import com.utils.UtilsFunctions;

import ij.*;
import ij.plugin.filter.Benchmark;

import ufrpe.com.imagebenchmark.*;

public class ImageBenchmarkLocal {
       private long time;
       private String errorMessage;

       // void
       public String localImageBenchmark(String filename, String outfilename) {
    	   String result = "";
    	   new Timer();
    	   Timer.reset();
           Timer.start();    
           
           Bitmap resizedBitmap = null;
           try {
	           
        	   BitmapFactory.Options opts = new BitmapFactory.Options();
	           opts.inJustDecodeBounds = false;
	           opts.inPreferredConfig = Config.RGB_565;
	           opts.inDither = true;
	           
        	   Bitmap bMap = BitmapFactory.decodeFile(
        			   UtilsFunctions.getFullFilename(filename), opts);
        	   
        	   float scaleWidth =  bMap.getWidth();
        	   float scaleHeight = bMap.getHeight();
	           
	           Matrix matrix = new Matrix();
	           matrix.setRotate(90, scaleWidth, scaleHeight);
	           
	           resizedBitmap = Bitmap.createBitmap(bMap, 0, 0, bMap.getWidth(), bMap.getHeight(), matrix, true);
	           
	           bMap.recycle();

	           bMap = null;
	           
	           FileOutputStream out = UtilsFunctions.saveImage(outfilename);
	           resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
	           
	           result = resizedBitmap.getHeight() + "" + resizedBitmap.getWidth() + "";
	           matrix = null;
	           resizedBitmap = null;
	           
	           System.gc();
	           System.gc();
	           
               } catch (Exception e) {
                   this.time = -1;
                   this.errorMessage = "LocalImageSizeFlip.localImageSizeFlip(): " + e;
               }
               
           Timer.stop();
           this.time = Timer.result();
           return result;
       }

       public String errorMessage() {
               return this.errorMessage;
       }

       public long returnTime() {
               return this.time;
       }
}