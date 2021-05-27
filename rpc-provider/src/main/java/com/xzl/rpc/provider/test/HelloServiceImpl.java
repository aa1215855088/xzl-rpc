package com.xzl.rpc.provider.test;

import com.xzl.rpc.facade.test.HelloService;
import com.xzl.rpc.provider.annotation.RpcServer;

/**
 * @author xzl
 * @date 2021-05-27 21:39
 **/
@RpcServer(serviceInterface = HelloService.class)
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(String name) {
        return String.join(",", "hello", name);
    }
}
