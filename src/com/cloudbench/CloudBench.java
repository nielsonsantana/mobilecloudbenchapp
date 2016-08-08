package com.cloudbench;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.BatteryManager;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.util.*;

import com.ntp.NtpTrustedTime;
import com.ntp.SntpClient;
import com.starter.Starter;
import com.timer.Timer;
import com.utils.Utils;
//import android.os.ServiceManager;

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
	String TAG_THREAD = "thread-log";
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
    
    private String fileNameLog = "/sdcard/benchmark/outlogcloudbench";
    private Thread threadMetricCollector;
    
    final com.ntp.SntpClient sntpclient = new com.ntp.SntpClient(); ;
    
    private int experiment = 0;
    private boolean runLocal = false;
    private boolean runCloud = false;
    
    Object lock = new Object();
    Object lock_start_new = new Object();
    Object lockLog = new Object();
    
	final Queue<Thread> queue = new LinkedList<Thread>();
	final Queue<String> queuelog = new LinkedList<String>();
    

    @Override
    public void onCreate(Bundle savedInstanceState) {  	        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        firstButton = findViewById(R.id.button1);
        firstButton.setOnClickListener(this);
       
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        status = (TextView) findViewById(R.id.status);  
        creator = (TextView) findViewById(R.id.textView1);        
        forthButton = findViewById(R.id.button4);
        forthButton.setOnClickListener(this);

        firstButton.setVisibility(View.INVISIBLE);
        forthButton.setVisibility(View.INVISIBLE);
        creator.setVisibility(View.INVISIBLE);
        
        bt_local = (Button)findViewById(R.id.bt_run_local);
        bt_local.setOnClickListener(onClickRunTaskLocal);
        
        bt_cloud = (Button)findViewById(R.id.bt_run_cloud);
        bt_cloud.setOnClickListener(onClickRunTaskCloud);
        
        spinner_benchmarks = (Spinner)findViewById(R.id.spinner1);
    }
    
    public void run() {    	
		int experiment = spinner_benchmarks.getSelectedItemPosition();
		
    	setupExperiment(experiment, runLocal, runCloud);
    	
    	return;
    }
    
    public boolean syncSntp(){
    	  if (sntpclient.requestTime("ntp.ubuntu.com",10000)) {
    		  Toast.makeText(getApplicationContext(), String.valueOf(sntpclient.getNtpTime()), Toast.LENGTH_LONG).show();
    		  return true;
    	  }
    	  Toast.makeText(getApplicationContext(), "Not syncronized", Toast.LENGTH_LONG).show();
    	  return false;
    }
		
	public void setupExperiment(final int experiment, final boolean runLocal, final boolean runCloud){
		
		Toast.makeText(getApplicationContext(), "Starting experiment ", Toast.LENGTH_LONG).show();
				
		new Thread(new Runnable() {
			@Override
			public void run() {
				ArrayList<Integer> inputList = new ArrayList<Integer>();
				ArrayList<TaskItem> inputListTask = new ArrayList<TaskItem>();
				
				int replications = 0;
				String benchmarkName = "";
				int sleepTimeReplication = 0;
				Log.i("cloudbench1", "runLocal:"+ Boolean.toString(runLocal) +" runCloud:" + Boolean.toString(runCloud));
				ArrayList<String> inputLines = null;
				String line = "";
				
				switch (experiment) {
				case 0:
					inputLines = Utils.readFile("benchmark/image/input.txt");
//					for(int i = 3; i < inputLines.size(); i++){
//						TaskItem ti = new TaskItem();
//						line = inputLines.get(i);
//						ti.index = Integer.valueOf(line.split(";")[0]);
//						ti.workload = line.split(";")[1].trim();
//						inputListTask.add(ti);
//					}
					replications = 10;
					benchmarkName = "image";
					sleepTimeReplication = 1000;
					break;
					
				case 1:
					inputLines = Utils.readFile("benchmark/primos/input-primos.txt");
					
					line = "";
//					Log.d(TAG, "File lines "+ Integer.toString(inputLines.size()) +"...");
//					for(int i = 3; i < inputLines.size(); i++){
//						line = inputLines.get(i);
//						TaskItem ti = new TaskItem();
//						line = inputLines.get(i);
//						ti.index = Integer.valueOf(line.split(",")[0]);
//						ti.workload = line.split(",")[1].trim();
//						inputListTask.add(ti);
//					}
					sleepTimeReplication = 15000;
					replications = 10;
					benchmarkName = "prime";
					
					break;
				case 2:
					sleepTimeReplication = 15000;
					replications = 1;
					benchmarkName = "linpack";
					
					inputLines = Utils.readFile("benchmark/linpack/input-linpack.txt");
		
					break;
		
				default:
					break;
				}
				String tmpfileNameLog = "";
				int j = 0;
				for (;j < inputLines.size(); j++) {
					String workloadTimes = inputLines.get(j).split(":")[1].trim();
					replications = Integer.valueOf(inputLines.get(j+1).split(":")[1].trim());
					
					String datetime = Utils.getCurrentTime();
					String sufix = "_" + datetime + "_.txt";
					String enviromnent = runLocal ? "local" : "cloud"; 
					String timeWorkload = workloadTimes.replace(";", "-").replace(" ", "")+"-segundos";
				    
					if(j == 0)
					{
						tmpfileNameLog = fileNameLog + "_" + benchmarkName +"_" + enviromnent + "_" + timeWorkload + sufix;
					    String header = "index, dateTime, local, request, server, response, total-response-server, " +
					    		"total-response , queue-size, queue-time, replication, workloadTime, workload";
					    Utils.createFileResults(tmpfileNameLog, header);

					}
					inputListTask.clear();
					for(int i = j+3; i < inputLines.size(); i++){
						line = inputLines.get(i);
						if(line.equalsIgnoreCase("<stop>") || line == null){
							j = i;
							break;
						}
						
						TaskItem ti = new TaskItem();
						ti.index = Integer.valueOf(line.split(",")[0]);;
						ti.workload = line.split(",")[1].trim();
						inputListTask.add(ti);
						
					}
					sleepTimeReplication = 3000;
					Thread t = runBenchmark2(replications, benchmarkName, inputList, inputListTask, 
							runLocal, runCloud, sleepTimeReplication, workloadTimes, tmpfileNameLog);
					
					try {
						t.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				finishActivity();
			}
		}).start();
		
		/*Input format
	 	time_carga: 0.5; 1;
		replication: 15
		indice, carga_trabalho
		1,25
		2,50*/
	}
	
	class TaskRunner extends Thread{
		@Override
		public void run() {
        	try {
        		boolean loop = true;
    			while(loop){
    				synchronized (lock) {
            			while (queue.peek() == null) {
            				Log.d(TAG_THREAD, "lock wating");
            	            lock.wait();
            				Log.d(TAG_THREAD, "lock released");
            	        }
    				}
    				
					Thread t = queue.remove();
					t.start();
		    		if(t.getName() == "finish"){
			    		loop = false;
			    	}
					Log.d(TAG, "Executando thread: " + t.getName());
					
						// Don't need wait the task store data or do anything else.
			    	synchronized (lock_start_new) {
			    		Log.d("lock_start", "lock_start waiting");
			    		lock_start_new.wait();
			    		Log.d("lock_start", "lock_start released");
					}
				}
			}catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	class LogWriter extends Thread{
		String outputLogName = "";
		
		public String getOutputLogName() {
			return outputLogName;
		}

		public void setOutputLogName(String outputLogName) {
			this.outputLogName = outputLogName;
		}

		@Override
		public void run() {
        	try {
        		String text = "";
        		boolean loop = true;
    			while(loop){
    				synchronized (lockLog) {
            			while (queuelog.peek() == null) {
            				lockLog.wait();
            	        }
        				text = queuelog.remove();
    				}
    				
    				Utils.writeResults(outputLogName, text);

				}
			}catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public Thread runBenchmark2(final int replications, final String benchmarkName, final ArrayList<Integer> inputList, final ArrayList<TaskItem> inputListTask,
			final boolean runLocal, final boolean runCloud, final int sleepTimeReplication, final String workloadTimes, final String fileNameOutput){
		
		Thread t = new Thread(new Runnable() {
            public void run() {
            SystemClock.sleep(5000);
			
			ArrayList<Integer> workloadTimeList = new ArrayList<Integer>(3);
			String [] splitWorkload = workloadTimes.split(";");
			for (String value : splitWorkload) {
				workloadTimeList.add((int)(Float.valueOf(value.trim()) * 1000));
			}
			int sizeInputList = inputListTask.size();
			
		    final Starter start = new Starter();
		    start.dataSaverInit();
		    
		    LogWriter logWriter = new LogWriter();
		    logWriter.setOutputLogName(fileNameOutput);
		    logWriter.start();
			
			new Timer();
			Timer.reset();
			Timer.start();
		    
			Utils.writeResults(fileNameOutput, "\nWorkloadTimes: " + workloadTimes);
			Utils.writeResults(fileNameOutput, "\nReplications: " + replications);
			
		    for (int t = 0; t < workloadTimeList.size(); t++) {
		    	int currentTimeMilleseconds =  workloadTimeList.get(t);
		    	final double currentTimeSeconds =  (workloadTimeList.get(t)/1000.);
		    	
		    	Log.d(TAG, "currentTimeSeconds: " + currentTimeSeconds +", "+ currentTimeMilleseconds);
					
			    TaskRunner taskRunner = new TaskRunner();
			    taskRunner.start();
				
			    for (int j = 0; j < replications; j++) {
					
			    	final String currentReplication = String.valueOf(j+1);
			    	synchronized (lockLog) {
				    	queuelog.add("\nReplication: " + String.valueOf(j+1)+
								" time:" + currentTimeSeconds);
				    	lockLog.notify();
					}
			    	
					Log.d(TAG, "Replication: " + currentReplication);
				    
			    	Thread th = null;
				    for (int i = 0; i < sizeInputList; i++) {
				    	
				    	final boolean first = i == 0 ? true : false;
				    	boolean last = (i+1) == sizeInputList ? true : false;
			    		final TaskItem tk = inputListTask.get(i);
				    	final long timestart = System.currentTimeMillis();
				    	
				    	th = new Thread(new Runnable() {
				            public void run() {
				            	
				            	double timeQueue = (System.currentTimeMillis() - timestart)/1000.;
				            	String workload = null;
				            	String id = String.valueOf(tk.getIndex());
				            	if (benchmarkName == "prime"){
						    		int input = Integer.valueOf(tk.getWorkload());
						    		start.primeCalc2(input, runLocal, runCloud);
						    		workload = String.valueOf(input);
						    	}
						    	else if(benchmarkName == "linpack"){
						    		int input = Integer.valueOf(tk.getWorkload());
						    		start.linpackCalc(input, runLocal, runCloud);
						    		workload = String.valueOf(input);
						    	}
						    	else if(benchmarkName == "image"){
						    		String input, out; 
						    		String[] strArray = tk.getWorkload().split(",");
						    		input = strArray[0].trim();
						    		out = strArray[1].trim();
						    		start.imageBenchmark(input, out, runLocal, runCloud);
						    	}
				            	
				            	// Start a new task
								// Don't need wait the task store data or do anything else.
				            	synchronized (lock_start_new) {
				            		lock_start_new.notify();
						    		Log.d("lock_start", "lock_start notify");
								}
				            	
				            	synchronized (lockLog) {
					            	String timeResponse = "0";
					            	
					            	if (first)
					            		timeQueue = 0;
				            		
					            	if(runLocal){
				            			timeResponse = (Double.valueOf(start.data.getLogLocalResult()) 
				            					+ timeQueue)+"";
					            	}
				            		else if(runCloud){
				            			timeResponse = (Double.valueOf(start.data.getTotalTimeCloudResult())/1000.
				            					+ timeQueue)+"";
				            		}
							    	
					            	String result_line = "\n" + id  + ", " + Utils.getCurrentTime() + 
							    						 ", " + start.data.getLogLocalResult() + 
							    						 ", " + start.data.getLogCloudResult() + 
							    						 ", " + timeResponse +
							    						 ", " + queue.size() +
					            						 ", " + timeQueue +
					            						 ", " + currentReplication +
					            						 ", " + currentTimeSeconds +
					            						 ", " + workload;

							    	queuelog.add(result_line);
						    		lockLog.notify();
								}
				            }
						});
				    	
				    	th.setName("thread-" + i);
				    	if(last)
				    		th.setName("finish");
	            		
			    		synchronized (lock) {
	            			queue.add(th);
					    	lock.notify();
				    		Log.d("lock_start", "lock notify");
						}
			    		
				    	Log.d(TAG, "Executando tarefa: " + i);			    	
				    	
				    	SystemClock.sleep(currentTimeMilleseconds);
				    }
//				     Waiting all task get done.
				    try {
		    			taskRunner.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				    
					System.gc();
					SystemClock.sleep(sleepTimeReplication);
				}
	            }
			    SystemClock.sleep(3000);
			    playSound2();
			    SystemClock.sleep(2500);
            }
    	});
		
		t.start();
		return t;

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
				
				String datetime = Utils.getCurrentTime();
				
				String sufix = "_"+datetime + "_.txt";
				String tmpfileNameLog = fileNameLog + sufix;
				
			    if(runLocal)
			    	tmpfileNameLog = fileNameLog + "_" + benchmarkName +"_local" + sufix;
			    if(runCloud)
			    	tmpfileNameLog = fileNameLog + "_" + benchmarkName +"_cloud" + sufix;
			    
			    Utils.createFileResults(tmpfileNameLog, "Local | Cloud");
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
					    		start.linpackCalc(input, runLocal, runCloud);
					    	}
					    	else if(benchmarkName == "image"){
					    		id = String.valueOf(inputListTask.get(i).getIndex());
					    		String input, out; 
					    		String[] strArray = inputListTask.get(i).getWorkload().split(",");
					    		input = strArray[0].trim();
					    		out = strArray[1].trim();
					    		start.imageBenchmark(input, out, runLocal, runCloud);
					    	}
					    	array_result.add("\n" + id  + ", " + Utils.getCurrentTime() + ",	" + start.data.getPrimeCalcLocalResult() + 
					    					 ",	" + start.data.getLogPrimeCalcCloudResult());
					    	
					    	if(array_result.size() == 5){
					    		Utils.writeResults(tmpfileNameLog, array_result);
					    		array_result.clear();
					    	}
					    }
					    Utils.writeResults(tmpfileNameLog, array_result);
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
	
	public void startServiceThread(){
		
    		threadMetricCollector = new Thread(new Runnable(){
			Boolean running = true;
            public void run() {
            	String fileName = Environment.getExternalStorageDirectory().getPath() +
            			"test_thread_outlogcloudbench.txt";
            	Utils.createFileResults(fileName, "time, Battery");
                while(running){
                	try {
						Thread.sleep(1000);
						float level = new ServiceCloudBench().getBatteryLevel();
						ArrayList<String> results = new ArrayList<String>();
						results.add("\n" + String.valueOf(level));
						Utils.writeResults(fileName, results);
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
    
	public final void finishActivity(){
		android.os.Process.killProcess(android.os.Process.myPid());
	}
	
	OnClickListener onClickRunTaskLocal = new OnClickListener(){

		@Override
		public void onClick(View v) {
			Toast.makeText(getApplicationContext(), "Runnning local", Toast.LENGTH_SHORT).show();
			
			int position = spinner_benchmarks.getSelectedItemPosition();
			experiment = position;
			runLocal = true;
			runCloud = false;
			setupExperiment(position, true, false);
		}
	};
	
	OnClickListener onClickRunTaskCloud = new OnClickListener(){

		@Override
		public void onClick(View v) {
			
			int position = spinner_benchmarks.getSelectedItemPosition();
			experiment = position;
			runLocal = false;
			runCloud = true;
			setupExperiment(position, false, true);
			
		}
	};
	
	// END by Nielson
    
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
//        case R.id.button1:
//        	
//			firstButton.setVisibility(View.INVISIBLE);
//			forthButton.setVisibility(View.INVISIBLE);
//			creator.setVisibility(View.INVISIBLE);
//			status.setText(R.string.start);
//			progressBar.setVisibility(ProgressBar.VISIBLE);
//			progressBar.setProgress(0);
//			status.setVisibility(View.VISIBLE);
//			
//			new Thread(this).start();
//
//        break;

        
//        case R.id.button4:
//        	this.finish();
//    	break;        
        }
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
}