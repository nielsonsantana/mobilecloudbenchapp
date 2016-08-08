package com.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class Utils {
	
	static String TAG = "com.cloudbench";
	
	public static void createFileResults(String fileName, String header) {
		try {
			File resultFile = new File(fileName);
			resultFile.createNewFile();
			
			FileOutputStream out = new FileOutputStream(resultFile);
			OutputStreamWriter osw = new OutputStreamWriter(out);
			BufferedWriter bufferWritter = new BufferedWriter(osw);
			bufferWritter.write(header);
			bufferWritter.flush();
			bufferWritter.close();
		}
		catch(Exception e) {
			Log.e(TAG, fileName + e.getMessage());
		}
	}
	
	public static void writeResults(String fileName, ArrayList<String> data) {
		try {
			FileWriter fileWritter = new FileWriter(fileName, true);
			
			BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
	        
			for (int i = 0; i < data.size(); i++) {
				bufferWritter.write(data.get(i));
			}
			bufferWritter.flush();
			bufferWritter.close();

		} catch (Exception e) {
			Log.e("Exception", e.getMessage());
		}
	}
	
	public static void writeResults(String fileName, String singleLine) {
		try {
			FileWriter fileWritter = new FileWriter(fileName, true);
			
			BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
			bufferWritter.write(singleLine);
			bufferWritter.flush();
			bufferWritter.close();
		} catch (Exception e) {
			Log.e("Exception", e.getMessage());
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
		ArrayList<String> list = new ArrayList<String>();

		//Get the text file
		File file = new File(sdcard,filename);

		//Read text from file
		StringBuilder text = new StringBuilder();

		try {
		    BufferedReader br = new BufferedReader(new FileReader(file));
		    String line;

		    while ((line = br.readLine()) != null) {
		    	line = line.replace("\n", "");
		    	list.add(line);
		    }
		    br.close();
		}
		catch (IOException e) {
		    //You'll need to add proper error handling here
		}

		//Find the view by its id
		return list;
	}
	
	public static File getFile(String filename){
	    //Find the directory for the SD Card using the API
		//*Don't* hardcode "/sdcard"
		Log.d(TAG, "Reading file "+ filename +"...");
		File sdcard = Environment.getExternalStorageDirectory();

		//Get the text file
		File file = new File(sdcard,filename);

		//Find the view by its id
		return file;
	}
	
	public static String getFullFilename(String filename){
		Log.d(TAG, "Reading file "+ filename +"...");
		File sdcard = Environment.getExternalStorageDirectory();

		//Get the text file
		File file = new File(sdcard,filename);
		
		return file.getAbsolutePath();
		//Find the view by its id
//		return file;
	}
	
	public static FileOutputStream saveImage(String filename){
		Log.d(TAG, "Writing file "+ filename +"...");
		File sdcard = Environment.getExternalStorageDirectory();
		FileOutputStream out = null;
		try {
			File file = new File(sdcard,filename);
			out = new FileOutputStream(file);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		//Get the text file
		
		return out;
	}
	
	public static float round2Float(double input){
		return (float) (Math.round(input*100)/100.00);
	}
	
}
