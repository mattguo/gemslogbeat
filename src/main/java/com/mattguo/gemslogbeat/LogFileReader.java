package com.mattguo.gemslogbeat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

public class LogFileReader {
    //private static Pattern segmentsPattern = Pattern.compile(Config.getInstance().getString("pattern", "segment", ""));
    //private static Pattern lineHeaderPattern = Pattern.compile(Config.getInstance().getString("pattern", "lineheader", ""));

    private Dispatcher dispatcher;

    public LogFileReader() {
    }

    private String previousLine = "";

    public void readDir(String dir){
        dispatcher = new Dispatcher();
        try {
            dispatcher.open();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        File directory = new File(dir);
        File[] fList = directory.listFiles();
        for (File file : fList){
            System.out.println("File: " + file.getPath());
            read(file.getPath());
        }
        dispatcher.close();
    }

    private void read(String file) {
        try {
            FileInputStream fstream = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String line;

            while ((line = br.readLine()) != null) {
                dispatcher.onNewLine(line);
            }
            // Close the input stream
            fstream.close();
        } catch (Exception e) {// Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }
}
