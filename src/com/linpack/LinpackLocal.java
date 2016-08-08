package com.linpack;

import java.util.HashMap;

import com.basebenchmark.BaseBenchmark;
import com.linpackbm.*;

public class LinpackLocal extends BaseBenchmark {
    
	public String runBenchmark(HashMap<String, String> paramets){
		
		int parameter = Integer.parseInt(paramets.values().toArray()[0].toString());
		
		Linpack l = new Linpack();
    	String result = l.run_benchmark(parameter);
		
		return result;
	}
}
