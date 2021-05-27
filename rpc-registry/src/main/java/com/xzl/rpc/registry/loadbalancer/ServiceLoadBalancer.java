package com.xzl.rpc.registry.loadbalancer;

import java.util.List;

/**
 * @author xzl
 * @date 2021-05-26 21:54
 **/
public interface ServiceLoadBalancer<T> {
    T select(List<T> service, int hashCode);
}
