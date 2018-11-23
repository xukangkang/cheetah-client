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

    public CheetahConsumerClient(String clientId) {
        super(clientId);
        threadParkCoordinator = new ConsumerThreadParkCoordinator();
        nettyClientHandler = new NettyConsumerClientHandler(new ConsumerHandlerSelector(threadParkCoordinator));
        executor = new ThreadPoolExecutor(coreThreadNum, coreThreadNum, threadFreeTime,
                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    }

    public CheetahConsumerClient(String clientId, String group) {
        this(clientId);
        this.group = group;
    }

    public Future<ConsumerRecords> poll() {
        final ConsumerRecordRequest consumerRecordRequest = new ConsumerRecordRequest();
        consumerRecordRequest.setClientId(getClientId());
        consumerRecordRequest.setGroup(group);
        consumerRecordRequest.setMaxPollNum(maxPollNum);
        consumerRecordRequest.setPollTag(getClientId() + pollTag.getAndIncrement());

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

}
