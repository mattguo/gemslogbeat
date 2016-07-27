package com.mattguo.gemslogbeat;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.elasticsearch.common.xcontent.XContentBuilder;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class GemsLogLine implements IndexedEntry {

    private Set<String> tags = Sets.newHashSet();
    private Map<String, Object> props = Maps.newHashMap();

    public Set<String> getTags() {
        return tags;
    }

    public Map<String, Object> getProperties() {
        return props;
    }

    public String getStringProperty(String propName) {
        Object obj = props.get(propName);
        return (obj != null) ? obj.toString() : null;
    }

    @Override
    public void toEsJson(XContentBuilder jsonBuilder, String nestedName) throws IOException {
        XContentBuilder obj = nestedName == null ? jsonBuilder.startObject() : jsonBuilder.startObject(nestedName);
        for (Map.Entry<String, Object> entry : props.entrySet()) {
            if (entry.getValue() instanceof IndexedEntry) {
                IndexedEntry indexedEntry = (IndexedEntry) entry.getValue();
                indexedEntry.toEsJson(obj, entry.getKey());
            } else {
                obj.field(entry.getKey(), entry.getValue());
            }
        }
        String[] tags = new String[getTags().size()];
        obj.field("tags", getTags().toArray(tags));
        obj.endObject();
    }
}
