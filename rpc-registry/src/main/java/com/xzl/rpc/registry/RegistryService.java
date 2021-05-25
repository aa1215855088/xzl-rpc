package com.xzl.rpc.registry;

import com.xzl.rpc.common.ServiceMeta;

/**
 * @author xzl
 * @date 2021-05-23 19:07
 **/
public interface RegistryService {
    void register(ServiceMeta serviceMeta);
}
