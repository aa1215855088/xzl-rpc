package com.xzl.rpc.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author xzl
 * @date 2021-05-24 22:29
 **/
@Data
public class RpcRequest implements Serializable {
    private String serviceVersion;
    private String className;
    private String methodName;
    private Object[] params;
    private Class<?>[] parameterTypes;
}
