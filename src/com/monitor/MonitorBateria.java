package com.monitor;

import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.util.Log;

public class MonitorBateria {
	
	private BroadcastReceiver monitorBateria = new BroadcastReceiver(){
        @Override
        public void onReceive(Context arg0, Intent intent) {

            //Variáves
            Calendar c = Calendar.getInstance();
            int hora = c.get(Calendar.HOUR_OF_DAY);
            int minuto = c.get(Calendar.MINUTE);
            int segundo = c.get(Calendar.SECOND);
            int milisegundo = c.get(Calendar.MILLISECOND);
            String time_consulta= new String (hora+":"+minuto+":"+segundo+":"+milisegundo);

            String  technology= intent.getExtras().getString(BatteryManager.EXTRA_TECHNOLOGY);
            int  temperature= intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,0);
            int  voltage= intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE,0);
            int  levelInfo= intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);
            int  plugged= intent.getIntExtra(BatteryManager.EXTRA_PLUGGED,0);

            int level = intent.getIntExtra("level", 0);//variável que vai receber o valor em porcentagem

            Log.w("Script", "Bateria:" + level + "%");//Parte do código que informa o valor da porcentagem via log
            Log.w("Script/Time:", time_consulta );
            
            

            //DBAdapter db = new DBAdapter(getApplicationContext());

//            db.open(); //Abrindo o Banco
//            StringBuilder sb = new StringBuilder();
//
//
//
//            db.InserirDados(String.valueOf(level)); //Inserindo os dados da bateria no banco
//            db.consultarTodosDados();//consultando os dados inseridos
//
//            db.close();
            
            

//            contentTVBateria.setText(
//                    "A Bateria está com: " + String.valueOf(level) + "% de carga\n"+
//                            "Technology: "+technology+"\n"+
//                            "Temperature: "+temperature+"\n"+
//                            "Nível: "+levelInfo+"%\n"+
//                            "Plugged: "+plugged+"\n"+
//                            "Voltage: "+voltage+"\n"); // Construção dos dados que será apresentado na tela


        }


    };
}
