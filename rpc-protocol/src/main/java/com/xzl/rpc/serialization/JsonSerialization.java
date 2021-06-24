//package com.xzl.rpc.serialization;
//
//import com.fasterxml.jackson.annotation.JsonInclude;
//import com.fasterxml.jackson.databind.DeserializationFeature;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.text.SimpleDateFormat;
//import java.util.Arrays;
//
///**
// * @author xzl
// * @date 2021-05-24 22:41
// **/
//public class JsonSerialization implements RpcSerialization {
//
//    private static final ObjectMapper MAPPER;
//
//    static {
//        MAPPER = generateMapper(JsonInclude.Include.ALWAYS);
//    }
//
//    private static ObjectMapper generateMapper(JsonInclude.Include always) {
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.setSerializationInclusion(always);
//        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        objectMapper.configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, false);
//        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
//        return objectMapper;
//    }
//
//    @Override
//    public <T> byte[] serialize(T obj) throws IOException {
//        return obj instanceof String ? ((String) obj).getBytes() : MAPPER.writeValueAsString(obj).getBytes(StandardCharsets.UTF_8);
//    }
//
//    @Override
//    public <T> T deserialize(byte[] data, Class<T> clz) throws IOException {
//        return MAPPER.readValue(Arrays.toString(data), clz);
//    }
//}
