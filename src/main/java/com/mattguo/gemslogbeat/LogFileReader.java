package com.mattguo.gemslogbeat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

public class LogFileReader {
    //private static Pattern segmentsPattern = Pattern.compile(Config.getInstance().getString("pattern", "segment", ""));
    //private static Pattern lineHeaderPattern = Pattern.compile(Config.getInstance().getString("pattern", "lineheader", ""));

	public static final String UTF8_BOM = "\uFEFF";
	
    private Dispatcher dispatcher;
    private Multiline multiline;
    
    public LogFileReader() {
    	multiline= new Multiline("^\\d\\d\\d\\d"); 
    }

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

    private static String removeUTF8BOM(String s) {
        if (s.startsWith(UTF8_BOM)) {
            s = s.substring(1);
        }
        return s;
    }
    
    private void read(String file) {
        try {
            FileInputStream fstream = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream, "UTF8"));
            String line;

            boolean firstLine = true;
            
            while ((line = br.readLine()) != null) {
            	if (firstLine) {
            		line = removeUTF8BOM(line);
                    firstLine = false;
                }
            	String newLine = multiline.addLine(line);
            	if(newLine != null)
                dispatcher.onNewLine(newLine);
            }
            String newLine = multiline.remainingString();
            if(newLine != null)
                dispatcher.onNewLine(newLine);
            		
            // Close the input stream
            fstream.close();
        } catch (Exception e) {// Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }
}
