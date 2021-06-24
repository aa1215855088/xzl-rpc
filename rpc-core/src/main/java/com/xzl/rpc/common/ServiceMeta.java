package com.xzl.rpc.common;

import lombok.Data;
import org.omg.CORBA.ORB;

/**
 * @author xzl
 * @date 2021-05-24 21:32
 **/
@Data
public class ServiceMeta {

    private String serviceName;

    private String serviceVersion;

    private String serviceAddr;

    private int servicePort;
}
