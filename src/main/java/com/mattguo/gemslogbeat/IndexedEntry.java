package com.mattguo.gemslogbeat;

import java.io.IOException;

import org.elasticsearch.common.xcontent.XContentBuilder;

public interface IndexedEntry {
    void toEsJson(XContentBuilder jsonBuilder, String nestedName) throws IOException;
}
