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
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xzl
 * @date 2021-05-26 23:10
 **/
@Slf4j
public class RpcConsumer {

    private final Bootstrap bootstrap = new Bootstrap();

    private final EventLoopGroup eventLoopGroup;

    private final Map<String, Channel> channelMap = new HashMap<>();

    private final Object lock = new Object();

    public RpcConsumer() {
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
            Channel channel = getChannel(serviceMetaData.getServiceAddr(), serviceMetaData.getServicePort());
            channel.writeAndFlush(protocol).addListener((ChannelFutureListener) channelFuture -> {
                if (channelFuture.isSuccess()) {
                    log.info("send success");
                } else {
                    log.error("send error");
                }
            });
        }
    }

    private Channel getChannel(String serviceAddr, int servicePort) throws InterruptedException {
        Channel channel = channelMap.get(serviceAddr + servicePort);
        if (channel != null && channel.isActive()) {
            return channel;
        }
        synchronized (this.lock) {
            Channel cn = channelMap.get(serviceAddr + servicePort);
            if (cn != null && cn.isActive()) {
                return cn;
            }
            ChannelFuture future = bootstrap.connect(serviceAddr, servicePort).sync();
            Channel newCn = future.channel();
            channelMap.put(serviceAddr + servicePort, newCn);
            return newCn;
        }
    }
}
