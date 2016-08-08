package com.linpack;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.util.Log;

import com.basebenchmark.BaseBenchmark;
import com.timer.Timer2;

public class LinpackCloud extends BaseBenchmark{
	
	String TAG = "LinpackCloud-CloudBench";
	
	@Override
	public HttpResponse runBenchmark(HashMap<String, String> paramets, Timer2 timer){
		HttpResponse response = null;
		try {
			
			String inUrl = paramets.values().toArray()[0].toString();
			timer.reset();
			timer.start();
			
			// https://blog.dahanne.net/2009/08/16/how-to-access-http-resources-from-android/
			HttpGet httpGet = new HttpGet(inUrl);
			HttpClient httpclient = new DefaultHttpClient();
			// Execute HTTP Get Request
			response = httpclient.execute(httpGet);
			
			String content = EntityUtils.toString(response.getEntity());
			setResultValue(content);
			
			timer.stop();
			
			
		} catch (MalformedURLException e) {
			setErrorMessage("Linpack.runBenchmark()" + e);
		} catch (IOException e) {
			setErrorMessage("Linpack.runBenchmark()" + e);
		}
		return response;
	}
	
	
}
