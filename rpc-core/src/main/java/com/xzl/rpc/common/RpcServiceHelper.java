package com.xzl.rpc.common;

/**
 * @author xzl
 * @date 2021-05-24 21:38
 **/
public class RpcServiceHelper {


    public static String buildServiceKey(String serviceName, String serviceVersion) {
        return String.join("#", serviceName, serviceVersion);
    }
}
