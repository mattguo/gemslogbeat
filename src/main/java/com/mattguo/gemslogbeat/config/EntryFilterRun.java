package com.mattguo.gemslogbeat.config;

public class EntryFilterRun {
    private EntryFilter[] filters = new EntryFilter[0];
    private LatencyCheck[] latencies = new LatencyCheck[0];

    public EntryFilter[] getFilters() {
        return filters;
    }

    public void setFilters(EntryFilter[] filters) {
        this.filters = filters;
    }

    public LatencyCheck[] getLatencies() {
        return latencies;
    }

    public void setLatencies(LatencyCheck[] latencies) {
        this.latencies = latencies;
    }
}
