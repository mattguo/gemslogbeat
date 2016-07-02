package com.mattguo.gemslogbeat;

import java.net.UnknownHostException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.google.common.collect.Lists;
import com.mattguo.gemslogbeat.config.Cfg;
import com.mattguo.gemslogbeat.config.EntryFilter;

public class Dispatcher {
    private static DateTimeFormatter iso = ISODateTimeFormat.dateTime();

    List<Pattern> patterns = Lists.newArrayList();
    List<String[]> groupNames = Lists.newArrayList();

    public Dispatcher() {
        for(EntryFilter filter : Cfg.one().filters()) {
            patterns.add(Pattern.compile(filter.regex()));
            groupNames.add(Util.findGroupName(filter.regex()));
        }
    }

    List<IndexedEntry> cachedEntries = Lists.newArrayList();
    ElasticSearchWriter uploader = new ElasticSearchWriter();

    public void open() throws UnknownHostException {
        uploader = new ElasticSearchWriter();
        uploader.open("localhost", 9300);
    }

    public void onNewLine(String line) {
        //Merge new line
        GemsLogLine indexedLine = new GemsLogLine();
        for(int i = 0; i < patterns.size(); i++) {
            Pattern p = patterns.get(i);
            String[] names = groupNames.get(i);
            String message = indexedLine.getStringProperty("message");
            if (message == null)
                message = line;

            Matcher matcher = p.matcher(message);
            if (matcher.find()) {
                for(int j = 0; j < matcher.groupCount(); j++) {
                    if ("timestamp".equals(names[j])) {
                        DateTime dateTime = iso.parseDateTime(matcher.group(j));
                        indexedLine.getProperties().put("@timestamp", dateTime.toDate());
                    } else {
                        indexedLine.getProperties().put(names[j], matcher.group(j));
                    }
                }
            }
        }

        //Run through regex to parse message and add tags/properties

        //customize logic to calc latency.

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

    public void close() {
        if (cachedEntries.size() > 0) {
            uploader.uploadAsync("gems-test", "gems", cachedEntries);
            cachedEntries.clear();
        }
        uploader.close(true);
        System.out.println("Dispatcher closed");
    }
}
