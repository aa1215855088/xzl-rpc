package com.xzl.rpc.provider;

import com.xzl.rpc.common.RpcProperties;
import com.xzl.rpc.registry.RegistryFactory;
import com.xzl.rpc.registry.RegistryService;
import com.xzl.rpc.registry.RegistryType;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author xzl
 * @date 2021-05-23 20:48
 **/
@Configuration
@EnableConfigurationProperties({RpcProperties.class})
public class RpcProviderAutoConfiguration {

    @Resource
    private RpcProperties rpcProperties;

    @Bean
    public RpcProvider init() {
        RegistryType type = RegistryType.valueOf(rpcProperties.getRegistryType());
        RegistryService registryService = RegistryFactory.getInstance(rpcProperties.getRegistryAddr(), type);
        return new RpcProvider(rpcProperties.getServicePort(), registryService);
    }
}
