package com.xzl.rpc.handler;

import com.xzl.rpc.common.RpcFuture;
import com.xzl.rpc.common.RpcRequestHolder;
import com.xzl.rpc.common.RpcResponse;
import com.xzl.rpc.protocol.RpcProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author xzl
 * @date 2021-05-27 21:25
 **/
public class RpcResponseHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcResponse>> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcProtocol<RpcResponse> protocol) throws Exception {
        long requestId = protocol.getHeader().getRequestId();
        RpcFuture<RpcResponse> future = RpcRequestHolder.REQUEST_MAP.get(requestId);
        future.getPromise().setSuccess(protocol.getBody());
    }
}
