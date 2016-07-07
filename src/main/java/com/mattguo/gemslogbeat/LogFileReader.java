package com.mattguo.gemslogbeat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogFileReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogFileReader.class);
    //private static Pattern segmentsPattern = Pattern.compile(Config.getInstance().getString("pattern", "segment", ""));
    //private static Pattern lineHeaderPattern = Pattern.compile(Config.getInstance().getString("pattern", "lineheader", ""));

    private Dispatcher dispatcher;

    final static Pattern fileNamePattern = Pattern.compile("([^\\s\\\\/]+)_\\d\\d\\d\\d-\\d\\d-\\d\\d-\\d\\d-\\d\\d\\.log$");

    public LogFileReader() {
    }

    private String previousLine = "";

    public void readDir(String dir) {
        dispatcher = new Dispatcher();
        try {
            dispatcher.open();
        } catch (UnknownHostException ex) {
            LOGGER.error("Failed to open dispatcher.", ex);
            return;
        }

        readDir(dir, dispatcher);

        dispatcher.close();
    }

    public void readDir(String dir, Dispatcher dispatcher){
        File directory = new File(dir);
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                LOGGER.info("Start to handle file: " + file.getPath());
                read(file.getPath());
            } else {
                LOGGER.info("Start to handle sub-dir: " + file.getPath());
                readDir(file.getPath(), dispatcher);
            }
        }
    }

    private void read(String file) {
        Matcher m = fileNamePattern.matcher(file);
        String host = "";
        if (m.find()) {
            host = m.group(1);
        } else {
            LOGGER.error("Skip file {} since its name doesn't like a GEMS log file", file);
        }
        try {
            FileInputStream fstream = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String line;

            while ((line = br.readLine()) != null) {
                dispatcher.onNewLine(line, host);
            }
            // Close the input stream
            fstream.close();
        } catch (Exception ex) {// Catch exception if any
            LOGGER.error("Failed to handle file {}", file, ex);
        }
    }
}
