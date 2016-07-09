package com.mattguo.gemslogbeat;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.elasticsearch.common.xcontent.XContentBuilder;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class LatencyIndexedEntry implements IndexedEntry {
    public LatencyIndexedEntry(Date startTime, Date endTime, String host, String id, String name) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.host = host;
        this.id = id;
        this.name = name;
    }

    private Date startTime;
    private Date endTime;
    private String host;
    private String id;
    private String name;

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public String getHost() {
        return host;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int elapsedMs() {
        return (int) (endTime.getTime() - startTime.getTime());
    }

    private Map<String, Integer> counts = Maps.newHashMap();
    private Set<String> tags = Sets.newHashSet("latency");

    public Map<String, Integer> getCounts() {
        return counts;
    }

    public Set<String> getTags() {
        return tags;
    }

    @Override
    public XContentBuilder toEsJson() {
        try {
            XContentBuilder obj = jsonBuilder().startObject();
            obj.field("host", host);
            obj.field("id", id);
            obj.field("name", name);
            obj.field("startTime", startTime);
            obj.field("endTime", startTime);
            obj.field("@timestamp", startTime);
            obj.field(" elapsedMs", elapsedMs());

            for (Map.Entry<String, Integer> entry : counts.entrySet()) {
                obj.field(entry.getKey() + "_count", entry.getValue());
            }

            String[] tags = new String[getTags().size()];
            obj.field("tags", getTags().toArray(tags));
            obj.endObject();
            return obj;
        } catch (IOException e) {
            return null;
        }
    }
}
