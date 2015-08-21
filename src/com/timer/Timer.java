package com.timer;
public class Timer {
	private static long start = 0;
	private static long stop = 0;
	
	public static void start() {
		start = System.currentTimeMillis();
	}
	
	public static void stop() { 
		stop = System.currentTimeMillis();
	}
	
	public static long result() {
		stop(); // Adicionando por Nielson
		if(start != 0 || stop != 0 || start > stop || start == Long.MAX_VALUE || stop == Long.MAX_VALUE) {
			return stop - start;
		} else {
			return -1;
		}
	}
	
	public static void reset() {
		start = 0;
		stop = 0;
	}
}
