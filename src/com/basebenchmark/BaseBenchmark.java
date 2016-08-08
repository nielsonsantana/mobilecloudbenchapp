package com.basebenchmark;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import org.apache.http.HttpResponse;

import android.util.Log;

import com.ntp.SntpClient;
import com.timer.Timer;
import com.timer.Timer2;
import com.utils.Utils;

public class BaseBenchmark {
	
	private long totalResponseTime = 0;
	private float requestNetworkTime = 0;
	private float responseNetworkTime = 0;
	private long totalNetworkTime = 0;
	private long computeTimeOnServer = 0;
	SntpClient sntpclient = null;
	
	private String errorMessage = "";
	private String resultValue = "";
	
	public void setSntpClient(SntpClient client){
		this.sntpclient = client;
	}
	
	public void startBenchmarkLocal(HashMap<String, String> paramets) {
		Timer2 timer = new Timer2();
		timer.start();
		
		this.resultValue = runBenchmark(paramets);
		
		timer.stop();
		
		this.totalResponseTime = timer.result();
		this.totalNetworkTime = (this.totalResponseTime - this.computeTimeOnServer);
		
		this.requestNetworkTime = Utils.round2Float((this.totalResponseTime - this.computeTimeOnServer)/2.0);
		this.responseNetworkTime = this.requestNetworkTime;
	}
	
	public void startBenchmarkCloud(HashMap<String, String> paramets) {		
		Timer2 timer = new Timer2();
		
		HttpResponse response = runBenchmark(paramets, timer);
				
		this.computeTimeOnServer = Long.parseLong(response.getFirstHeader("compute-time-server").getValue());
		
		this.totalResponseTime = timer.result();
		this.totalNetworkTime = (this.totalResponseTime - this.computeTimeOnServer);
		
		this.requestNetworkTime = (this.totalResponseTime - this.computeTimeOnServer)/2;
		this.responseNetworkTime = this.requestNetworkTime;
	}
	
	public long getComputeTimeOnServer() {
		return computeTimeOnServer;
	}

	public void setComputeTimeOnServer(long computeTimeOnServer) {
		this.computeTimeOnServer = computeTimeOnServer;
	}
	
	public String runBenchmark(HashMap<String, String> paramets){
		throw new UnsupportedOperationException("Method not implemented yet.");
	}
	
	public HttpResponse runBenchmark(HashMap<String, String> paramets, Timer2 timer){
		throw new UnsupportedOperationException("Method not implemented yet.");
	}

	public float getTotalResponseTime() {
		return totalResponseTime;
	}

	public long getTotalNetworkTime() {
		return totalNetworkTime;
	}

	public void setTotalResponseTime(long totalResponseTime) {
		this.totalResponseTime = totalResponseTime;
	}

	public String getResultValue() {
		return resultValue;
	}

	public void setResultValue(String resultValue) {
		this.resultValue = resultValue;
	}

	public float getRequestNetworkTime() {
		return requestNetworkTime;
	}

	public float getResponseNetworkTime() {
		return responseNetworkTime;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	
}
