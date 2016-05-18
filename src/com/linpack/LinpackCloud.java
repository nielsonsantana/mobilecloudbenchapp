package com.linpack;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import com.ntp.SntpClient;
import com.timer.Timer;

public class LinpackCloud {
	private long totalTime = 0;
	private long requestTime = 0;
	private long responseTime = 0;
	private long computeTimeServer = 0;
	SntpClient sntpclient = null;
	
	private String errorMessage;
	private String returnValue = "dude";
	
	public void setSntpClient(SntpClient client){
		this.sntpclient = client;
	}
	
	public void runInteract(String inUrl) {
		try {
			new Timer();
			Timer.reset();
			Timer.start();
			URL url = new URL(inUrl);
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			String str;
			while((str = in.readLine()) != null) {
				this.returnValue = str;
				String [] csv = str.split(",");
				if(csv.length > 2){
					this.returnValue = String.valueOf(csv[0]); // Calc 
					this.requestTime = Long.parseLong(csv[1]);
					this.computeTimeServer = Long.parseLong(csv[2]);
					long currentServerTime = Long.parseLong(csv[3]);
					this.responseTime = System.currentTimeMillis() - currentServerTime;
				}
			}
			in.close();
			Timer.stop();
			this.totalTime = Timer.result();
			this.requestTime = (this.totalTime - this.computeTimeServer)/2;
			this.responseTime = this.requestTime;
		} catch (MalformedURLException e) {
			this.errorMessage = "PrimeInteract.primeInteract()" + e;
		} catch (IOException e) {
			this.errorMessage = "PrimeInteract.primeInteract()" + e;
		}
	}
	
	public long getRequestTime() {
		return requestTime;
	}

	public long getResponseTime() {
		return responseTime;
	}

	public long getComputeTimeServer() {
		return computeTimeServer;
	}
	

	public String errorMessage() {
		return this.errorMessage;
	}
	
    public String returnNumber() {
		return this.returnValue;
    }
	
	public long returnTime() {
		return this.totalTime;
	}
	
	
}
