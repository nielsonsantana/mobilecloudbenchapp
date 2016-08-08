package com.imageMagick;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Formatter.BigDecimalLayoutForm;

import magick.ImageMagick;
import magick.MagickImage;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.Image;
import android.util.Log;

import com.timer.Timer;
import com.utils.Utils;

import ij.*;
import ij.plugin.filter.Benchmark;
import ij.plugin.filter.ImageMath;

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
        	   
        	   MagickImage mi =  new MagickImage();
	           
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