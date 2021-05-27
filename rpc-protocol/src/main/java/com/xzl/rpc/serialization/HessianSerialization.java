package com.xzl.rpc.serialization;

import com.caucho.hessian.io.HessianSerializerInput;
import com.caucho.hessian.io.HessianSerializerOutput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

/**
 * @author xzl
 * @date 2021-05-24 22:36
 **/
public class HessianSerialization implements RpcSerialization {


    @Override
    public <T> byte[] serialize(T obj) {
        if (Objects.isNull(obj)) {
            throw new NullPointerException();
        }
        byte[] body;
        HessianSerializerOutput hessianOutput;
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            hessianOutput = new HessianSerializerOutput(os);
            hessianOutput.writeObject(obj);
            hessianOutput.flush();
            body = os.toByteArray();
        } catch (Exception e) {
            //
            return null;
        }
        return body;
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public <T> T deserialize(byte[] data, Class<T> clz) {
        if (data == null || clz == null) {
            throw new NullPointerException();
        }
        T result;

        try (ByteArrayInputStream is = new ByteArrayInputStream(data)) {
            HessianSerializerInput hessianSerializerInput = new HessianSerializerInput(is);
            result = (T) hessianSerializerInput.readObject(clz);
        } catch (IOException e) {
            //
            return null;
        }
        return result;
    }
}
