package com.mattguo.gemslogbeat;

import java.io.IOException;
import java.util.Date;

import org.elasticsearch.common.xcontent.XContentBuilder;

public class LatencyIndexedEntry implements IndexedEntry {
    public LatencyIndexedEntry(Date startTime, Date endTime, String correlateId, String name) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.correlateId = correlateId;
        this.name = name;
    }

    private Date startTime;
    private Date endTime;
    private String correlateId;
    private String name;

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public String getCorrelateId() {
        return correlateId;
    }

    public String getName() {
        return name;
    }

    public int elapsedMs() {
        return (int) (endTime.getTime() - startTime.getTime());
    }

    @Override
    public void toEsJson(XContentBuilder jsonBuilder, String nestedName) throws IOException {
        // XContentBuilder obj = nestedName == null ? jsonBuilder.startObject() : jsonBuilder.startObject(nestedName);
        // obj.field("correlateId", correlateId);
        // obj.field("name", name);
        // obj.field("startTime", startTime);
        // obj.field("endTime", startTime);
        // obj.field(" elapsedMs", elapsedMs());
        // obj.endObject();

        XContentBuilder obj = nestedName == null ? jsonBuilder.startObject() : jsonBuilder;
        String nestedPrefix = nestedName == null ?  "" : nestedName + "_";
        obj.field(nestedPrefix + "correlateId", correlateId);
        obj.field(nestedPrefix + "name", name);
        obj.field(nestedPrefix + "startTime", startTime);
        obj.field(nestedPrefix + "endTime", startTime);
        obj.field(nestedPrefix + "elapsedMs", elapsedMs());

        if (nestedName == null)
            obj.endObject();
    }
}
