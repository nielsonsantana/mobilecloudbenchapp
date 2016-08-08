package com.starter;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.R.string;

public class DataSaver { 
	private String key;
	private String downloadSpeed;
	private String uploadSpeed;
	private String errorMessage = "";
	private String primeCalcLocalResult;
	
	private String resultCloud = "";
	private String resultLocal = "";
	
	private String primeCalcCloudResult = "";
	private String totalTimeLocalResult = "";
	private String totalTimeCloudResult = "";
	private String computeTimeLocalResult = "";
	private String computeTimeServerCloudResult = "";
	private String requestTimeCloudResult = "";
	private String responseTimeCloudResult = "";
	
	private String listSorterLocalResult;
	
	public String getComputeTimeLocalResult() {
		return computeTimeLocalResult;
	}

	public void setComputeTimeLocalResult(String computeTimeLocalResult) {
		this.computeTimeLocalResult = computeTimeLocalResult;
	}

	public String getComputeTimeServerCloudResult() {
		return computeTimeServerCloudResult;
	}

	public void setComputeTimeServerCloudResult(String computeTimeCloudResult) {
		this.computeTimeServerCloudResult = computeTimeCloudResult;
	}

	public String getRequestTimeCloudResult() {
		return requestTimeCloudResult;
	}

	public void setRequestTimeCloudResult(String sendTimeCloudResult) {
		this.requestTimeCloudResult = sendTimeCloudResult;
	}

	public String getResponseTimeCloudResult() {
		return responseTimeCloudResult;
	}

	public String getTotalTimeCloudResult() {
		return totalTimeCloudResult;
	}

	public void setTotalTimeCloudResult(String totalTimeCloudResult) {
		this.totalTimeCloudResult = totalTimeCloudResult;
	}

	public void setResponseTimeCloudResult(String responseTimeCloudResult) {
		this.responseTimeCloudResult = responseTimeCloudResult;
	}
	
	public String getTotalTimeLocalResult() {
		return totalTimeLocalResult;
	}

	public void setTotalTimeLocalResult(String totalTimeLocalResult) {
		this.totalTimeLocalResult = totalTimeLocalResult;
	}

	public long getLogLocalResultMilliseconds() {
		return (long)(Float.valueOf(this.totalTimeLocalResult)*1);
	}
	
	public String getLogLocalResult() {
		if(this.totalTimeLocalResult != "")
			return (Float.valueOf(this.totalTimeLocalResult)/1000.) + "";
		return null;
	}
	
	private String ToSecondsOrNull(String value){
		if(value != ""){
			return (Float.valueOf(value)/1000.)+"";
		}
		return null;
	}
	
	public String getLogCloudResult() {
		return ToSecondsOrNull(this.requestTimeCloudResult)+ ",  " + ToSecondsOrNull(this.computeTimeServerCloudResult) + 
				",  " + ToSecondsOrNull(this.responseTimeCloudResult)+ ",  "+ ToSecondsOrNull(this.getTotalTimeCloudResult());
	}

	public String getPhoneInfo() {
		return phoneInfo;
	}

	public void setPhoneInfo(String phoneInfo) {
		this.phoneInfo = phoneInfo;
	}

	public String getKey() {
		return key;
	}

	public String getDownloadSpeed() {
		return downloadSpeed;
	}

	public String getUploadSpeed() {
		return uploadSpeed;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public String getPrimeCalcLocalResult() {
		return primeCalcLocalResult;
	}
	
	public String getPrimeCalcCloudResult() {
		return primeCalcCloudResult ;
	}
	
	public String getLogPrimeCalcCloudResult() {
		return this.requestTimeCloudResult + ",  " + this.computeTimeServerCloudResult + 
				",  " + this.responseTimeCloudResult + ",  "+ this.getTotalTimeCloudResult();
	}
	
	public String getListSorterLocalResult() {
		return listSorterLocalResult;
	}

	public String getListSorterCloudResult() {
		return listSorterCloudResult;
	}

	public String getImageTransformLocalResult() {
		return imageTransformLocalResult;
	}

	public String getImageTransformCloudResult() {
		return imageTransformCloudResult;
	}

	public String getTestData() {
		return testData;
	}

	private String listSorterCloudResult;
	private String imageTransformLocalResult;
	private String imageTransformCloudResult;
	private String phoneInfo;
	private String testData = "";
	
	public void setPhoneDetails(String phoneModel, String fingerPrint, String phoneSDK, String connectionInfo) {
		this.phoneInfo = "PhoneModel: " + phoneModel + " &FingerPrint: " + fingerPrint + " &SDK: " + phoneSDK + " &Connection: " + connectionInfo;  
	}
	
	public void setDownloadSpeed(String download) {
		this.downloadSpeed = download;
	}
	
	public void setUploadSpeed(String upload) {
		this.uploadSpeed = upload;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public void setErrorMessage(String error) {
		if(error != null) {
			this.errorMessage += error;
		}
	}
	
	public void setPrimeCalcLocalResult(String primeCalcLocalResult) {
		this.primeCalcLocalResult = primeCalcLocalResult;
	}
	
	public void setPrimeCalcCloudResult(String primeCalcCloudResult) {
		this.primeCalcCloudResult = primeCalcCloudResult;
	}
	
	public void setListSorterLocalResult(String listSorterLocalResult) {
		this.listSorterLocalResult = listSorterLocalResult;
	}
	
	public void setListSorterCloudResult(String listSorterCloudResult) {
		this.listSorterCloudResult = listSorterCloudResult;
	}
	
	public void setImageTransformLocalResult(String imageTransformLocalResult) {
		this.imageTransformLocalResult = imageTransformLocalResult;
	}
	
	public void setImageTransformCloudResult(String imageTransformCloudResult) {
		this.imageTransformCloudResult = imageTransformCloudResult;
	}	
	
	public void setTestData(String testData) {
		this.testData += testData;
	}
	
	public void sendResults(String urlGet, String urlPost) {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        try {
            HttpGet httpget = new HttpGet(urlGet);

            HttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            entity.consumeContent();
            //EntityUtils.consume(entity);

            HttpPost httpost = new HttpPost(urlPost);

            List <NameValuePair> nvps = new ArrayList <NameValuePair>();
            nvps.add(new BasicNameValuePair("key", this.key));
            nvps.add(new BasicNameValuePair("errorMessage", this.errorMessage));
            nvps.add(new BasicNameValuePair("speedDownload", this.downloadSpeed));
            nvps.add(new BasicNameValuePair("speedUpload", this.uploadSpeed));
            nvps.add(new BasicNameValuePair("primeCalcLocalResult", this.primeCalcLocalResult));
            nvps.add(new BasicNameValuePair("primeCalcCloudResult", this.primeCalcCloudResult));
            nvps.add(new BasicNameValuePair("listSorterLocalResult", this.listSorterLocalResult));
            nvps.add(new BasicNameValuePair("listSorterCloudResult", this.listSorterCloudResult));
            nvps.add(new BasicNameValuePair("imageTransformLocalResult", this.imageTransformLocalResult));
            nvps.add(new BasicNameValuePair("imageTransformCloudResult", this.imageTransformCloudResult));
            nvps.add(new BasicNameValuePair("phoneInfo", this.phoneInfo));
            nvps.add(new BasicNameValuePair("testData", this.testData));
            httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

            response = httpclient.execute(httpost);
            entity = response.getEntity();
            entity.consumeContent();
        
	    } catch(Exception e) {
        	System.out.println(e);
        } finally {
            httpclient.getConnectionManager().shutdown();
        }	
    }
}
