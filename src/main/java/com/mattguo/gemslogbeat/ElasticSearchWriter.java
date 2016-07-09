package com.mattguo.gemslogbeat;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.mattguo.gemslogbeat.config.Cfg;

public class ElasticSearchWriter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchWriter.class);

    private int maxRetry;
    private int timeoutSeconds;

    private Client client;
    ListeningExecutorService uploadExectuor = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(1));
    AtomicInteger pendingUploading;
    AtomicInteger uploaded;
    AtomicInteger retried;
    AtomicInteger failed;
    AtomicInteger id;

    public ElasticSearchWriter() {
        timeoutSeconds = Cfg.one().getEs().getTimeoutSeconds();
        maxRetry = Cfg.one().getEs().getRetryCount();
    }

    public void open(String host, int port) throws UnknownHostException {
        client = TransportClient.builder().build()
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
        pendingUploading = new AtomicInteger(0);
        uploaded = new AtomicInteger(0);
        retried = new AtomicInteger(0);
        failed = new AtomicInteger(0);
        id = new AtomicInteger(0);
    }

    public int pendingUploads() {
        return pendingUploading.get();
    }

    public void close(boolean waitForPendingUploads) {
        if (!waitForPendingUploads) {
            client.close();
            return;
        }

        while (true) {
            if (pendingUploading.get() <= 0) {
                client.close();
                return;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }
    }

    public void uploadAsync(final String index, final String type, final List<IndexedEntry> entries) {
        final BulkRequestBuilder bulkRequest = client.prepareBulk();
        for (IndexedEntry entry : entries) {
            bulkRequest.add(client.prepareIndex(index, type, Integer.toString(id.incrementAndGet())).setSource(entry.toEsJson()));
        }

        uploadAsync(bulkRequest, 0);
    }

    private void uploadAsync(final BulkRequestBuilder bulkRequest, final int retiedTimes) {
        pendingUploading.incrementAndGet();

        uploadExectuor.submit(new Runnable() {
            @Override
            public void run() {
                BulkResponse bulkResponse = bulkRequest.get(TimeValue.timeValueSeconds(timeoutSeconds));
                int pendingVal = pendingUploading.decrementAndGet();

                if (bulkResponse.hasFailures()) {
                    int failedVal;
                    int retriedVal;
                    boolean willRetry;
                    if (retiedTimes >= maxRetry) {
                        failedVal = failed.incrementAndGet();
                        retriedVal = retried.get();
                        willRetry = false;
                    } else {
                        failedVal = failed.get();
                        retriedVal = retried.incrementAndGet();
                        willRetry = false;
                        uploadAsync(bulkRequest, retiedTimes + 1);
                    }
                    LOGGER.warn("Bulk upload Failed. pending:{}, failed:{}, totalRetried:{}, retry:{}/{}, willRetry:{}, err:{}",
                            pendingVal, failedVal, retriedVal, retiedTimes, maxRetry, willRetry,
                            bulkResponse.buildFailureMessage());
                } else {
                    int uploadedVal = uploaded.incrementAndGet();
                    LOGGER.info("Bulk upload finished. pending:{}, uploaded:{}", pendingVal, uploadedVal);
                }
            }
        });
    }
}
