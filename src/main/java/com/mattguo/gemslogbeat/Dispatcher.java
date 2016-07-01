package com.mattguo.gemslogbeat;

import java.net.UnknownHostException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;
import com.mattguo.gemslogbeat.config.Cfg;

public class Dispatcher {
    Pattern segmentsPattern = Pattern.compile(Cfg.one().patterns().segment());

    List<IndexedEntry> cachedEntries = Lists.newArrayList();
    ElasticSearchWriter uploader = new ElasticSearchWriter();

    public void open() throws UnknownHostException {
        uploader = new ElasticSearchWriter();
        uploader.open("localhost", 9300);
    }

    public void onNewLine(String line) {
        //Merge new line

        //Run through segment regex.

        //Run through regex to parse message and add tags/properties

        //customize logic to calc latency.
        Matcher matcher = segmentsPattern.matcher(line);
        if (matcher.find()) {
            GemsLogLine indexedLine = new GemsLogLine();
            indexedLine.setTimestamp(matcher.group("ts"));
            indexedLine.setLevel(matcher.group("level"));
            indexedLine.setThr(matcher.group("thr"));
            indexedLine.setCat(matcher.group("cat"));
            indexedLine.setMessage(matcher.group("msg"));
            cachedEntries.add(indexedLine);
            if (cachedEntries.size() >= 10000) {
                uploader.uploadAsync("gems-test", "gems", cachedEntries);
                cachedEntries.clear();
                while(true) {
                    int pendingUploads = uploader.pendingUploads();
                    System.out.println("Pending uploads: " + pendingUploads);
                    if (pendingUploads < 64)
                        break;
                    else {
                        System.out.println("Sleep for 500ms to wait for uploader.");
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                        }
                    }
                }
            }
        }
    }

    public void close() {
        if (cachedEntries.size() > 0) {
            uploader.uploadAsync("gems-test", "gems", cachedEntries);
            cachedEntries.clear();
        }
        uploader.close(true);
        System.out.println("Dispatcher closed");
    }
}
