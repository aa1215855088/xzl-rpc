package com.xzl.rpc.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author xzl
 * @date 2021-05-26 23:03
 **/
public class RpcRequestHolder {
    public static final AtomicLong REQUEST_ID_GET = new AtomicLong(1000);
    public static final Map<Long, RpcFuture<RpcResponse>> REQUEST_MAP = new ConcurrentHashMap<>();
}
