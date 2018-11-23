package org.kk.cheetah.client;

import org.kk.cheetah.client.assist.ThreadParkCoordinator;
import org.kk.cheetah.client.nettyhandler.NettyAbstractClientHandler;
import org.kk.cheetah.common.serializable.MarshallingCodeCFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class CheetahAbstractClient implements CheetahClient {
    private final Logger logger = LoggerFactory.getLogger(CheetahAbstractClient.class);
    protected NettyAbstractClientHandler nettyClientHandler;
    protected ThreadParkCoordinator threadParkCoordinator;
    private String clientId;

    public String getClientId() {
        return clientId;
    }

    public CheetahAbstractClient(String clientId) {
        this.clientId = clientId;
    }

    public void start() {
        String host = "127.0.0.1";
        int port = 9997;
        if (logger.isDebugEnabled()) {
            logger.debug("start param , host:{},port:{}", host, port);
        }
        connect(host, port);
    }

    private void connect(String host, int port) {
        EventLoopGroup workerGroup = null;
        try {
            workerGroup = new NioEventLoopGroup();
            Bootstrap bootstrap = new Bootstrap();
            bootstrap
                    .group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            if (nettyClientHandler == null) {
                                throw new NullPointerException("nettyClientHandler不能为空");
                            }
                            socketChannel.pipeline().addLast(
                                    MarshallingCodeCFactory.buildMarshallingEncoder());
                            socketChannel.pipeline().addLast(
                                    MarshallingCodeCFactory.buildMarshallingDecoder());
                            socketChannel.pipeline().addLast(nettyClientHandler);
                        }

                    });
            bootstrap.connect(host, port).sync();
        } catch (Exception e) {
            workerGroup.shutdownGracefully();
        }
    }

}
