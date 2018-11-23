package org.kk.cheetah.client.producer;

import java.util.Properties;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.kk.cheetah.client.CheetahProducerClient;
import org.kk.cheetah.common.model.request.ProducerRecordRequest;
import org.kk.cheetah.common.model.response.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheetahProducer<K, V> implements Producer<K, V> {
    private final Logger logger = LoggerFactory.getLogger(CheetahProducer.class);

    private CheetahProducerClient cheetahClient;
    private String clientId;

    public CheetahProducer(Properties properties) {
        String clientId = (String) properties.get("clientId");
        if (clientId == null) {
            throw new NullPointerException();
        }
        cheetahClient = new CheetahProducerClient(clientId);
        cheetahClient.start();
        try {
            //等待对方初始化操作
            TimeUnit.MICROSECONDS.sleep(100);
        } catch (InterruptedException e) {
            logger.error("new CheetahProducer", e);
        }

    }

    public Future<ProducerRecord> send(ProducerRecordRequest<K, V> producerRecord) {

        return cheetahClient.send(producerRecord);
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

}
