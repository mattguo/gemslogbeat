package com.mattguo.gemslogbeat;

import java.net.UnknownHostException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mattguo.gemslogbeat.config.Cfg;
import com.mattguo.gemslogbeat.config.EntryFilter;

public class Dispatcher {
    private static DateTimeFormatter iso = ISODateTimeFormat.dateTime();
	//private static DateTimeFormatter iso = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final Logger LOGGER = LoggerFactory.getLogger(Dispatcher.class);

    public Dispatcher() {
    }

    List<IndexedEntry> cachedEntries = Lists.newArrayList();
    ElasticSearchWriter uploader = new ElasticSearchWriter();

    public void open() throws UnknownHostException {
        uploader = new ElasticSearchWriter();
        uploader.open("localhost", 9300);
    }

    public void onNewLine(final String line, final String host) {
        //Merge new line
        GemsLogLine indexedLine = new GemsLogLine();
        indexedLine.getProperties().put("host", host);
        for (EntryFilter filter : Cfg.one().getFilters()) {
            if (!Strings.isNullOrEmpty(filter.getHasProp()) && !indexedLine.getProperties().containsKey(filter.getHasProp())) {
                continue;
            }

            Pattern p = filter.getPattern();
            String[] names = filter.getGroupNames();
            String message = null;
            if (!Strings.isNullOrEmpty(filter.getField())) {
                message = indexedLine.getStringProperty(filter.getField());
            }
            if (message == null)
                message = line;

            Matcher matcher = p.matcher(message);
            if (matcher.find()) {
                for (int j = 1; j <= matcher.groupCount(); j++) {
                    String groupName = names[j - 1];
                    if ("timestamp".equals(groupName)) {
                        DateTime dateTime = iso.parseDateTime(matcher.group(j));
                        indexedLine.getProperties().put("@timestamp", dateTime.toDate());
                    } else {
                        indexedLine.getProperties().put(groupName, matcher.group(j));
                    }
                }

                if (!Strings.isNullOrEmpty(filter.getAddTag())) {
                    for(String tag : Splitter.on(',').split(filter.getAddTag()))
                        indexedLine.getTags().add(tag);
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
                LOGGER.info("Pending uploads: {}", pendingUploads);
                if (pendingUploads < 64)
                    break;
                else {
                    LOGGER.info("Sleep for 500ms to wait for uploader.");
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
        LOGGER.info("Dispatcher closed");
    }
}
