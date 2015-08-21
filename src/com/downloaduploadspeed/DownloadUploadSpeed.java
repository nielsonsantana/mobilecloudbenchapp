package com.downloaduploadspeed;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;

import java.net.URL;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;

import com.timer.Timer;

public class DownloadUploadSpeed {
	private String errorMessage = "";
	
	public float downloadUrl(File downloadTempFile, String urlString) {
		
		if(downloadTempFile.exists() == true) {
			BufferedInputStream in = null;
	        FileOutputStream fout = null;
	        
	        new Timer();
	        Timer.reset();        
	        Timer.start();
	        try {
	            in = new BufferedInputStream(new URL(urlString).openStream());
	            fout = new FileOutputStream(downloadTempFile);
	
	            byte data[] = new byte[1024];
	            int count;
	            while ((count = in.read(data, 0, 1024)) != -1) {
	            	fout.write(data, 0, count);
	            }
	            if (in != null) {
	                in.close();
	                if (fout != null) {
	                	fout.close();
	                }  
	            }          
			} catch(MalformedURLException e) {
				this.errorMessage = "DownloadUploadSpeed.downloadUrl(): " + e;
			} catch(IOException e) {
				this.errorMessage = "DownloadUploadSpeed.downloadUrl(): " + e;
			}
	    	
		    Timer.stop();
		    return (downloadTempFile.length()/(Timer.result()));
		} else {
			this.errorMessage = "DownloadUploadSpeed.downloadUrl(): inputError";
			return -1;
		}
	}
	
	public float uploadUrl(File filen, String urlString) {
		if(filen.exists() == true) {
	        HttpClient httpclient = new DefaultHttpClient();
	        new Timer();
	        Timer.reset();
	        try {
			    URL url = new URL(urlString);
	
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
	
	            Timer.start();
	            httpclient.execute(httppost);
	            Timer.stop();
	            
	        } catch(Exception e) {
	        	this.errorMessage = "DownloadUploadSpeed.uploadUrl(): " + e;
	        }
	        try { 
	        	httpclient.getConnectionManager().shutdown(); 
	    	} catch (Exception e) {
	    		this.errorMessage = "DownloadUploadSpeed.uploadUrl()" + e;
			}
			return (filen.length()/(Timer.result()));
		} else {
			this.errorMessage = "DownloadUploadSpeed.uploadUrl(): inputError";
			return -1;
		}
    }
	
	public String getErrorMessage() {
		return this.errorMessage;
	}
}