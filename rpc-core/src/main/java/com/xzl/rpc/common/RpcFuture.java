package com.xzl.rpc.common;

import io.netty.util.concurrent.Promise;
import lombok.Data;

/**
 * @author xzl
 * @date 2021-05-24 23:16
 **/
@Data
public class RpcFuture<T> {
    private Promise<T> promise;
    private long timeout;

    public RpcFuture(Promise<T> promise, long timeout) {
        this.promise = promise;
        this.timeout = timeout;
    }
}
