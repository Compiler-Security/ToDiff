package org.generator.util.timer;

public class timer {
    long startTime, endTime;
    public void start(){
        startTime = System.currentTimeMillis();
    }

    public void finish(){
        endTime = System.currentTimeMillis();
    }

    public double getTime(){
        return (endTime - startTime) / 1000.0;
    }
}
