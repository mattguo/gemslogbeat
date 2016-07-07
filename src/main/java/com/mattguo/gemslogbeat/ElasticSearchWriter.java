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

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

public class ElasticSearchWriter {
    private Client client;
    ListeningExecutorService uploadExectuor = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(1));
    AtomicInteger pendingUploading;
    AtomicInteger uploaded;
    AtomicInteger id;

    public ElasticSearchWriter() {

    }

    public void open(String host, int port) throws UnknownHostException {
        client = TransportClient.builder().build()
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
        pendingUploading = new AtomicInteger(0);
        uploaded = new AtomicInteger(0);
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

    public void uploadAsync(String index, String type, List<IndexedEntry> entries) {
        final BulkRequestBuilder bulkRequest = client.prepareBulk();
        for (IndexedEntry entry : entries) {
            bulkRequest.add(client.prepareIndex(index, type, Integer.toString(id.incrementAndGet())).setSource(entry.toEsJson()));
        }
        pendingUploading.incrementAndGet();

        uploadExectuor.submit(new Runnable() {
            @Override
            public void run() {
                BulkResponse bulkResponse = bulkRequest.get();
                int pendingVal = pendingUploading.decrementAndGet();
                int uploadedVal = uploaded.incrementAndGet();
                if (bulkResponse.hasFailures()) {
                    System.out.println("Bulk upload Failed. pending:" + pendingVal + ", uploaded:" + uploadedVal + ", err:" + bulkResponse.buildFailureMessage());
                } else {
                    System.out.println("Bulk upload finished. pending:" + pendingVal + ", uploaded:" + uploadedVal);
                }
            }
        });
    }
}
