package org.kk.cheetah.client;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;

import org.kk.cheetah.client.assist.ConsumerThreadParkCoordinator;
import org.kk.cheetah.client.assist.ThreadPark;
import org.kk.cheetah.client.handler.selector.ConsumerHandlerSelector;
import org.kk.cheetah.client.nettyhandler.NettyConsumerClientHandler;
import org.kk.cheetah.common.model.request.ConsumerRecordRequest;
import org.kk.cheetah.common.model.response.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheetahConsumerClient extends CheetahAbstractClient {

    private final Logger logger = LoggerFactory.getLogger(CheetahConsumerClient.class);

    private String group;
    private int maxPollNum = 5;
    private AtomicLong pollTag = new AtomicLong();
    private ExecutorService executor;
    private int coreThreadNum = 5;
    private long threadFreeTime = 10l;

    private CheetahConsumerClient() {
        threadParkCoordinator = new ConsumerThreadParkCoordinator();
        nettyClientHandler = new NettyConsumerClientHandler(new ConsumerHandlerSelector(threadParkCoordinator));
        executor = new ThreadPoolExecutor(coreThreadNum, coreThreadNum, threadFreeTime,
                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    }

    public Future<ConsumerRecords> poll() {
        final ConsumerRecordRequest consumerRecordRequest = new ConsumerRecordRequest();
        consumerRecordRequest.setClientId(clientId);
        consumerRecordRequest.setGroup(group);
        consumerRecordRequest.setMaxPollNum(maxPollNum);
        consumerRecordRequest.setTopic(topic);
        consumerRecordRequest.setPollTag(getClientId() + pollTag.getAndIncrement());
        if (logger.isDebugEnabled()) {
            logger.debug("poll ->发送第 {} 条消息", pollTag.get());
        }
        return executor.submit(new Callable<ConsumerRecords>() {
            public ConsumerRecords call() throws Exception {
                ((NettyConsumerClientHandler) nettyClientHandler).poll(consumerRecordRequest);
                ThreadPark threadPark = new ThreadPark();
                threadPark.setOnlyTag(consumerRecordRequest.getPollTag());
                threadPark.setThread(Thread.currentThread());
                threadParkCoordinator.addProducerThreadParkList(threadPark);
                ConsumerRecords consumerRecords = new ConsumerRecords();
                //TODO 指定超时时间，超时重发 
                LockSupport.park(consumerRecords);
                return consumerRecords;
            }
        });

    }

    public String getGroup() {
        return group;
    }

    public static CheetahConsumerClientBuilder cheetahConsumerClientBuilder() {
        return new CheetahConsumerClientBuilder();
    }

    public static class CheetahConsumerClientBuilder {
        private String clientId;
        private String group;
        private int maxPollNum;
        private int maxThreadNum;
        private int coreThreadNum;
        private long threadFreeTime;
        private String cluster;
        private String topic;
        private ExecutorService executor;

        public CheetahConsumerClientBuilder() {
        }

        public CheetahConsumerClientBuilder cluster(String cluster) {
            this.cluster = cluster;
            return this;
        }

        public CheetahConsumerClientBuilder topic(String topic) {
            this.topic = topic;
            return this;
        }

        public CheetahConsumerClientBuilder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public CheetahConsumerClientBuilder maxPollNum(int maxPollNum) {
            this.maxPollNum = maxPollNum;
            return this;
        }

        public CheetahConsumerClientBuilder coreThreadNum(int coreThreadNum) {
            this.coreThreadNum = coreThreadNum;
            return this;
        }

        public CheetahConsumerClientBuilder maxThreadNum(int maxThreadNum) {
            this.maxThreadNum = maxThreadNum;
            return this;
        }

        public CheetahConsumerClientBuilder group(String group) {
            this.group = group;
            return this;
        }

        public CheetahConsumerClientBuilder threadFreeTime(long threadFreeTime) {
            this.threadFreeTime = threadFreeTime;
            return this;
        }

        public CheetahConsumerClient buildCheetahConsumerClient() {
            CheetahConsumerClient cheetahConsumerClient = new CheetahConsumerClient();
            cheetahConsumerClient.clientId = this.clientId;
            cheetahConsumerClient.group = this.group;
            cheetahConsumerClient.maxPollNum = this.maxPollNum;
            cheetahConsumerClient.cluster = this.cluster;
            cheetahConsumerClient.topic = this.topic;
            executor = new ThreadPoolExecutor(coreThreadNum, maxThreadNum, threadFreeTime,
                    TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
            cheetahConsumerClient.executor = this.executor;
            return cheetahConsumerClient;
        }
    }
}