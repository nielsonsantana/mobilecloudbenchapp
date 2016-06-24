package com.linpack;

import com.basebenchmark.BaseBenchmark;
import com.linpackbm.*;
import com.timer.Timer;

public class LinpackLocal extends BaseBenchmark {
    
    private int primeNumberCounter = 0;
    private long time;
    
    public void runLinpack(int parametro) {
		new Timer();
		Timer.reset();
		Timer.start();
    	
		Linpack l = new Linpack();
    	l.run_benchmark(parametro);
    	
		Timer.stop();
		this.time = Timer.result();
    }

    public String returnNumber() {
		return this.primeNumberCounter + "";
    }
    
	public long returnTime() {
		return this.time;
	}
}
