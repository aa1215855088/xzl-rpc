package com.xzl.rpc.protocol;

import lombok.Data;

import java.io.Serializable;

/**
 * @author xzl
 * @date 2021-05-24 21:54
 **/
@Data
public class RpcProtocol<T> implements Serializable {

    private MsgHeader header;

    private T body;
}
