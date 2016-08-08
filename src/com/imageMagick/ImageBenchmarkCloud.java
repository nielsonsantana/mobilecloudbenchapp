package com.imageMagick;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.timer.Timer;

public class ImageBenchmarkCloud {
	private long time;
	private String errorMessage;

	// void
    public String imageCloud(String urlIn, File filen, File tempSaveImage) {
    	
    	if(filen.exists() == true && tempSaveImage.exists() == true) {
	    	new Timer();
	    	Timer.reset();
	    	Timer.start();
	    	
	        Bitmap bMap = null;
	        HttpClient httpclient = new DefaultHttpClient();
	        try {
			    URL url = new URL(urlIn);
	
			    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			    String adress = "";
			    String str;
			    while ((str = in.readLine()) != null) {
			    		adress = str;
			    }
			    in.close();
	        	
	            HttpPost httppost = new HttpPost(adress);
	
	            FileBody bin = new FileBody(filen, "image/jpeg");
	
	            MultipartEntity reqEntity = new MultipartEntity();
	            reqEntity.addPart("myFile", bin);
	            
	            httppost.setEntity(reqEntity);
	            HttpResponse response = httpclient.execute(httppost);
	            HttpEntity resEntity = response.getEntity();
	
	            if (resEntity != null) {
	            	InputStream inputStr = resEntity.getContent();
	            	BufferedInputStream bis = new BufferedInputStream(inputStr);
	                
	            	bMap = BitmapFactory.decodeStream(bis);
	                FileOutputStream out = new FileOutputStream(tempSaveImage);
	                //do I need to compress it?
	                bMap.compress(Bitmap.CompressFormat.PNG, 100, out);
	                out.flush();
	                out.close();
	                if (inputStr != null) {
	                	inputStr.close();
	                }
	                if (bis != null) {
	                	bis.close();
	                }
	            }
	            httpclient.getConnectionManager().shutdown();            
	        } catch(Exception e) {
	        	this.errorMessage = "InteractorAppEngine.imageCloud(): " + e;
	        }  
	        Timer.stop();
	        time = Timer.result();
	        
			return bMap.getHeight() + "" + bMap.getWidth() + "";
    	} else {
    		this.errorMessage = "InteractorAppEngine.imageCloud(): inputError";
    		this.time = -1;
    		return "ImageTransformCloud Error";
    	}
    }
    
	public String errorMessage() {
		return this.errorMessage;
	}
    
    public long returnTime() {
    	return this.time;
    }
}