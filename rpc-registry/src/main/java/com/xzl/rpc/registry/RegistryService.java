package com.xzl.rpc.registry;

import com.xzl.rpc.common.ServiceMeta;

import java.io.IOException;

/**
 * @author xzl
 * @date 2021-05-23 19:07
 **/
public interface RegistryService {
    void register(ServiceMeta serviceMeta) throws Exception;

    void unRegister(ServiceMeta serviceMeta) throws Exception;

    ServiceMeta discovery(String serviceName, int invokerHashCode) throws Exception;

    void destroy() throws IOException;
}
