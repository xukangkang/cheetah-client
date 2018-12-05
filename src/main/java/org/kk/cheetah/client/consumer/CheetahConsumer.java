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

    public CheetahConsumer(Properties properties) {
        String clientId = "p1";
        /* String clientId = (String) properties.get("clientId");
        if (clientId == null) {
            throw new NullPointerException("clientId不能为空");
        }*/
        String group = (String) properties.get("group");
        if (group == null) {
            throw new NullPointerException("group不能为空");
        }
        String topic = properties.getProperty("topic");
        if (topic == null) {
            throw new NullPointerException("topic 不能为 null");
        }
        String server = properties.getProperty("server");
        if (server == null) {
            throw new NullPointerException("server 不能为 null");
        }
        cheetahConsumerClient = CheetahConsumerClient
                .cheetahConsumerClientBuilder()
                .cluster(server)
                .clientId(clientId)
                .group(group)
                .coreThreadNum(5)
                .maxThreadNum(5)
                .maxPollNum(100000)
                .topic(topic)
                .threadFreeTime(10l)
                .buildCheetahConsumerClient();
        cheetahConsumerClient.start();
    }

    public ConsumerRecords<K, V> poll() {
        Future<ConsumerRecords> consumerRecordsFuture = cheetahConsumerClient.poll();
        ConsumerRecords consumerRecords = null;
        try {
            consumerRecords = consumerRecordsFuture.get();
            if (consumerRecords.getErrorMsg() != null) {
                cheetahConsumerClient.shutdown();
                //TODO自定义异常
                throw new RuntimeException(consumerRecords.getErrorMsg());
            }
        } catch (InterruptedException e) {
            logger.error("poll", e);
        } catch (ExecutionException e) {
            logger.error("poll", e);
        }
        return consumerRecords;
    }

}
