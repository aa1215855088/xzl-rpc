package com.xzl.rpc.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author xzl
 * @date 2021-05-24 22:30
 **/
@Data
public class RpcResponse implements Serializable {
    private Object data;
    private String message;
}
