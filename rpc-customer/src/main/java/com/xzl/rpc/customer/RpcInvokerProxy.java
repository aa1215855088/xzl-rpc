package com.xzl.rpc.customer;

import com.xzl.rpc.registry.RegistryService;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author xzl
 * @date 2021-05-24 21:51
 **/
public class RpcInvokerProxy  implements InvocationHandler {


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
        return null;
    }
}
