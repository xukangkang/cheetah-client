package org.kk.cheetah.client.nettyhandler;

import org.kk.cheetah.client.handler.Handler;
import org.kk.cheetah.client.handler.selector.HandlerSelector;
import org.kk.cheetah.common.model.response.ServerResponse;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public abstract class NettyAbstractClientHandler extends ChannelHandlerAdapter {

    private HandlerSelector handlerSelector;

    protected abstract void init(ChannelHandlerContext ctx);

    public NettyAbstractClientHandler(HandlerSelector handlerSelector) {
        this.handlerSelector = handlerSelector;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        init(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, final Object msg) throws Exception {
        if (msg instanceof ServerResponse) {
            ctx.executor().submit(new Runnable() {
                public void run() {
                    ServerResponse serverResponse = (ServerResponse) msg;
                    Handler handler = handlerSelector.select(serverResponse);
                    handler.handle(serverResponse);
                }
            });
        } else {
            //TODO 返回给客户端不支持响应
            throw new RuntimeException("不支持响应");
        }
    }

}
