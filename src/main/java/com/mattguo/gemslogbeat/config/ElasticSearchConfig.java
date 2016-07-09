package com.mattguo.gemslogbeat.config;

public class ElasticSearchConfig {
    private String url;
    private String index;
    private String doctype;
    private int port = 9300;
    private int uploadBulkSize = 10000;
    private int timeoutSeconds = 30;
    private int retryCount = 3;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getDoctype() {
        return doctype;
    }

    public void setDoctype(String doctype) {
        this.doctype = doctype;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getUploadBulkSize() {
        return uploadBulkSize;
    }

    public void setUploadBulkSize(int uploadBulkSize) {
        this.uploadBulkSize = uploadBulkSize;
    }

    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public void setTimeoutSeconds(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }
}
