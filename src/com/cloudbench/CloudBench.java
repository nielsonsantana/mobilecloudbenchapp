package com.cloudbench;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.UUID;

import com.primecalc.PrimeCalcLocal;
import com.starter.Starter;
import com.linpack.LinpackLocal;
import com.listsorter.ListSorterCloud;
import com.listsorter.ListSorterLocal;
import com.timer.Timer;
import com.utils.UtilsFunctions;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SlidingDrawer;
import android.widget.Spinner;
import android.widget.TextView; 
import android.widget.Toast;

import android.os.BatteryManager;
//import android.os.ServiceManager;
import android.content.Context;
import android.content.res.Resources.Theme;

/*
 * 
 * Created by Oskar Hamren as part of the thesis 
 * work on mobile cloud computing, 2011-2012
 * 
 */


public class CloudBench extends Activity implements OnClickListener, Runnable {
	final List<String> wordList = new ArrayList<String>();
	String textString = "";
	String TAG = "CloudBench";
    private ProgressBar progressBar;
    private TextView status;
    private TextView creator;
    private View firstButton;
    //private View thirdButton;    
    private View forthButton;
    private File filen;
    private File downloadTempFile;
    private File tempSaveImage1;
    private File tempSaveImage2;
    private InputStream is;
    private String phoneModel;
    private String fingerPrint;
    private String phoneSDK;
    private String connectionInfo;
    private Button bt_local;
    private Button bt_cloud;
    private Spinner spinner_benchmarks;
    
    private String fileNameLog = "/sdcard/outlogcloudbench";
    private Thread threadMetricCollector;
    
    final com.ntp.SntpClient sntpclient = new com.ntp.SntpClient(); ;
    
    private int experiment = 0;
    private boolean runLocal = false;
    private boolean runCloud = false;
    
    Object lock = new Object();
    /*
    private String listSorterTest = "hang ten";
    private String primeCalcTest = "nada surf";
    private String imageTransformTest = "bro";
    */
    
    @Override
    public void onCreate(Bundle savedInstanceState) {  	        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
      Intent cbservice = new Intent(this, ServiceCloudBench.class);
//      startService(cbservice);

        
        firstButton = findViewById(R.id.button1);
        firstButton.setOnClickListener(this);
       
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        status = (TextView) findViewById(R.id.status);  
        creator = (TextView) findViewById(R.id.textView1);        
        forthButton = findViewById(R.id.button4);
        forthButton.setOnClickListener(this);
        
        //thirdButton = findViewById(R.id.button3);
        //thirdButton.setOnClickListener(this);

        firstButton.setVisibility(View.INVISIBLE);
        forthButton.setVisibility(View.INVISIBLE);
        creator.setVisibility(View.INVISIBLE);
        
        bt_local = (Button)findViewById(R.id.bt_run_local);
        bt_local.setOnClickListener(onClickRunTaskLocal);
        
        bt_cloud = (Button)findViewById(R.id.bt_run_cloud);
        bt_cloud.setOnClickListener(onClickRunTaskCloud);
        
        spinner_benchmarks = (Spinner)findViewById(R.id.spinner1);
        
        Toast.makeText(getApplicationContext(), String.valueOf(getBatteryLevel()), Toast.LENGTH_LONG).show();
        
//        new Thread(new Runnable() {
//            public void run() {
//            	handler.sendEmptyMessage(1);
//                .gatherInfo();
//        		handler.sendEmptyMessage(2);
//                loadTextFile();
//        		handler.sendEmptyMessage(3);
//                loadImageFile();
//        		handler.sendEmptyMessage(4);
//            }
//            
//            private Handler handler = new Handler() {
//
//                @Override
//                public void handleMessage(Message msg) {
//                    switch (msg.what) {
//        	            case 1:
//        	            	status.setText(R.string.loading1);  
//        	            	progressBar.setProgress(25);
//        	                break;
//                    
//        	            case 2:
//        	                status.setText(R.string.loading2);  
//        	            	progressBar.setProgress(50);
//        	                break;     
//        	                
//        	            case 3:
//        	                status.setText(R.string.loading3);  
//        	            	progressBar.setProgress(75);
//        	                break; 	 
//        	                
//        	            case 4:
//        	                status.setText(R.string.loading4);  
//        	            	progressBar.setProgress(100);
//                        	firstButton.setVisibility(View.VISIBLE);
//                        	forthButton.setVisibility(View.VISIBLE);
//                        	creator.setVisibility(View.VISIBLE);
//                            progressBar.setVisibility(ProgressBar.INVISIBLE);
//                            progressBar.setProgress(0);
//                            status.setVisibility(View.INVISIBLE);
//        	                break;     
//                        }
//                    }
//            	};        	
//          }).start();
//        

    }
    
    public boolean syncSntp(){
    	  if (sntpclient.requestTime("ntp.ubuntu.com",10000)) {
    		  Toast.makeText(getApplicationContext(), String.valueOf(sntpclient.getNtpTime()), Toast.LENGTH_LONG).show();
    		  return true;
    	  }
    	  Toast.makeText(getApplicationContext(), "Not syncronized", Toast.LENGTH_LONG).show();
    	  return false;
    }
    	  
	private void gatherInfo() {
		this.phoneModel = android.os.Build.MODEL;
		this.phoneSDK = android.os.Build.VERSION.SDK;
		
		this.fingerPrint = phoneFingerPrint();
		this.connectionInfo = phoneConnection();

	}
	
	private String phoneConnection() {
		ConnectivityManager mConnectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		TelephonyManager mTelephony = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		
		NetworkInfo info = mConnectivity.getActiveNetworkInfo();
		if (info == null || !mConnectivity.getBackgroundDataSetting()) {
			return "NetworkError: Network == null ?";
		} else {
		
			int netType = info.getType();
			int netSubtype = info.getSubtype();
			if (netType == ConnectivityManager.TYPE_WIFI) {
				return "WIFI: " + netType;
			} else if (netType == ConnectivityManager.TYPE_MOBILE && netSubtype == TelephonyManager.NETWORK_TYPE_UMTS && !mTelephony.isNetworkRoaming()) {
				return "Phone: " + netType;
			} else {
				return "NetworkError2: Some other connection?";	
			}
		}	
	}
	
	private String phoneFingerPrint() {
	    final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
	    
	    //final String tmDevice, tmSerial, tmPhone, androidId;
	    final String tmDevice, tmSerial, androidId;
	    tmDevice = "" + tm.getDeviceId();
	    tmSerial = "" + tm.getSimSerialNumber();
	    androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

	    UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
	   return deviceUuid.toString();
	}
    
	private void loadTextFile() {
        InputStream is;
        
		try {
			is = getAssets().open("text.txt");

	        BufferedReader br = new BufferedReader(new InputStreamReader(is));
	        String line = null;
	        while ((line = br.readLine()) != null) {
	        	this.wordList.add(line); 
	        	this.textString += line + " ";
	        }
	        br.close();
	        Collections.shuffle(this.wordList);
	        //this.listSorterTest = this.wordList.get(200) + " - " + this.wordList.get(201);
        	
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

	private void loadImageFile() {
		try {
			this.is = getAssets().open("computer.jpg");
			
			File root = Environment.getExternalStorageDirectory();
			
            this.filen = new File(root, "computer1.jpg");
            this.filen.createNewFile();
            
            this.downloadTempFile = new File(root, "computer2.jpg");
            this.downloadTempFile.createNewFile();
            
            this.tempSaveImage1 = new File(root, "computer3.jpg");
            this.tempSaveImage1.createNewFile();
            
            this.tempSaveImage2 = new File(root, "computer4.jpg");
            this.tempSaveImage2.createNewFile();

            OutputStream out = new FileOutputStream(filen);
            byte buf[] = new byte[1024];
            int len;
            while((len = is.read(buf)) > 0) {
            	out.write(buf,0,len);
            }
            
            this.downloadTempFile.deleteOnExit();
            this.filen.deleteOnExit();
            this.tempSaveImage1.deleteOnExit();
            this.tempSaveImage1.deleteOnExit();
            
            out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	@Override
	public void onClick(View v) {
        switch(v.getId()){
        case R.id.button1:
        	
			firstButton.setVisibility(View.INVISIBLE);
			forthButton.setVisibility(View.INVISIBLE);
			creator.setVisibility(View.INVISIBLE);
			status.setText(R.string.start);
			progressBar.setVisibility(ProgressBar.VISIBLE);
			progressBar.setProgress(0);
			status.setVisibility(View.VISIBLE);
			
			new Thread(this).start();

        break;

        
        case R.id.button4:
        	this.finish();
    	break;        
        }
	}
	
	OnClickListener onClickRunTaskLocal = new OnClickListener(){

		@Override
		public void onClick(View v) {
			Toast.makeText(getApplicationContext(), "Runnning local", Toast.LENGTH_SHORT).show();
			
			int position = spinner_benchmarks.getSelectedItemPosition();
			experiment = position;
			runLocal = true;
			runCloud = false;
			runExperiment(position, true, false);
		}
	};
	
	OnClickListener onClickRunTaskCloud = new OnClickListener(){

		@Override
		public void onClick(View v) {
			
			int position = spinner_benchmarks.getSelectedItemPosition();
			experiment = position;
			runLocal = false;
			runCloud = true;
			runExperiment(position, false, true);
			
		}
	};
	
	public final void finishActivity(){
		android.os.Process.killProcess(android.os.Process.myPid());
	}
	
	
	public void runExperiment(int experiment, boolean runLocal, boolean runCloud){
		ArrayList<Integer> inputList = new ArrayList<Integer>();
		ArrayList<TaskItem> inputListTask = new ArrayList<TaskItem>();
		
		int execucoes = 0;
		String benchmarkName = "";
		int initialInput = 1000;
		int fatorIncrease = 1000;
		int sleep_time_replication = 0;
		int constant_time = 0;
		boolean estress_test = true;
		Log.i("cloudbench1", "runLocal:"+ Boolean.toString(runLocal) +" runCloud:" + Boolean.toString(runCloud));
		ArrayList<String> list = null;
		String line = "";
		
		switch (experiment) {
		case 0:
			break;
		
		case 1:
			list = UtilsFunctions.readFile("benchmark/image/input.txt");
			for(int i = 0; i < list.size(); i++){
				TaskItem ti = new TaskItem();
				line = list.get(i);
				ti.index = Integer.valueOf(line.split(";")[0]);
				ti.workload = line.split(";")[1].trim();
				inputListTask.add(ti);
			}
			execucoes = 10;
			benchmarkName = "image";
			sleep_time_replication = 1000;
			break;
			
		case 2:
			list = UtilsFunctions.readFile("benchmark/primos/input-primos.txt");
			
			line = "";
			int value = 0;
			Log.d(TAG, "File lines "+ Integer.toString(list.size()) +"...");
			for(int i = 1; i < list.size(); i++){
				line = list.get(i);
				TaskItem ti = new TaskItem();
				line = list.get(i);
				ti.index = Integer.valueOf(line.split(",")[0]);
				ti.workload = line.split(",")[1].trim();
				inputListTask.add(ti);
			}
			sleep_time_replication = 30000;
			execucoes = 10;
			benchmarkName = "prime";
			
			break;
		case 4:
			
			if (estress_test){
				list = UtilsFunctions.readFile("benchmark/linpack/input-linpack.txt");
				for(int i = 1; i < list.size(); i++){
					line = list.get(i);
					
					TaskItem ti = new TaskItem();
					ti.index = Integer.valueOf(line.split(",")[0]);;
					ti.workload = line.split(",")[1].trim();
					inputListTask.add(ti);
				}
			}
			else{
				
			}
			sleep_time_replication = 30000;
			constant_time = 1500;
			execucoes = 1;
			benchmarkName = "linpack";
			
			break;

		default:
			break;
		}
		
		runBenchmark2(execucoes, benchmarkName, inputList, inputListTask, 
				runLocal, runCloud, sleep_time_replication, constant_time);	
	}
	
	public void runBenchmark2(final int execucoes, final String benchmarkName, final ArrayList<Integer> inputList, final ArrayList<TaskItem> inputListTask,
			final boolean runLocal, final boolean runCloud, final int sleep_time_interaction, final int constant_time){
		Toast.makeText(getApplicationContext(), "Starting experiment ", Toast.LENGTH_LONG).show();
		
		long seed = System.nanoTime();
		
    	SystemClock.sleep(5000);
    	
		new Timer();
		Timer.reset();
		Timer.start();
		ArrayList<Integer> experiments = new ArrayList<Integer>(3);
		final ArrayList<String> array_result = new ArrayList<String>(10);
		final ArrayList<Thread> threads_queue = new ArrayList<Thread>(10);
		experiments.add(execucoes);
		
		int sizeInputList = inputList.size();
		if(!inputListTask.isEmpty())
			sizeInputList = inputListTask.size();
		
		String datetime = UtilsFunctions.getCurrentTime();
		
		String sufix = "_" + datetime + "_.txt";
		String enviromnent = runLocal ? "local" : "cloud"; 
		
	    final String tmpfileNameLog = fileNameLog + "_" + benchmarkName +"_" + 
	    							  enviromnent + "_" + (constant_time/1000.0) +"-segundos" + sufix;
	    
	    createFileResults(tmpfileNameLog, "Local | Cloud");
	    String header = "\nIndex, 	DateTime,	Comp-local,	Request,	Server,	Response,	Total";
	    UtilsFunctions.writeResults(tmpfileNameLog, header);
	    
	    for (int k = 0; k < experiments.size(); k++) {
			int replicacoes = experiments.get(k);
		    
		    UtilsFunctions.writeResults(tmpfileNameLog, "\n\nExperimentos: " + String.valueOf(experiments.get(k)));
		    
		    for (int j = 0; j < replicacoes; j++) {
				
				UtilsFunctions.writeResults(tmpfileNameLog, "\nCaso " + String.valueOf(j+1));
				
			    final Starter start = new Starter();
			    start.dataSaverInit();
			    
		    	final Queue<Thread> q = new LinkedList<Thread>();
			    for (int i = 0; i < sizeInputList; i++) {
			    	
			    	final boolean last = (i+1) == sizeInputList ? true : false;
		    		final TaskItem tk = inputListTask.get(i);
			    	final long timestart = System.currentTimeMillis();
			    	Thread th = new Thread(new Runnable() {
			            public void run() { 
			            	String id = String.valueOf(tk.getIndex());
			            	if (benchmarkName == "prime"){
					    		int input = Integer.valueOf(tk.getWorkload());
					    		start.primeCalc2(input, runLocal, runCloud);
					    	}
					    	else if(benchmarkName == "linpack"){
					    		int input = Integer.valueOf(tk.getWorkload());
					    		start.linpackCalc2(input, runLocal, runCloud);
					    	}
					    	else if(benchmarkName == "image"){
					    		String input, out; 
					    		String[] strArray = tk.getWorkload().split(",");
					    		input = strArray[0].trim();
					    		out = strArray[1].trim();
					    		start.imageBenchmark(input, out, runLocal, runCloud);
					    	}
			            	
					    	String result_line = "\n" + id  + ", " + UtilsFunctions.getCurrentTime() + 
					    						 ",	" + start.data.getPrimeCalcLocalResult() + 
					    						 ",	" + start.data.getLogPrimeCalcCloudResult() + 
					    						 ", " + (System.currentTimeMillis() - timestart) +
					    						 ", " + q.size();
					    	
					    	UtilsFunctions.writeResults(tmpfileNameLog, result_line);
					    	
					    	if(!last){
				            	try {
				            		synchronized (lock) {
				            			while (q.peek() == null) {
				            	            lock.wait();
				            	        }
										Thread t = q.remove();
								    	if (t != null){
								    		t.start();
								    	}
									}
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
					    	}else{
					    		String a = id;
					    	}
					    	
			            }
					});
			    	
			    	if (i == 0){
			    		th.start();
			    	}
			    	else{
	            		synchronized (lock) {
	            			q.add(th);
					    	lock.notify();
						}
			    	}
//			    	threads_queue.add(th);
			    	
			    	SystemClock.sleep(constant_time);
			    	
			    }
//			    threads_queue.
//			    for (int i = 0; i < threads_queue.size(); i++) {
//			    	i = 0;
//			    	SystemClock.sleep(constant_time);
//					Thread t = threads_queue.get(i);
//					t.start();
//			    	try {
//						t.join();
//						threads_queue.remove(i);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
			    
			    array_result.clear();
			    System.gc();
			    System.gc();
				SystemClock.sleep(sleep_time_interaction);
			}
		}
	    SystemClock.sleep(3000);
	    playSound2();
	    SystemClock.sleep(1500);
	    finishActivity();
		

	}
	
	public void runBenchmark(final int execucoes, final String benchmarkName, final ArrayList<Integer> inputList, final ArrayList<TaskItem> inputListTask,
			final boolean runLocal, final boolean runCloud, final int sleep_time_interaction){
		Toast.makeText(getApplicationContext(), "Starting experiment ", Toast.LENGTH_LONG).show();
		
		long seed = System.nanoTime();
		new Thread(new Runnable() {
            public void run() {
            	SystemClock.sleep(10000);
				new Timer();
				Timer.reset();
				Timer.start();
				ArrayList<Integer> experiments = new ArrayList<Integer>(3);
				ArrayList<String> array_result = new ArrayList<String>(10);
				experiments.add(execucoes);
				
				int sizeInputList = inputList.size();
				if(!inputListTask.isEmpty())
					sizeInputList = inputListTask.size();
				
				String datetime = UtilsFunctions.getCurrentTime();
				
				String sufix = "_"+datetime + "_.txt";
				String tmpfileNameLog = fileNameLog + sufix;
				
			    if(runLocal)
			    	tmpfileNameLog = fileNameLog + "_" + benchmarkName +"_local" + sufix;
			    if(runCloud)
			    	tmpfileNameLog = fileNameLog + "_" + benchmarkName +"_cloud" + sufix;
			    
			    createFileResults(tmpfileNameLog, "Local | Cloud");
			    array_result.add("\nIndex, 	DateTime,	Comp-local,	Request,	Server,	Response,	Total" );
			    for (int k = 0; k < experiments.size(); k++) {
					int interacaos = experiments.get(k);
					int time = experiments.get(k);
				    
				    array_result.add("\n\nExperimento: " + String.valueOf(experiments.get(k)));
					
				    for (int j = 0; j < interacaos; j++) {
						array_result.add("\nCaso " + String.valueOf(j));
						
					    Starter start = new Starter();
					    start.dataSaverInit();
					    
						for (int i = 0; i < sizeInputList; i++) {
							String id = String.valueOf(i).toString();
					    	if (benchmarkName == "prime"){
					    		id = String.valueOf(inputListTask.get(i).getIndex());
					    		int input = Integer.valueOf(inputListTask.get(i).getWorkload());
					    		start.primeCalc2(input, runLocal, runCloud);
					    	}
					    	else if(benchmarkName == "linpack"){
					    		id = String.valueOf(inputListTask.get(i).getIndex());
					    		int input = Integer.valueOf(inputListTask.get(i).getWorkload());
					    		start.linpackCalc2(input, runLocal, runCloud);
					    	}
					    	else if(benchmarkName == "image"){
					    		id = String.valueOf(inputListTask.get(i).getIndex());
					    		String input, out; 
					    		String[] strArray = inputListTask.get(i).getWorkload().split(",");
					    		input = strArray[0].trim();
					    		out = strArray[1].trim();
					    		start.imageBenchmark(input, out, runLocal, runCloud);
					    	}
					    	array_result.add("\n" + id  + ", " + UtilsFunctions.getCurrentTime() + ",	" + start.data.getPrimeCalcLocalResult() + 
					    					 ",	" + start.data.getLogPrimeCalcCloudResult());
					    	
					    	if(array_result.size() == 5){
					    		UtilsFunctions.writeResults(tmpfileNameLog, array_result);
					    		array_result.clear();
					    	}
					    }
					    UtilsFunctions.writeResults(tmpfileNameLog, array_result);
					    array_result.clear();
					    System.gc();
					    System.gc();
						SystemClock.sleep(sleep_time_interaction);
					}
				}
			    SystemClock.sleep(3000);
			    playSound2();
			    SystemClock.sleep(1500);
			    finishActivity();
            }
		}).start();
	}
	
	public void createFileResults(String fileName, String header) {
		try {
			FileWriter fileWritter = new FileWriter(fileName);
			
			BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
			bufferWritter.write(header);
			bufferWritter.close();
		}
		catch(Exception e) {
			Log.e("CloudBench1FILE", fileName + e.getMessage());
			Toast.makeText(getBaseContext(), e.getMessage(),
					Toast.LENGTH_LONG).show();
		}
	}
	
	
    public void run() {
    	Starter start = new Starter();
    	
		int experiment = spinner_benchmarks.getSelectedItemPosition();
		
    	runExperiment(experiment, runLocal, runCloud);
    	
    	return;
    	
//		handler.sendEmptyMessage(16);
//    	start.dataSaverInit();
//    	start.savePhoneInfo(this.phoneModel, this.fingerPrint, this.phoneSDK, this.connectionInfo);
//        
//		start.downloadUploadSpeed(this.filen, this.downloadTempFile);
//		handler.sendEmptyMessage(32);
//        
//		// this.primeCalcTest = start.primeCalc();
//		start.primeCalc();
//		handler.sendEmptyMessage(48);
//    	
//		// this.listSorterTest = start.listSorter(this.wordList, this.textString);
//		start.listSorter(this.wordList, this.textString);
//		handler.sendEmptyMessage(66);
//
//		// this.imageTransformTest = start.imageTran(this.is, this.filen, this.tempSaveImage1, this.tempSaveImage2);
//		start.imageTran(this.is, this.filen, this.tempSaveImage1, this.tempSaveImage2);
//		handler.sendEmptyMessage(84);
//
//		start.dataSaverFinnish();
//		handler.sendEmptyMessage(100);
//    	
//		handler.sendEmptyMessage(101);
    }
    
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
	            case 16:
	                status.setText(R.string.start);  
	            	progressBar.setProgress(16);
	                break;
            
	            case 32:
	                status.setText(R.string.test1);  
	            	progressBar.setProgress(32);
	                break;     
	                
	            case 48:
	                status.setText(R.string.test2);  
	            	progressBar.setProgress(48);
	                break; 	 
	                
	            case 66:
	                status.setText(R.string.test3);  
	            	progressBar.setProgress(66);
	                break;     
	                
	            case 84:
	                status.setText(R.string.test4);  
	            	progressBar.setProgress(84);
	                break; 	                
	                
                case 100:
                    status.setText(R.string.send); 
	            	progressBar.setProgress(100);
                    break;
                    
                case 101:
                	firstButton.setVisibility(View.VISIBLE);
                	forthButton.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                    creator.setVisibility(View.VISIBLE);
                    progressBar.setProgress(0);
                
                    try {
                    	Thread.sleep(2000);
                    } catch (InterruptedException e) {
                    	e.printStackTrace();
                    }

                    status.setVisibility(View.INVISIBLE);
                    break;
                }
            }
    	};
	
	public void startServiceThread(){
		
    		threadMetricCollector = new Thread(new Runnable(){
			Boolean running = true;
            public void run() {
            	String fileName = "/sdcard/test_thread_outlogcloudbench.txt";
				createFileResults(fileName, "time, Battery");
                while(running){
                	try {
						Thread.sleep(1000);
						float level = getBatteryLevel();
						ArrayList<String> results = new ArrayList<String>();
						results.add("\n" + String.valueOf(level));
						UtilsFunctions.writeResults(fileName, results);
                	} 
                	catch (InterruptedException e) {
                		e.printStackTrace();
                	}
                	
                	if (Thread.currentThread().isInterrupted()) {
                		running = false;
                	}
                }
	        }
        });
//		threadMetricCollector.start();
	}
    	
    	// Capiturar nivel de baterria sem monitora-la
	public float getBatteryLevel() {
	    Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
	    int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
	    int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
	    int voltage = batteryIntent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);

	    // Error checking that probably isn't needed but I added just in case.
	    if(level == -1 || scale == -1) {
	        return 50.0f;
	    }
	    Log.i("Battery", "Baterry scale: " + String.valueOf(scale) + " Level:" + String.valueOf(level));
	    //return ((float)level / (float)scal	e) * 100.0f; 
	    return (float)voltage;
	}
	
    public void getBatteryCapacity() {
        Object mPowerProfile_ = null;

        final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";

        try {
            mPowerProfile_ = Class.forName(POWER_PROFILE_CLASS)
                    .getConstructor(Context.class).newInstance(this);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("Battery", e.getMessage() );
        } 

        try {
            double batteryCapacity = (Double) Class
                    .forName(POWER_PROFILE_CLASS)
                    .getMethod("getAveragePower", java.lang.String.class)
                    .invoke(mPowerProfile_, "battery.capacity");
            Toast.makeText(this, batteryCapacity + " mah",
                    Toast.LENGTH_LONG).show();
            Log.i("Battery", String.valueOf(batteryCapacity));
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("Battery", e.getMessage());
        } 
    }
    
    public final void playSound(){
    	Uri defaultRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

    	MediaPlayer mediaPlayer = new MediaPlayer();

    	try {
    	      mediaPlayer.setDataSource(getApplicationContext(), defaultRingtoneUri);
    	      mediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
    	      mediaPlayer.prepare();
    	      mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

    	         @Override
    	         public void onCompletion(MediaPlayer mp)
    	         {
    	            mp.release();
    	         }
    	      });
    	      
    	  mediaPlayer.start();
    	  
    	} catch (IllegalArgumentException e) {
    	 e.printStackTrace();
    	} catch (SecurityException e) {
    	 e.printStackTrace();
    	} catch (IllegalStateException e) {
    	 e.printStackTrace();
    	} catch (IOException e) {
    	 e.printStackTrace();
    	}
    }
    
    public final void playSound2(){
    	Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

    	if(alert == null){
    	    // alert is null, using backup
    	    alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

    	    // I can't see this ever being null (as always have a default notification)
    	    // but just incase
    	    if(alert == null) {  
    	        // alert backup is null, using 2nd backup
    	        alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);                
    	    }
    	}
    	Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), alert);
    	r.play();
    }
	
}