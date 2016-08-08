package com.starter;

import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.util.Log;

import com.downloaduploadspeed.DownloadUploadSpeed;
import com.image.ImageTransformCloud;
import com.image.ImageTransformLocal;
import com.image2.ImageBenchmarkLocal;
import com.linpack.LinpackCloud;
import com.linpack.LinpackLocal;
import com.listsorter.ListSorterLocal;
import com.listsorter.ListSorterCloud;
import com.ntp.SntpClient;
import com.primecalc.PrimeCalcLocal;
import com.primecalc.PrimeCalcCloud;

public class Starter {
	
	public DataSaver data;
	private String key = "1234";
	SntpClient sntpclient = null;
	
	public void dataSaverInit() {
		this.data = new DataSaver();
		this.data.setKey(this.key);
	}
	
	public void setSntpClient(SntpClient client){
		this.sntpclient = client;
	}
	
	public void downloadUploadSpeed(File filen, File downloadTempFile) {
		try {
			String urlUpload = "http://example.appspot.com/upload?validate=1234";
			String urlDownload = "http://example.appspot.com/serve?validate=1234";
			
			DownloadUploadSpeed downUpSpeed = new DownloadUploadSpeed();
			this.data.setDownloadSpeed(Float.toString(downUpSpeed.downloadUrl(downloadTempFile, urlDownload)));
			this.data.setUploadSpeed(Float.toString(downUpSpeed.uploadUrl(filen, urlUpload)));
			this.data.setErrorMessage(downUpSpeed.getErrorMessage());
		} catch (Exception e) {
			this.data.setErrorMessage("Starter.uploadDownloadSpeed(): " + e);
		}
	}
	
	public void primeCalc2(int max_limit, boolean runLocal, boolean runCloud) {
//		String inUrl = "http://177.71.149.2:8080/ServletBenchmarks/primecalc2?validate=1234";
		String inUrl = "http://177.71.149.2:5000/primecalc?validate=1234";
		
		HashMap<String, String> p = new HashMap<String, String>();
		
		if(runLocal){
			p.put("input", max_limit+"");
			PrimeCalcLocal primeCalc = new PrimeCalcLocal();
			primeCalc.startBenchmarkLocal(p);
			
			this.data.setTotalTimeLocalResult(Float.toString(primeCalc.getTotalResponseTime()));
			this.data.setErrorMessage(primeCalc.getErrorMessage());
		}
		
		if(runCloud){
			inUrl += "&max_limit=" + String.valueOf(max_limit);
			inUrl += "&send_time=" + System.currentTimeMillis();
			
			p.put("input", inUrl);
			Log.i("cloudbench1", inUrl);
			PrimeCalcCloud pcloud = new PrimeCalcCloud();
			pcloud.startBenchmarkCloud(p);
			this.data.setComputeTimeServerCloudResult(String.valueOf(pcloud.getComputeTimeOnServer()));
			this.data.setRequestTimeCloudResult(String.valueOf(pcloud.getRequestNetworkTime()));
			this.data.setResponseTimeCloudResult(String.valueOf(pcloud.getResponseNetworkTime()));
			Log.i("cloudbench1", this.data.getComputeTimeServerCloudResult());
			this.data.setErrorMessage(pcloud.getErrorMessage());
			
			this.data.setTotalTimeCloudResult(Float.toString(pcloud.getTotalResponseTime()));
		}
	}
	
	public void linpackCalc(int parameter, boolean runLocal, boolean runCloud) {
//		String inUrl = "http://177.71.149.2:8080/ServletBenchmarks/linpackcalc?validate=1234";
		String inUrl = "http://177.71.149.2:5000/linpack?validate=1234";

		HashMap<String, String> p = new HashMap<String, String>();
		
		if(runLocal){
			
			p.put("input", parameter+"");
			
			LinpackLocal pCalc = new LinpackLocal();
			pCalc.startBenchmarkLocal(p);
			
			this.data.setTotalTimeLocalResult(Float.toString(pCalc.getTotalResponseTime()));
		}
		
		if(runCloud){
			
			inUrl += "&parameter=" + String.valueOf(parameter);
			inUrl += "&send_time=" + System.currentTimeMillis();
			Log.i("cloudbench1", inUrl);
			
			p.put("input", inUrl);
			
			LinpackCloud pcloud = new LinpackCloud();
			pcloud.startBenchmarkCloud(p);
			this.data.setComputeTimeServerCloudResult(String.valueOf(pcloud.getComputeTimeOnServer()));
			this.data.setRequestTimeCloudResult(String.valueOf(pcloud.getRequestNetworkTime()));
			this.data.setResponseTimeCloudResult(String.valueOf(pcloud.getResponseNetworkTime()));
//			Log.i("cloudbench1", this.data.getComputeTimeServerCloudResult());
//			this.data.setErrorMessage(pcloud.errorMessage());
//			String primeCalcCloudTest = pcloud.returnNumber();
			this.data.setTotalTimeCloudResult(Float.toString(pcloud.getTotalResponseTime()));
		}
	}
	
	public void imageBenchmark(String fileNameInput, String outfilename, boolean runLocal, boolean runCloud) {
		
		String inUrl = "http://177.71.149.2:8080/ServletBenchmarks/linpackcalc?validate=1234";

		inUrl += "&send_time=" + System.currentTimeMillis();
		Log.i("cloudbench1", inUrl);
		
		if(runLocal){
			ImageBenchmarkLocal imgbm = new ImageBenchmarkLocal();
			imgbm.localImageBenchmark(fileNameInput, outfilename);
			this.data.setTotalTimeLocalResult(Float.toString(imgbm.returnTime()));
			return;
		}
		
		if(runCloud){
			
			ImageTransformLocal imgbm = new ImageTransformLocal();
//			String imageTransformLocal = local.localImageSizeFlip(in, tempSaveImage1);
			this.data.setImageTransformLocalResult(Float.toString(imgbm.returnTime()));
			this.data.setErrorMessage(imgbm.errorMessage());
			
			String urlIn = "http://example.appspot.com/upload?validate=1234";
			ImageTransformCloud cloud = new ImageTransformCloud();
//			String imageTransformCloud = cloud.imageCloud(urlIn, filen, tempSaveImage2);
			this.data.setImageTransformCloudResult(Float.toString(cloud.returnTime()));
			this.data.setErrorMessage(cloud.errorMessage());
//			this.data.setTestData(imageTransformLocal + "==" + imageTransformCloud);
			
			//return imageTransformLocal + "==" + imageTransformCloud;
			this.data.setTotalTimeCloudResult(Float.toString(imgbm.returnTime()));
			
		}
	} 
	
	public void listSorter(List<String> wordList, String textString) {
	//public String listSorter(List<String> wordList, String textString) {
		String urlForm = "http://master-listsort2.appspot.com/";
		String urlPost = "http://master-listsort2.appspot.com/sign";
		
		ListSorterLocal listSorter = new ListSorterLocal();
		// listSorter.listSorter(wordList);
		String listSortLocal = listSorter.listSorter(wordList);
		this.data.setListSorterLocalResult(Float.toString(listSorter.returnTime()));
		this.data.setErrorMessage(listSorter.errorMessage());
		
		ListSorterCloud listSorterCloud = new ListSorterCloud();
		// listSorterCloud.listSorterCloud(textString, urlForm, urlPost);
		String listSortCloud = listSorterCloud.listSorterCloud(textString, urlForm, urlPost);
		this.data.setListSorterCloudResult(Float.toString(listSorterCloud.returnTime()));
		this.data.setErrorMessage(listSorterCloud.errorMessage());
		this.data.setTestData(listSortLocal + "==" + listSortCloud+"&");
		
		//
		//return "ListSortLocal: " + listSortLocal + " ListSorterCloud: " + listSortCloud;
	}
	
	//public void imageTran(InputStream in, File filen, File tempSaveImage) {		
	public void imageTran(InputStream in, File filen, File tempSaveImage1, File tempSaveImage2) {		
		ImageTransformLocal local = new ImageTransformLocal();
		String imageTransformLocal = local.localImageSizeFlip(in, tempSaveImage1);
		this.data.setImageTransformLocalResult(Float.toString(local.returnTime()));
		this.data.setErrorMessage(local.errorMessage());
		
		String urlIn = "http://example.appspot.com/upload?validate=1234";
		ImageTransformCloud cloud = new ImageTransformCloud();
		String imageTransformCloud = cloud.imageCloud(urlIn, filen, tempSaveImage2);
		this.data.setImageTransformCloudResult(Float.toString(cloud.returnTime()));
		this.data.setErrorMessage(cloud.errorMessage());
		this.data.setTestData(imageTransformLocal + "==" + imageTransformCloud);
		
		//return imageTransformLocal + "==" + imageTransformCloud;
	}
	
	
	public void savePhoneInfo(String phoneModel, String fingerPrint, String phoneSDK, String connectionInfo) {
		this.data.setPhoneDetails(phoneModel, fingerPrint, phoneSDK, connectionInfo);
	}
	
	public void dataSaverFinnish() {
		String urlGet = "http://master-datasaver2.appspot.com/";
		String urlPost = "http://master-datasaver2.appspot.com/appenginedatasaver";
		this.data.sendResults(urlGet, urlPost);
	}
}