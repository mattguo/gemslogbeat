package com.mattguo.gemslogbeat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;
import com.mattguo.gemslogbeat.config.Cfg;

public class LogFileReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogFileReader.class);
    //private static Pattern segmentsPattern = Pattern.compile(Config.getInstance().getString("pattern", "segment", ""));
    //private static Pattern lineHeaderPattern = Pattern.compile(Config.getInstance().getString("pattern", "lineheader", ""));

	public static final String UTF8_BOM = "\uFEFF";

    private Dispatcher dispatcher;

    final static Pattern fileNamePattern = Pattern.compile("([^\\s\\\\/]+)_\\d\\d\\d\\d-\\d\\d-\\d\\d-\\d\\d-\\d\\d\\.log$");

    private Multiline multiline;

    public LogFileReader() {
    	multiline= new Multiline(Cfg.one().getLineHeader());
    }

    public void readDirs(String[] dirs) {
        dispatcher = new Dispatcher();
        try {
            dispatcher.open();
        } catch (UnknownHostException ex) {
            LOGGER.error("Failed to open dispatcher.", ex);
            return;
        }

        for(String dir : dirs)
            readDir(dir, dispatcher);

        dispatcher.close();
    }

    public void readDir(String dir, Dispatcher dispatcher){
    	File directory = new File(dir);
    	for (File file : Files.fileTreeTraverser().preOrderTraversal(directory)) {
    		if (file.isFile()) {
                LOGGER.info("Start to handle file: {}", file.getPath());
                read(file.getPath());
            } else {
            	LOGGER.info("Ignore the sub-dir: {}", file.getPath());
            }
    	}
    }

    private static String removeUTF8BOM(String s) {
        if (s.startsWith(UTF8_BOM)) {
            s = s.substring(1);
        }
        return s;
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
            	    dispatcher.onNewLine(newLine, host);
            }
            String newLine = multiline.remainingString();
            if(newLine != null)
                dispatcher.onNewLine(newLine, host);

            // Close the input stream
            fstream.close();
        } catch (Exception ex) {// Catch exception if any
            LOGGER.error("Failed to handle file {}", file, ex);
        }
    }
}
