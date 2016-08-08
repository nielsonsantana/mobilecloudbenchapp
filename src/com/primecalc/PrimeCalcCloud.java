package com.primecalc;
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

import com.basebenchmark.BaseBenchmark;
import com.timer.Timer2;

public class PrimeCalcCloud extends BaseBenchmark{
	
	String TAG = "PrimeCalc-CloudBench";
	
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
			setErrorMessage("PrimeCalc.runBenchmark()" + e);
		} catch (IOException e) {
			setErrorMessage("PrimeCalc.runBenchmark()" + e);
		}
		return response;
	}
	
//	public void primeInteract(String inUrl) {
//		try {
//			new Timer();
//			Timer.reset();
//			Timer.start();
//			URL url = new URL(inUrl);
//			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
//			String str;
//			while((str = in.readLine()) != null) {
//				this.returnValue = str;
//				String [] csv = str.split(",");
//				if(csv.length > 2){
//					this.returnValue = String.valueOf(csv[0]); // Calc 
//					this.requestTime = Long.parseLong(csv[1]);
//					this.computeTimeServer = Long.parseLong(csv[2]);
//					long currentServerTime = Long.parseLong(csv[3]);
//					this.responseTime = System.currentTimeMillis() - currentServerTime;
//				}
//			}
//			in.close();
//			Timer.stop();
//			this.totalTime = Timer.result();
//			this.requestTime = (this.totalTime - this.computeTimeServer)/2;
//			this.responseTime = this.requestTime;
//		} catch (MalformedURLException e) {
//			this.errorMessage = "PrimeInteract.primeInteract()" + e;
//		} catch (IOException e) {
//			this.errorMessage = "PrimeInteract.primeInteract()" + e;
//		}
//	}
	
//	public void syncTimeServer(String inUrl) {
//		try {
//			new Timer();
//			Timer.reset();
//			Timer.start();
//			URL url = new URL(inUrl);
//			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
//			String str;
//			while((str = in.readLine()) != null) {
//				this.returnValue = str;
//				String [] csv = str.split(",");
//				if(csv.length > 2){
//					this.returnValue = String.valueOf(csv[0]);
//					this.requestTime = Long.parseLong(csv[1]);
//					this.computeTimeServer = Long.parseLong(csv[2]);
//					long currentServerTime = Long.parseLong(csv[3]);
//					this.responseTime = sntpclient.getNtpTime() - currentServerTime;
//				}
//			}
//			in.close();
//			Timer.stop();
//			this.totalTime = Timer.result();
//			//this.requestTime = (this.totalTime - this.computeTimeServer)/2;
//			//this.responseTime = this.requestTime;
//		} catch (MalformedURLException e) {
//			this.errorMessage = "PrimeInteract.primeInteract()" + e;
//		} catch (IOException e) {
//			this.errorMessage = "PrimeInteract.primeInteract()" + e;
//		}
//	}
	
//	public long getRequestTime() {
//		return requestTime;
//	}
//
//	public long getResponseTime() {
//		return responseTime;
//	}
//
//	public long getComputeTimeServer() {
//		return computeTimeServer;
//	}
//	
//
//	public String errorMessage() {
//		return this.errorMessage;
//	}
//	
//    public String returnNumber() {
//		return this.returnValue;
//    }
//	
//	public long returnTime() {
//		return this.totalTime;
//	}
	
	
}
