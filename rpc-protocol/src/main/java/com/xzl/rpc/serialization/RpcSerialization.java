package com.xzl.rpc.serialization;

import java.io.IOException;

/**
 * @author xzl
 * @date 2021-05-24 22:33
 **/
public interface RpcSerialization {

    <T> byte[] serialize(T obj) throws IOException;

    <T> T deserialize(byte[] data, Class<T> clz) throws IOException;
}
