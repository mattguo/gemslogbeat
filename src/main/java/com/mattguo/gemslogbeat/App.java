package com.mattguo.gemslogbeat;

import org.apache.commons.lang3.time.StopWatch;

import com.mattguo.gemslogbeat.config.Cfg;

public class App {

    public static void main(String[] args) throws InterruptedException {
        LogFileReader fr = new LogFileReader(new Dispatcher());
        StopWatch sw = new StopWatch();
        sw.start();
        fr.readDirs(Cfg.one().getInputDir());
        System.out.println("Elapsed time: " + sw.toString());
    }

}
