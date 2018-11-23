package org.kk.cheetah.client.nettyhandler;

import org.kk.cheetah.client.handler.selector.HandlerSelector;
import org.kk.cheetah.common.model.request.ConsumerRecordRequest;

import io.netty.channel.ChannelHandlerContext;

public class NettyConsumerClientHandler extends NettyAbstractClientHandler {
    public NettyConsumerClientHandler(HandlerSelector handlerSelector) {
        super(handlerSelector);
    }

    private ChannelHandlerContext ctx;

    @Override
    protected void init(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public void poll(ConsumerRecordRequest consumerRecordRequest) {
        ctx.writeAndFlush(consumerRecordRequest);
    }

}
