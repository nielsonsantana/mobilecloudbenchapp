package com.cloudbench;



import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TooManyListenersException;

import com.linpack.LinpackLocal;
import com.starter.Starter;
import com.timer.Timer;
import com.utils.UtilsFunctions;

//import net.jaqpot.netcounter.HandlerContainer;
//import net.jaqpot.netcounter.NetCounterApplication;
//import net.jaqpot.netcounter.activity.ListAdapter;
//import net.jaqpot.netcounter.model.Counter;
//import net.jaqpot.netcounter.model.NetCounterModel;
//import net.jaqpot.netcounter.service.NetCounterAlarm;
//import net.jaqpot.netcounter.service.OnAlarmReceiver;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.widget.Toast;

public class ServiceCloudBench extends Service  {
//public class MyService extends Service {
	private static String TAG = "MyService";
	public String LOG_MONITOR_BATERIA_TAG = "LOG_BATTERIA";
    public String log_monitor_bateria = "";
	private String logFileName = "/sdcard/outLogCollectMetrics.txt";
	
	private static final int NOTIFYID = 20100811;
//	private IpcService ipcService = null;
	
	public String BENCHMARK = "BENCHMARK";
	
	@Override
	public void onCreate() {
		Toast.makeText(this, "Congrats! MyService Created", Toast.LENGTH_SHORT).show();
		Log.d(TAG, "onCreateService");
//		startBenchLinpackLocal();
	}
	
	public void startBenchLinpackLocal(){
		for (int i = 0; i < 1000; i++) {
			new LinpackLocal().runLinpack(1);
		}
	}
	
//	public void startBenchmark(){
////		new Thread(new Runnable() {
////            public void run() {
//				new Timer();
//				Timer.reset();
//				Timer.start();
//				ArrayList<Integer> timeExperiments = new ArrayList<Integer>(3);
//				ArrayList<String> array_result = new ArrayList<String>(10);
//				timeExperiments.add(1);
//				timeExperiments.add(2);
//				timeExperiments.add(3);
//				String tmpfileNameLog = fileNameLog + ".txt";
//				
//			    if(runLocal)
//			    	tmpfileNameLog = fileNameLog + "_local.txt";
//			    if(runCloud)
//			    	tmpfileNameLog = fileNameLog + "_cloud.txt";
//			    
//			    createFileResults(tmpfileNameLog, "Local | Cloud");
//			    array_result.add("\nIndex, Comp-local | Request, Server, Response, Total" );
//			    for (int k = 0; k < timeExperiments.size(); k++) {
//					int interacaos = timeExperiments.get(k);
//					int time = timeExperiments.get(k);
//					
//					Toast.makeText(getApplicationContext(), "Starting experiment time: " + String.valueOf(time), Toast.LENGTH_SHORT).show();
//
//					Toast.makeText(getApplicationContext(), "Starting experiment ", Toast.LENGTH_LONG).show();
//					    
//					ArrayList<Integer> arrlist = new ArrayList<Integer>(7);
//				    arrlist.add(100);
//				    arrlist.add(1000);
//				    arrlist.add(10000);
//				    arrlist.add(100000);
//				    arrlist.add(1000000);
//				    
//				    array_result.add("\n\nExperimento: " + String.valueOf(timeExperiments.get(k)));
//					
////					Log.d("log_cloud", String.valueOf(arrlist.size()));
//					for (int i = 0; i < arrlist.size(); i++) {
//						array_result.add("\nCaso " + String.valueOf(i));
//						
//					    Starter start = new Starter();
//					    start.setSntpClient(sntpclient);
//					    start.dataSaverInit();
//					    
////						array_result.add("\n" + String.valueOf(time) + ", " + String.valueOf(Timer.result()/100.0));
//						
//					    for (int j = 0; j < interacaos; j++) {
//					    	start.primeCalc2(arrlist.get(i), runLocal, runCloud);
//					    	array_result.add("\n" + String.valueOf(j).toString() + ",	" + start.data.getPrimeCalcLocalResult() + 
//					    					 ",	" + start.data.getLogPrimeCalcCloudResult());
//					    	
//					    	if(array_result.size() == 5){
//					    		UtilsFunctions.writeResults(tmpfileNameLog, array_result);
//					    		array_result.clear();
//					    	}
//					    }
//					    Toast.makeText(getApplicationContext(), "Finished " + String.valueOf(i), Toast.LENGTH_SHORT).show();
//					    UtilsFunctions.writeResults(tmpfileNameLog, array_result);
//					    array_result.clear();
//					}
//					Toast.makeText(getApplicationContext(), "Finishing:" + String.valueOf(time) + ", " + String.valueOf(Timer.result()/100.0), Toast.LENGTH_LONG).show();
//				}
//			    
//			    Toast.makeText(getApplicationContext(), "EXPERIMENT FINISHED", Toast.LENGTH_LONG).show();
//			    
////            }
////		}).start();
//	}
	
	private final BroadcastReceiver mWifiReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Toast.makeText(getApplicationContext(), "Receing network traffic", Toast.LENGTH_SHORT).show();
		}
	};
	
	public class PhoneListen extends PhoneStateListener {
		  private Context context;    
		  public PhoneListen(Context c) {
		     context=c;
		  }    
		  @Override
		  public void onDataConnectionStateChanged(int state) {
		    switch(state) {
		      case TelephonyManager.DATA_DISCONNECTED:// 3G
		        //3G has been turned OFF
		      break;
		      case TelephonyManager.DATA_CONNECTING:// 3G
		        //3G is connecting
		      break;
		      case TelephonyManager.DATA_CONNECTED:// 3G
		        //3G has turned ON
		      break;
		    }
		  }
		}

	public void setLogNameMonitorBateria(String logName) {
		this.log_monitor_bateria = logName;
	}
	
	public BroadcastReceiver monitorBateria = new BroadcastReceiver(){
        @Override
        public void onReceive(Context arg0, Intent intent) {

            Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            int second = c.get(Calendar.SECOND);
            int milisecond = c.get(Calendar.MILLISECOND);
            String time_consumed = new String (hour+":"+minute+":"+second+":"+milisecond);

            String  technology= intent.getExtras().getString(BatteryManager.EXTRA_TECHNOLOGY);
            int  temperature= intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,0);
            int  voltage= intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE,0);
            int  levelInfo= intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);
            int  plugged= intent.getIntExtra(BatteryManager.EXTRA_PLUGGED,0);
            int level = intent.getIntExtra("level", 0);

            Log.i("Script", "Bateria:" + level + "%");
            Log.i("Script/Time:", time_consumed );
            
            if( log_monitor_bateria == "" ){
            	Log.w("Script", "Arquivo de Log da Baterria não configurado.");
            	// Show Mensagem de arquivo de log não configurado;
            	return;            	
            }
            
            //UtilFunctions.writeResults(log_monitor_bateria, "\n" + time_consumed + ", " + String.valueOf(level));

        }

    };
	
    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
                        boolean isFailover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);
            
            NetworkInfo currentNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            NetworkInfo otherNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);
            
            currentNetworkInfo.getDetailedState();
                        
            // do application-specific task(s) based on the current network state, such 
            // as enabling queuing of HTTP requests when currentNetworkInfo is connected etc.
            
            TrafficStats.getMobileRxBytes();
            TrafficStats.getMobileTxBytes();
//            TrafficStats.get
            
//            mApp = (NetCounterApplication) getApplication();
//    		mModel = mApp.getAdapter(NetCounterModel.class);
//    		mWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
//    		mAlarm = new NetCounterAlarm(this, OnAlarmReceiver.class);

    		IntentFilter f = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
    		registerReceiver(mWifiReceiver, f);
            
        }
    };
    
 // Capiturar nivel de baterria sem monitora-la
	public float getBatteryLevel() {
	    Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
	    int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
	    int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

	    // Error checking that probably isn't needed but I added just in case.
	    if(level == -1 || scale == -1) {
	        return 50.0f;
	    }
	    Log.i("Battery", "Baterry scale: " + String.valueOf(scale) + " Level:" + String.valueOf(level));
	    return ((float)level / (float)scale) * 100.0f; 
	}
	

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show(); 
		Log.d(TAG, "onStart");
		String logBatteria = intent.getStringExtra(this.LOG_MONITOR_BATERIA_TAG);
		this.setLogNameMonitorBateria(logBatteria);
//		this.registerReceiver(monitorBateria, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		
		//intent.getExtras().get(BENCHMARK);

	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "MyService Stopped", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onDestroy");
	}


}
