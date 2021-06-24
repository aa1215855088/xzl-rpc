package com.xzl.rpc.customer.test;

import com.xzl.rpc.customer.annotation.RpcReference;
import com.xzl.rpc.facade.test.HelloService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xzl
 * @date 2021-05-27 21:41
 **/
@RestController
public class HelloController {

    @SuppressWarnings({"SpringJavaAutowiredFieldsWarningInspection", "SpringJavaInjectionPointsAutowiringInspection"})
    @RpcReference
    private HelloService helloService;

    @GetMapping("/hello/{name}")
    public String hello(@PathVariable String name) {
        return this.helloService.hello(name);
    }
}
