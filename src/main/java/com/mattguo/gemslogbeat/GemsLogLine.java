package com.mattguo.gemslogbeat;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

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
    public XContentBuilder toEsJson() {
        try {
            XContentBuilder obj = jsonBuilder().startObject();
            for(Map.Entry<String, Object> entry : props.entrySet()) {
                obj.field(entry.getKey(), entry.getValue());
            }
            String[] tags = new String[getTags().size()];
            obj.field("tags", getTags() .toArray(tags));
            obj.endObject();
            return obj;
        } catch (IOException e) {
            return null;
        }
    }
}
