package com.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.os.Environment;
import android.util.Log;

public class UtilsFunctions {
	
	static String TAG = "cloudbench";
	
	
	public static void writeResults(String fileName, ArrayList<String> data) {
		try {
			FileWriter fileWritter = new FileWriter(fileName, true);
			
			BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
	        
			for (int i = 0; i < data.size(); i++) {
				bufferWritter.write(data.get(i));
			}
			bufferWritter.close();
//			Toast.makeText(getBaseContext(),
//					"Done writing SD 'mysdfile.txt'",
//					Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Log.e("Exception", e.getMessage());
//			Toast.makeText(getBaseContext(), e.getMessage(),
//					Toast.LENGTH_LONG).show();
		}
	}
	
	public static String getCurrentTime(){
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss.SSS");
		String sdt = df.format(new Date(System.currentTimeMillis()));
		
		return sdt;
	}
	
	public static ArrayList<String> readFile(String filename){
	    //Find the directory for the SD Card using the API
		//*Don't* hardcode "/sdcard"
		Log.d(TAG, "Reading file "+ filename +"...");
		File sdcard = Environment.getExternalStorageDirectory();

		//Get the text file
		File file = new File(sdcard,filename);

		//Read text from file
		StringBuilder text = new StringBuilder();

		try {
		    BufferedReader br = new BufferedReader(new FileReader(file));
		    String line;

		    while ((line = br.readLine()) != null) {
		        text.append(line);
		        text.append('\n');
		    }
		    br.close();
		}
		catch (IOException e) {
		    //You'll need to add proper error handling here
		}

		//Find the view by its id
		return new ArrayList<String>();
	}
	
}
