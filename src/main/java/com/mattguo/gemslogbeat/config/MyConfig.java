package com.mattguo.gemslogbeat.config;

public class MyConfig {
    private String[] inputDir;
    private ElasticSearchConfig es;
    private String lineHeader;
    private EntryFilterRun[] runs = new EntryFilterRun[0];

    public String[] getInputDir() {
        return inputDir;
    }

    public void setInputDir(String[] inputDir) {
        this.inputDir = inputDir;
    }

    public ElasticSearchConfig getEs() {
        return es;
    }

    public void setEs(ElasticSearchConfig elasticSearchConfig) {
        this.es = elasticSearchConfig;
    }

    public String getLineHeader() {
        return lineHeader;
    }

    public void setLineHeader(String lineHeader) {
        this.lineHeader = lineHeader;
    }

    public EntryFilterRun[] getRuns() {
        return runs;
    }

    public void setRuns(EntryFilterRun[] runs) {
        this.runs = runs;
    }
}
