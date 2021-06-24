package com.xzl.rpc.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @author xzl
 * @date 2021-05-24 21:45
 **/
@EnableConfigurationProperties
@SpringBootApplication
public class RpcCustomerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RpcCustomerApplication.class, args);
    }

}
