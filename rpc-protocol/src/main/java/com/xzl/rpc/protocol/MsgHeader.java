package com.xzl.rpc.protocol;

import lombok.Data;

import java.io.Serializable;

/**
 * @author xzl
 * @date 2021-05-24 21:54
 **/
@Data
public class MsgHeader implements Serializable {

    private short magic;
    private byte version;
    private byte serialization;
    private byte msgType;
    private byte status;
    private long requestId;
    private int msgLen;

}
