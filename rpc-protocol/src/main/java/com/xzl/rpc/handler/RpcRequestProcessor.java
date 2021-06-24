package com.xzl.rpc.handler;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author xzl
 * @date 2021-05-24 23:07
 **/
public class RpcRequestProcessor {
    private static final ThreadPoolExecutor POOL;

    private static final AtomicInteger THREAD_NUM = new AtomicInteger(0);

    static {
        int processors = Runtime.getRuntime().availableProcessors();
        POOL = new ThreadPoolExecutor(processors * 2, processors * 2, 1, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100000),
                r -> new Thread(r, "businessThread-" + THREAD_NUM.getAndIncrement()));
    }

    public static void submitRequest(Runnable task) {
        POOL.submit(task);
    }
}
