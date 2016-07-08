package com.mattguo.gemslogbeat.config;

import java.util.regex.Pattern;

import com.mattguo.gemslogbeat.RegexUtil;

public class EntryFilter {
    private String regex;
    private String field = "message";
    private String addTag;
    private String hasTag;
    private String hasProp; //one kind of condition
    private LatencyCheck latency;
    // TODO private String typecast
    // TODO private String condition

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
        this.pattern = Pattern.compile(regex, Pattern.DOTALL);
        this.groupNames = RegexUtil.findGroupName(regex);
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getAddTag() {
        return addTag;
    }

    public void setAddTag(String addTag) {
        this.addTag = addTag;
    }

    public String getHasTag() {
        return hasTag;
    }

    public void setHasTag(String hasTag) {
        this.hasTag = hasTag;
    }

    public String getHasProp() {
        return hasProp;
    }

    public void setHasProp(String hasProp) {
        this.hasProp = hasProp;
    }

    public LatencyCheck getLatency() {
        return latency;
    }

    public void setLatency(LatencyCheck latency) {
        this.latency = latency;
    }

    private Pattern pattern;
    private String[] groupNames;

    public Pattern getPattern() {
        return pattern;
    }

    public String[] getGroupNames() {
        return groupNames;
    }
}
