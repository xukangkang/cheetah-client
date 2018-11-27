package org.kk.cheetah.client;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;

import org.kk.cheetah.client.assist.ProducerThreadParkCoordinator;
import org.kk.cheetah.client.assist.ThreadPark;
import org.kk.cheetah.client.handler.selector.ProducerHandlerSelector;
import org.kk.cheetah.client.nettyhandler.NettyProducerClientHandler;
import org.kk.cheetah.common.model.request.ProducerRecordRequest;
import org.kk.cheetah.common.model.response.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheetahProducerClient extends CheetahAbstractClient {

    private final Logger logger = LoggerFactory.getLogger(CheetahProducerClient.class);
    private ExecutorService executor;
    private AtomicLong messageId = new AtomicLong();

    private CheetahProducerClient() {
        threadParkCoordinator = new ProducerThreadParkCoordinator();
        nettyClientHandler = new NettyProducerClientHandler(new ProducerHandlerSelector(threadParkCoordinator));
    }

    public Future<ProducerRecord> send(final ProducerRecordRequest producerRecordRequest) {
        producerRecordRequest.setClientId(clientId);
        producerRecordRequest.setDataId(getClientId() + messageId.getAndIncrement());
        producerRecordRequest.setTopic(topic);
        return executor.submit(new Callable<ProducerRecord>() {

            public ProducerRecord call() throws Exception {
                ((NettyProducerClientHandler) nettyClientHandler).send(producerRecordRequest);
                ThreadPark threadPark = new ThreadPark();
                threadPark.setOnlyTag(producerRecordRequest.getDataId());
                threadPark.setThread(Thread.currentThread());
                threadParkCoordinator.addProducerThreadParkList(threadPark);
                ProducerRecord producDataResponse = new ProducerRecord();
                //TODO 指定超时时间，超时重发 
                LockSupport.park(producDataResponse);
                return producDataResponse;
            }

        });
    }

    public static CheetahProducerClientBuilder cheetahProducerClientBuilder() {
        return new CheetahProducerClientBuilder();
    }

    public static class CheetahProducerClientBuilder {
        private String clientId;
        private int maxThreadNum;
        private int coreThreadNum;
        private long threadFreeTime;
        private String topic;
        private String cluster;
        private ExecutorService executor;

        public CheetahProducerClientBuilder() {
        }

        public CheetahProducerClientBuilder cluster(String cluster) {
            this.cluster = cluster;
            return this;
        }

        public CheetahProducerClientBuilder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public CheetahProducerClientBuilder coreThreadNum(int coreThreadNum) {
            this.coreThreadNum = coreThreadNum;
            return this;
        }

        public CheetahProducerClientBuilder threadFreeTime(long threadFreeTime) {
            this.threadFreeTime = threadFreeTime;
            return this;
        }

        public CheetahProducerClientBuilder maxThreadNum(int maxThreadNum) {
            this.maxThreadNum = maxThreadNum;
            return this;
        }

        public CheetahProducerClientBuilder topic(String topic) {
            this.topic = topic;
            return this;
        }

        public CheetahProducerClient buildCheetahConsumerClient() {

            CheetahProducerClient cheetahProducerClient = new CheetahProducerClient();
            cheetahProducerClient.cluster = this.cluster;
            cheetahProducerClient.clientId = this.clientId;
            cheetahProducerClient.topic = this.topic;
            executor = new ThreadPoolExecutor(coreThreadNum, maxThreadNum, threadFreeTime,
                    TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
            cheetahProducerClient.executor = this.executor;

            return cheetahProducerClient;
        }
    }

    @Override
    public void shutdown() {
        // TODO Auto-generated method stub

    }
}
