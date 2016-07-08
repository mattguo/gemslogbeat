package com.mattguo.gemslogbeat.config;

public class LatencyCheck {
    private String startTag;
    private String endTag;
    private String id;
    private int timeout;
    private String countTags;

    public String getStartTag() {
        return startTag;
    }

    public void setStartTag(String startTag) {
        this.startTag = startTag;
    }

    public String getEndTag() {
        return endTag;
    }

    public void setEndTag(String endTag) {
        this.endTag = endTag;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getCountTags() {
        return countTags;
    }

    public void setCountTags(String countTags) {
        this.countTags = countTags;
    }

}
