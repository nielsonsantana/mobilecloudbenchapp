package com.cloudbench;



import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TooManyListenersException;

import com.linpack.LinpackLocal;
import com.starter.Starter;
import com.timer.Timer;
import com.utils.Utils;

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
	private static String TAG = "MyService";
	public String LOG_MONITOR_BATERIA_TAG = "LOG_BATTERIA";
    public String log_monitor_bateria = "";
	private String logFileName = "/sdcard/outLogCollectMetrics.txt";
	
	private static final int NOTIFYID = 20100811;
	
	public String BENCHMARK = "BENCHMARK";
	
	@Override
	public void onCreate() {
		Toast.makeText(this, "Congrats! MyService Created", Toast.LENGTH_SHORT).show();
		Log.d(TAG, "onCreateService");
	}
	
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
    
 // Capiturar nivel de baterria sem monitora-la
// 	public float getBatteryLevel() {
// 	    Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
// 	    int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
// 	    int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
// 	    int voltage = batteryIntent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
//
// 	    // Error checking that probably isn't needed but I added just in case.
// 	    if(level == -1 || scale == -1) {
// 	        return 50.0f;
// 	    }
// 	    Log.i("Battery", "Baterry scale: " + String.valueOf(scale) + " Level:" + String.valueOf(level));
// 	    //return ((float)level / (float)scal	e) * 100.0f; 
// 	    return (float)voltage;
// 	}
 	
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
