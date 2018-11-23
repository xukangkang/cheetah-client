package org.kk.cheetah.client.task;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.kk.cheetah.common.model.request.ProducerRecordRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;

public class NettyDataSendTask implements Runnable {
    private Logger logger = LoggerFactory.getLogger(NettyDataSendTask.class);
    private ChannelHandlerContext ctx;
    private final BlockingQueue<ProducerRecordRequest> producDataRequestQueue = new LinkedBlockingQueue<ProducerRecordRequest>();

    public BlockingQueue<ProducerRecordRequest> getProducDataRequestQueue() {
        return producDataRequestQueue;
    }

    public NettyDataSendTask(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public void run() {
        ProducerRecordRequest producerRecord = null;
        try {
            while ((producerRecord = producDataRequestQueue.take()) != null) {
                ctx.writeAndFlush(producerRecord);
            }
        } catch (InterruptedException e) {
            logger.error("run", e);
        }
    }

    public void send(ProducerRecordRequest producerRecordRequest) {
        producDataRequestQueue.offer(producerRecordRequest);
    }

}
