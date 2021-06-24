package com.xzl.rpc.registry;

import com.xzl.rpc.common.ServiceMeta;

/**
 * @author xzl
 * @date 2021-05-23 20:56
 **/
public class EurekaRegistryServiceImpl implements RegistryService {

    @Override
    public void register(ServiceMeta serviceMeta) {

    }

    @Override
    public void unRegister(ServiceMeta serviceMeta) {

    }

    @Override
    public ServiceMeta discovery(String serviceName, int invokerHashCode) {
        return null;
    }

    @Override
    public void destroy() {

    }
}
