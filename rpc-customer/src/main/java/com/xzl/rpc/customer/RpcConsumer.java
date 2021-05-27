package com.xzl.rpc.customer;

import com.xzl.rpc.codec.RpcDecoder;
import com.xzl.rpc.codec.RpcEncoder;
import com.xzl.rpc.common.RpcRequest;
import com.xzl.rpc.common.RpcServiceHelper;
import com.xzl.rpc.common.ServiceMeta;
import com.xzl.rpc.handler.RpcRequestHandler;
import com.xzl.rpc.handler.RpcResponseHandler;
import com.xzl.rpc.protocol.RpcProtocol;
import com.xzl.rpc.registry.RegistryService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author xzl
 * @date 2021-05-26 23:10
 **/
@Slf4j
public class RpcConsumer {

    private final Bootstrap bootstrap;

    private final EventLoopGroup eventLoopGroup;

    public RpcConsumer() {
        bootstrap = new Bootstrap();
        eventLoopGroup = new NioEventLoopGroup(4);
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast(new RpcEncoder())
                                .addLast(new RpcDecoder())
                                .addLast(new RpcResponseHandler());
                    }
                });
    }

    public void sendRequest(RpcProtocol<RpcRequest> protocol, RegistryService registryService) throws Exception {
        RpcRequest rpcRequest = protocol.getBody();
        Object[] params = rpcRequest.getParams();
        String serviceKey = RpcServiceHelper.buildServiceKey(rpcRequest.getClassName(), rpcRequest.getServiceVersion());
        int invokerHashCode = params.length > 0 ? params[0].hashCode() : serviceKey.hashCode();
        ServiceMeta serviceMetaData = registryService.discovery(serviceKey, invokerHashCode);
        if (serviceMetaData != null) {
            ChannelFuture future = bootstrap.connect(serviceMetaData.getServiceAddr(), serviceMetaData.getServicePort()).sync();
            future.addListener((ChannelFutureListener) channelFuture -> {
                if (channelFuture.isSuccess()) {
                    log.info("connect rpc server {} on port {} success.", serviceMetaData.getServiceAddr(), serviceMetaData.getServicePort());
                } else {
                    log.error("connect rpc server {} on port {} failed.", serviceMetaData.getServiceAddr(), serviceMetaData.getServicePort());
                    future.cause().printStackTrace();
                    eventLoopGroup.shutdownGracefully();
                }
            });
            future.channel().writeAndFlush(protocol).addListener((ChannelFutureListener) channelFuture -> {
                if (channelFuture.isSuccess()) {
                    log.error("send success");
                } else {
                    log.error("send error");
                }
            });
        }
    }
}
