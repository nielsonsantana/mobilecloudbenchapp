package com.listsorter;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import com.timer.Timer;

public class ListSorterCloud {
	private long time;
	private String errorMessage;
	
	//	public void listSorterCloud(String textString, String urlForm, String urlPost) {
	public String listSorterCloud(String textString, String urlForm, String urlPost) {
		if(textString.length() > 100) {
			List<String> wordListCloud = new ArrayList<String>();
	        wordListCloud.clear();
			
	        DefaultHttpClient httpclient = new DefaultHttpClient();
	        try {
				HttpGet httpget = new HttpGet(urlForm);
	
	            HttpResponse response = httpclient.execute(httpget);
	            HttpEntity entity = response.getEntity();
	            entity.consumeContent();
	
	            HttpPost httpost = new HttpPost(urlPost);
	
	            List <NameValuePair> nvps = new ArrayList <NameValuePair>();
	            nvps.add(new BasicNameValuePair("content", textString));
	            nvps.add(new BasicNameValuePair("validate", "1234")); 
	            httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
	
	    		new Timer();
	    		Timer.reset();
	    		Timer.start();
	            response = httpclient.execute(httpost);
	            entity = response.getEntity();
			    
	        	StringWriter writer = new StringWriter();
	        	IOUtils.copy(entity.getContent(), writer, "UTF-8");
	        	String theString = writer.toString();
	        	String[] total = theString.split("\n");
	        	wordListCloud = Arrays.asList(total);
	        	Timer.stop();
	        	entity.consumeContent();
		    } catch(Exception e) {
		    	this.errorMessage = "ListSorterCloud.listSorterCloud(): " + e;
	        } finally {
	            httpclient.getConnectionManager().shutdown();
	        }
	        this.time = Timer.result();
	        
	        //
	        return wordListCloud.get(500) + "" + wordListCloud.get(501);
		} else {
			this.time = -1;
			this.errorMessage = "ListSorterCloud.listSorterCloud(): inputError"; 
			return "ListSorterCloud Error";
		}
	}
	
	public String errorMessage() {
		return this.errorMessage;
	}
	
	public long returnTime() {
		return this.time;
	}	
}
