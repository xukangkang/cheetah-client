package org.kk.cheetah.client.consumer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.kk.cheetah.client.CheetahConsumerClient;
import org.kk.cheetah.client.producer.CheetahProducer;
import org.kk.cheetah.common.model.response.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheetahConsumer<K, V> implements Consumer<K, V> {
    private final Logger logger = LoggerFactory.getLogger(CheetahProducer.class);
    private CheetahConsumerClient cheetahConsumerClient;
    private String clientId;
    private String group;

    public CheetahConsumer(Properties properties) {
        clientId = (String) properties.get("clientId");
        group = (String) properties.get("group");
        if (clientId == null || group == null) {
            throw new NullPointerException("clientId和group不能为空");
        }
        cheetahConsumerClient = new CheetahConsumerClient(clientId, group);
        cheetahConsumerClient.start();
    }

    public ConsumerRecords<K, V> poll() {
        Future<ConsumerRecords> consumerRecordsFuture = cheetahConsumerClient.poll();
        ConsumerRecords consumerRecords = null;
        try {
            consumerRecords = consumerRecordsFuture.get();
        } catch (InterruptedException e) {
            logger.error("poll", e);
        } catch (ExecutionException e) {
            logger.error("poll", e);
        }
        return consumerRecords;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

}
