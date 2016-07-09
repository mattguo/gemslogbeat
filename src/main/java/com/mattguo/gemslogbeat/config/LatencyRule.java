package com.mattguo.gemslogbeat.config;

import com.google.common.base.Splitter;

public class LatencyRule {
    private String name;
    private String startTag;
    private String endTag;
    private String id;
    private String countTags;
    private String copyProps;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public String getCountTags() {
        return countTags;
    }

    public Iterable<String> getCountTagsCollection() {
        return Splitter.on(",").split(countTags);
    }

    public void setCountTags(String countTags) {
        this.countTags = countTags;
    }

    public String setCopyProps() {
        return copyProps;
    }

    public Iterable<String> getCopyPropsCollection() {
        return Splitter.on(",").split(copyProps);
    }

    public void setCopyProps(String copyProps) {
        this.copyProps = copyProps;
    }

    @Override
    public String toString() {
        return String.format("(Latency %s, %s->%s:%s)", name, startTag, endTag, id);
    }
}
