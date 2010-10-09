package com.vas.android.bluetooth.sample.sample1;


public class StopWatch {
	private long startTime = 0;
    private long stopTime = 0;
    private long elapsed = 0;
    private boolean running = false;

    
    public void start() {
        this.startTime = System.currentTimeMillis();
        this.running = true;
    }

    
    public void stop() {
        this.stopTime = System.currentTimeMillis();
        this.running = false;
    }

    
    //elapsed time in milliseconds
    public long getElapsedTime() {
        //long this.elapsed;
        if (running) {
             this.elapsed = (System.currentTimeMillis() - startTime);
        }
        else {
            elapsed = (stopTime - startTime);
        }
        return elapsed;
    }
}
