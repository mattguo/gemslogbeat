package com.mattguo.gemslogbeat;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.util.Set;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.google.common.collect.Sets;

public class GemsLogLine implements IndexedEntry {
    private static DateTimeFormatter iso = ISODateTimeFormat.dateTime();

    // raw log line segments
    private String timestamp;
    private String level;
    private String cat;
    private String thr;
    private String message;

    // extra fields extracted from the message
    private Set<String> tags = Sets.newHashSet();
    private String email;
    private String pushchannel;
    private int syncDelay;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getCat() {
        return cat;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }

    public String getThr() {
        return thr;
    }

    public void setThr(String thr) {
        this.thr = thr;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPushchannel() {
        return pushchannel;
    }

    public void setPushchannel(String pushchannel) {
        this.pushchannel = pushchannel;
    }

    public int getSyncDelay() {
        return syncDelay;
    }

    public void setSyncDelay(int syncDelay) {
        this.syncDelay = syncDelay;
    }

    public Set<String> getTags() {
        return tags;
    }

    @Override
    public XContentBuilder toEsJson() {
        DateTime dateTime = iso.parseDateTime(getTimestamp());

        try {
            return jsonBuilder().startObject()
                .field("@timestamp", dateTime.toDate())
                .field("thr", this.getThr())
                .field("level", this.getLevel())
                .field("cat", this.getCat())
                .field("message", this.getMessage())
            .endObject();
        } catch (IOException e) {
            return null;
        }
    }
}
