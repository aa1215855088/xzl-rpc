package com.xzl.rpc.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author xzl
 * @date 2021-05-23 20:46
 **/
@Data
@ConfigurationProperties(prefix = "rpc")
public class RpcProperties {

    private int servicePort;

    private String registryAddr;

    private String registryType;
}
