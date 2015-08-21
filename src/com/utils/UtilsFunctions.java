package com.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;

import android.util.Log;

public class UtilsFunctions {
	
	
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

}
