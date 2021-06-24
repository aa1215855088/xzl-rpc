package com.xzl.rpc.provider;

import com.xzl.rpc.codec.RpcDecoder;
import com.xzl.rpc.codec.RpcEncoder;
import com.xzl.rpc.common.RpcServiceHelper;
import com.xzl.rpc.common.ServiceMeta;
import com.xzl.rpc.handler.RpcRequestHandler;
import com.xzl.rpc.provider.annotation.RpcServer;
import com.xzl.rpc.registry.RegistryService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.reflect.FastClass;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author xzl
 * @date 2021-05-23 18:42
 **/
@Slf4j
public class RpcProvider implements InitializingBean, BeanPostProcessor {

    private String serverAddress;

    private final int serverPort;

    private final RegistryService registryService;

    private final Map<String, Object> rpcServiceMap = new HashMap<>(128);

    private final Map<String, FastClass> fastClassMap = new HashMap<>(128);

    public RpcProvider(int serverPort, RegistryService registryService) {
        this.serverPort = serverPort;
        this.registryService = registryService;
    }

    private void startRpcServer() {
        try {
            this.serverAddress = InetAddress.getLocalHost().getHostAddress();
            EventLoopGroup boss = new NioEventLoopGroup();
            EventLoopGroup worker = new NioEventLoopGroup();

            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap
                    .group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast(new RpcDecoder())
                                    .addLast(new RpcEncoder())
                                    .addLast(new RpcRequestHandler(rpcServiceMap, fastClassMap));
                        }
                    })
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture channelFuture = bootstrap.bind(this.serverAddress, this.serverPort).sync();
            log.info("server addr {} started on port {}", this.serverAddress, this.serverPort);
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("startRpcServer error", e);
        }

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        new Thread(this::startRpcServer, "rpcProviderStartUpThread").start();
    }


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        RpcServer rpcServer = bean.getClass().getAnnotation(RpcServer.class);
        if (Objects.nonNull(rpcServer)) {
            String serviceName = rpcServer.serviceInterface().getName();
            String serviceVersion = rpcServer.serviceVersion();
            ServiceMeta serviceMeta = new ServiceMeta();
            serviceMeta.setServiceAddr(serverAddress);
            serviceMeta.setServicePort(serverPort);
            serviceMeta.setServiceName(serviceName);
            serviceMeta.setServiceVersion(serviceVersion);
            try {
                String key = RpcServiceHelper.buildServiceKey(serviceName, serviceVersion);
                if (!rpcServiceMap.containsKey(key)) {
                    registryService.register(serviceMeta);
                    rpcServiceMap.put(key, bean);
                    fastClassMap.put(key, FastClass.create(bean.getClass()));
                }
            } catch (Exception e) {
                log.error("service register fail data:{}", serviceMeta);
            }
        }

        return bean;
    }
}
