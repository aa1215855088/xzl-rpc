package com.xzl.rpc.customer;

import com.xzl.rpc.common.RpcFuture;
import com.xzl.rpc.common.RpcRequest;
import com.xzl.rpc.common.RpcRequestHolder;
import com.xzl.rpc.common.RpcResponse;
import com.xzl.rpc.protocol.MsgHeader;
import com.xzl.rpc.protocol.MsgType;
import com.xzl.rpc.protocol.ProtocolConstants;
import com.xzl.rpc.protocol.RpcProtocol;
import com.xzl.rpc.registry.RegistryService;
import com.xzl.rpc.serialization.SerializationTypeEnum;
import io.netty.channel.DefaultEventLoop;
import io.netty.util.concurrent.DefaultPromise;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @author xzl
 * @date 2021-05-24 21:51
 **/
public class RpcInvokerProxy implements InvocationHandler {


    private final String serviceVersion;

    private final long timeout;

    private final RegistryService registryService;


    public RpcInvokerProxy(String serviceVersion, long timeout, RegistryService registryService) {
        this.serviceVersion = serviceVersion;
        this.timeout = timeout;
        this.registryService = registryService;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcProtocol<RpcRequest> protocol = new RpcProtocol<>();
        MsgHeader msgHeader = new MsgHeader();
        long requestId = RpcRequestHolder.REQUEST_ID_GET.incrementAndGet();
        msgHeader.setMagic(ProtocolConstants.MAGIC);
        msgHeader.setVersion(ProtocolConstants.VERSION);
        msgHeader.setRequestId(requestId);
        msgHeader.setSerialization((byte) SerializationTypeEnum.HESSIAN.getType());
        msgHeader.setMsgType((byte) MsgType.REQUEST.getType());
        msgHeader.setStatus((byte) 0x1);
        protocol.setHeader(msgHeader);

        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setServiceVersion(this.serviceVersion);
        rpcRequest.setClassName(method.getDeclaringClass().getName());
        rpcRequest.setMethodName(method.getName());
        rpcRequest.setParameterTypes(method.getParameterTypes());
        rpcRequest.setParams(args);
        protocol.setBody(rpcRequest);

        RpcConsumer rpcConsumer = new RpcConsumer();
        RpcFuture<RpcResponse> future = new RpcFuture<>(new DefaultPromise<>(new DefaultEventLoop()), timeout);
        RpcRequestHolder.REQUEST_MAP.put(requestId, future);
        rpcConsumer.sendRequest(protocol, registryService);
        return future.getPromise().get(future.getTimeout(), TimeUnit.MILLISECONDS).getData();
    }
}
