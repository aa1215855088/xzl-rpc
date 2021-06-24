package com.xzl.rpc.protocol;

import lombok.Getter;

/**
 * @author xzl
 * @date 2021-05-24 21:56
 **/
public enum MsgStatus {
    SUCCESS(0),
    FAIL(1);
    @Getter
    private final int code;
    MsgStatus(int code) {
        this.code = code;
    }
}
