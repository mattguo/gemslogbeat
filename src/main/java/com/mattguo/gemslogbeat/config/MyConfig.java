package com.mattguo.gemslogbeat.config;

public class MyConfig {
    private String lineHeader;
    private EntryFilter[] filters;

    public String getLineHeader() {
        return lineHeader;
    }

    public void setLineHeader(String lineHeader) {
        this.lineHeader = lineHeader;
    }

    public EntryFilter[] getFilters() {
        return filters;
    }

    public void setFilters(EntryFilter[] filters) {
        this.filters = filters;
    }
}
