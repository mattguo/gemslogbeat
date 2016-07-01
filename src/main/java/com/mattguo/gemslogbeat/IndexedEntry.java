package com.mattguo.gemslogbeat;

import org.elasticsearch.common.xcontent.XContentBuilder;

public interface IndexedEntry {
    XContentBuilder toEsJson();
}
