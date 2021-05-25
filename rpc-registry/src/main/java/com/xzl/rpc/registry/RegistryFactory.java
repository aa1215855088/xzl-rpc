package com.xzl.rpc.registry;

import java.util.Objects;

/**
 * @author xzl
 * @date 2021-05-23 20:54
 **/
public class RegistryFactory {

    private static volatile RegistryService registryService;

    public static RegistryService getInstance(String registryAddr, RegistryType type) {
        if (Objects.isNull(registryService)) {
            synchronized (RegistryService.class) {
                if (Objects.isNull(registryService)) {
                    switch (type) {
                        case EUREKA:
                            registryService = new EurekaRegistryServiceImpl();
                            break;
                        case ZOOKEEPER:
                            registryService = new ZookeeperRegistryServiceImpl();
                            break;
                        default:
                            return null;
                    }
                }
            }
        }
        return registryService;
    }
}
