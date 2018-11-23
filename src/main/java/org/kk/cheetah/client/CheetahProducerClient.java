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
    private int coreThreadNum = 5;
    private long threadFreeTime = 10l;
    private AtomicLong messageId = new AtomicLong();

    public CheetahProducerClient(String clientId) {
        super(clientId);
        threadParkCoordinator = new ProducerThreadParkCoordinator();
        nettyClientHandler = new NettyProducerClientHandler(
                new ProducerHandlerSelector(threadParkCoordinator));
        executor = new ThreadPoolExecutor(coreThreadNum, coreThreadNum, threadFreeTime,
                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    }

    public Future<ProducerRecord> send(final ProducerRecordRequest producerRecordRequest) {
        producerRecordRequest.setClientId(getClientId());
        producerRecordRequest.setDataId(getClientId() + messageId.getAndIncrement());
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

}
