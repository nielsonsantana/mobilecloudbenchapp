package com.listsorter;

import java.util.Collections;
import java.util.List;

import com.timer.Timer;

public class ListSorterLocal {
	
	private long time;
	private String errorMessage = "";
	
	// 	public void listSorter(List<String> wordList) {
	public String listSorter(List<String> wordList) {
		if(wordList.isEmpty() == false) {
			Collections.shuffle(wordList);
			new Timer();
			Timer.reset();
			Timer.start();
			Collections.sort(wordList);
			Timer.stop();
			this.time = Timer.result();
			
			//
			return wordList.get(500) + "" + wordList.get(501);
		} else {
			this.errorMessage = "ListSorterLocal.listSorter(): inputError"; 
			this.time = -1;
			return "ListSorterLocal Error";
		}
	}
	
	public String errorMessage() {
		return this.errorMessage;
	}
	
	public long returnTime() {
		return this.time;
	}
}