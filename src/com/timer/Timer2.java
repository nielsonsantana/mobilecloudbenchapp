package com.timer;
public class Timer2 {
	
	private long start = 0;
	private long stop = 0;
	
	public void start() {
		start = System.currentTimeMillis();
	}
	
	public void stop() { 
		stop = System.currentTimeMillis();
	}
	
	public long result() {
		if(start != 0 || stop != 0 || start > stop || start == Long.MAX_VALUE || stop == Long.MAX_VALUE) {
			return stop - start;
		} else {
			return -1;
		}
	}
	
	public float result_seconds(){
		return (float) (this.result()/1000.0);
	}
	
	public void reset() {
		start = 0;
		stop = 0;
	}
}