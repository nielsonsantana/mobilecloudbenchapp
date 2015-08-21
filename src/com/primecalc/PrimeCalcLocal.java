package com.primecalc;

import com.timer.Timer;

public class PrimeCalcLocal {
    
    private final int UPPER_LIMIT = 10000;
    private int primeNumberCounter = 0;
    private long time;
    
    public void calculatePrimeNumbers(int upper_limit) {
    	time = 0;
    	primeNumberCounter = 0;
        int i = 0;        
		new Timer();
		Timer.reset();
		Timer.start();
		if (upper_limit == 0)
			upper_limit = UPPER_LIMIT;
		
        while (++i <= upper_limit) {

            int i1 = (int) Math.ceil(Math.sqrt(i));

            boolean isPrimeNumber = false;

            while (i1 > 1) {

                if ((i != i1) && (i % i1 == 0)) {
                    isPrimeNumber = false;
                    break;
                } else if (!isPrimeNumber) {
                    isPrimeNumber = true;
                }

                --i1;
            }

            if (isPrimeNumber) {
                //System.out.println(i);
                ++this.primeNumberCounter;
            }
        }
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
