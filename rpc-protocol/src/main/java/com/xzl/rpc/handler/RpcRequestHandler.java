package com.xzl.rpc.handler;

import com.xzl.rpc.common.RpcRequest;
import com.xzl.rpc.common.RpcResponse;
import com.xzl.rpc.common.RpcServiceHelper;
import com.xzl.rpc.protocol.*;
import com.xzl.rpc.serialization.SerializationTypeEnum;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.reflect.FastClass;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * @author xzl
 * @date 2021-05-24 23:14
 **/
@Slf4j
public class RpcRequestHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcRequest>> {

    private final Map<String, Object> rpcServiceMap;

    public RpcRequestHandler(Map<String, Object> rpcServiceMap) {
        this.rpcServiceMap = rpcServiceMap;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcProtocol<RpcRequest> protocol) throws Exception {
        RpcRequestProcessor.submitRequest(() -> {
            RpcProtocol<RpcResponse> rpcProtocol = new RpcProtocol<>();
            RpcResponse response = new RpcResponse();
            MsgHeader header = protocol.getHeader();
            header.setMsgType((byte) MsgType.RESPONSE.getType());
            try {
                Object result = handle(protocol.getBody());
                response.setData(result);
                header.setStatus((byte) MsgStatus.SUCCESS.getCode());
                rpcProtocol.setHeader(header);
                rpcProtocol.setBody(response);
            } catch (Exception e) {
                header.setStatus((byte) MsgStatus.FAIL.getCode());
                response.setMessage(e.toString());
                log.error("process request {} error", header.getRequestId(), e);
            }
            channelHandlerContext.writeAndFlush(rpcProtocol).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("send success");
                } else {
                    log.error("send error");
                }
            });
        });
    }

    private Object handle(RpcRequest body) throws InvocationTargetException {
        String serviceKey = RpcServiceHelper.buildServiceKey(body.getClassName(), body.getServiceVersion());
        Object serviceBean = rpcServiceMap.get(serviceKey);
        if (serviceBean == null) {
            throw new RuntimeException(String.format("service not exist: %s:%s", body.getClassName(), body.getMethodName()));
        }
        Class<?> serviceClass = serviceBean.getClass();
        String methodName = body.getMethodName();
        Class<?>[] parameterTypes = body.getParameterTypes();
        Object[] params = body.getParams();

        FastClass fastClass = FastClass.create(serviceClass);

        int index = fastClass.getIndex(methodName, parameterTypes);

        return fastClass.invoke(index, serviceBean, params);
    }
}
