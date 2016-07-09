package com.mattguo.gemslogbeat;

import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.mattguo.gemslogbeat.config.LatencyRule;

public class LatencyChecker {
    private static final Logger LOGGER = LoggerFactory.getLogger(LatencyChecker.class);

    class LatencyDataKey {
        public LatencyDataKey(LatencyRule latency, String host, String coalesceId) {
            this.latency = latency;
            this.host = host;
            this.coalesceId = coalesceId;
        }

        private LatencyRule latency;
        private String host;
        private String coalesceId;

        @Override
        public boolean equals(final Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            final LatencyDataKey other = (LatencyDataKey) obj;
            return Objects.equal(this.latency, other.latency) && Objects.equal(this.host, other.host)
                    && Objects.equal(this.coalesceId, other.coalesceId);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(this.latency, this.host, this.coalesceId);
        }

        @Override
        public String toString() {
            return String.format("{%s id:%s host:%s}", latency, coalesceId, host);
        }
    }

    class LatencyDataValue {
        public LatencyDataValue(GemsLogLine startLine) {
            this.startLine = startLine;
        }

        private GemsLogLine startLine;
        private Map<String, Integer> counts = Maps.newHashMap();
    }

    Map<LatencyDataKey, LatencyDataValue> latencyData = Maps.newHashMap();

    public LatencyIndexedEntry onNewLine(LatencyRule latencyRule, String host, GemsLogLine indexedLine) {
        Object id = indexedLine.getProperties().get(latencyRule.getId());
        if (id == null) {
            return null;
        }
        String idStr = id.toString();
        LatencyDataKey key = new LatencyDataKey(latencyRule, host, idStr);
        if (indexedLine.getTags().contains(latencyRule.getStartTag())) {
            if (latencyData.containsKey(key)) {
                LOGGER.warn("{} found startTag but is already in latencyData, ignore previous one at. newTs:{} oldTs:{}", key,
                        indexedLine.getProperties().get("@timestamp"),
                        latencyData.get(key).startLine.getProperties().get("@timestamp"));
            }
            // TODO handle copyProps
            latencyData.put(key, new LatencyDataValue(indexedLine));
            return null;
        }

        if (indexedLine.getTags().contains(latencyRule.getEndTag())) {
            LatencyDataValue context = latencyData.remove(key);
            if (context == null) {
                LOGGER.warn("{} found end but there's no context in latencyData, ignore. ts:{}", key,
                        indexedLine.getProperties().get("@timestamp"));
                return null;
            }
            Date startTime = (Date) context.startLine.getProperties().get("@timestamp");
            Date endTime = (Date) indexedLine.getProperties().get("@timestamp");

            LatencyIndexedEntry latencyLine = new LatencyIndexedEntry(startTime, endTime, host, idStr, latencyRule.getName());
            // TODO handle copyProps
            return latencyLine;
        }

        // TODO handle countTags

        return null;
    }
}
