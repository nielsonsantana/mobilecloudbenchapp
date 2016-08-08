package com.primecalc;

import java.util.HashMap;

import com.basebenchmark.BaseBenchmark;
import com.linpackbm.Linpack;
import com.primecalcbm.PrimeCalc;
import com.timer.Timer;

public class PrimeCalcLocal extends BaseBenchmark{
    
//    private final int UPPER_LIMIT = 10000;
//    private int primeNumberCounter = 0;
//    private long time;
	
	@Override
	public String runBenchmark(HashMap<String, String> paramets){
		
		int parameter = Integer.parseInt(paramets.values().toArray()[0].toString());
		
		PrimeCalc primebm = new PrimeCalc();
		String result = primebm.run_benchmark(parameter);
		
		return result;
	}
}
