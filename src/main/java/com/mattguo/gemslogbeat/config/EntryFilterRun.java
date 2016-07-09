package com.mattguo.gemslogbeat.config;

public class EntryFilterRun {
    private EntryFilter[] filters = new EntryFilter[0];
    private LatencyRule[] latencies = new LatencyRule[0];

    public EntryFilter[] getFilters() {
        return filters;
    }

    public void setFilters(EntryFilter[] filters) {
        this.filters = filters;
    }

    public LatencyRule[] getLatencies() {
        return latencies;
    }

    public void setLatencies(LatencyRule[] latencies) {
        this.latencies = latencies;
    }
}
