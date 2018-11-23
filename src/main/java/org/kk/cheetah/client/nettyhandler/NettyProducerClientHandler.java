package org.kk.cheetah.client.nettyhandler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.kk.cheetah.client.handler.selector.HandlerSelector;
import org.kk.cheetah.common.model.request.ProducerRecordRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;

public class NettyProducerClientHandler extends NettyAbstractClientHandler {

    public NettyProducerClientHandler(HandlerSelector handlerSelector) {
        super(handlerSelector);
    }

    private NettyDataSendTask nettyDataSendTask;

    @Override
    protected void init(ChannelHandlerContext ctx) {
        nettyDataSendTask = new NettyDataSendTask(ctx);
        new Thread(nettyDataSendTask).start();
    }

    public void send(ProducerRecordRequest producerRecordRequest) {
        nettyDataSendTask.send(producerRecordRequest);
    }

    class NettyDataSendTask implements Runnable {
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
            ProducerRecordRequest producerRecordRequest = null;
            try {
                while ((producerRecordRequest = producDataRequestQueue.take()) != null) {
                    ctx.writeAndFlush(producerRecordRequest);
                }
            } catch (InterruptedException e) {
                logger.error("run", e);
            }
        }

        private void send(ProducerRecordRequest producerRecordRequest) {
            producDataRequestQueue.offer(producerRecordRequest);
        }

    }
}
