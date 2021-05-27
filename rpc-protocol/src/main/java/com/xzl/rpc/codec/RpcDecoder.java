package com.xzl.rpc.codec;

import com.xzl.rpc.common.RpcRequest;
import com.xzl.rpc.common.RpcResponse;
import com.xzl.rpc.protocol.MsgHeader;
import com.xzl.rpc.protocol.MsgType;
import com.xzl.rpc.protocol.ProtocolConstants;
import com.xzl.rpc.protocol.RpcProtocol;
import com.xzl.rpc.serialization.RpcSerialization;
import com.xzl.rpc.serialization.SerializationFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author xzl
 * @date 2021-05-24 22:46
 **/
public class RpcDecoder extends ByteToMessageDecoder {
    /**
     * +---------------------------------------------------------------+
     * | 魔数 2byte | 协议版本号 1byte | 序列化算法 1byte | 报文类型 1byte  |
     * +---------------------------------------------------------------+
     * | 状态 1byte |        消息 ID 8byte     |      数据长度 4byte     |
     * +---------------------------------------------------------------+
     * |                   数据内容 （长度不定）                          |
     * +---------------------------------------------------------------+
     */
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> out) throws Exception {
        if (byteBuf.readableBytes() < ProtocolConstants.HEADER_TOTAL_LEN) {
            return;
        }
        byteBuf.markReaderIndex();

        short magic = byteBuf.readShort();
        if (magic != ProtocolConstants.MAGIC) {
            throw new IllegalArgumentException("magic number is illegal," + magic);
        }

        byte version = byteBuf.readByte();
        byte serializeType = byteBuf.readByte();
        byte msgType = byteBuf.readByte();
        byte status = byteBuf.readByte();
        long requestId = byteBuf.readLong();

        int dataLength = byteBuf.readInt();
        if (byteBuf.readableBytes() < dataLength) {
            byteBuf.resetReaderIndex();
            return;
        }
        byte[] dataBytes = new byte[dataLength];
        byteBuf.readBytes(dataBytes);
        MsgType msgTypeEnum = MsgType.findByType(msgType);
        if (msgTypeEnum == null) {
            return;
        }
        MsgHeader msgHeader = new MsgHeader();
        msgHeader.setMagic(magic);
        msgHeader.setSerialization(serializeType);
        msgHeader.setMsgType(msgType);
        msgHeader.setStatus(status);
        msgHeader.setRequestId(requestId);
        RpcSerialization rpcSerialization = SerializationFactory.getRpcSerialization(serializeType);

        switch (msgTypeEnum) {
            case REQUEST:
                RpcRequest request = rpcSerialization.deserialize(dataBytes, RpcRequest.class);
                if (request != null) {
                    RpcProtocol<RpcRequest> protocol = new RpcProtocol<>();
                    protocol.setHeader(msgHeader);
                    protocol.setBody(request);
                    out.add(protocol);
                }
                break;
            case RESPONSE:
                RpcResponse response = rpcSerialization.deserialize(dataBytes, RpcResponse.class);
                if (response != null) {
                    RpcProtocol<RpcResponse> protocol = new RpcProtocol<>();
                    protocol.setHeader(msgHeader);
                    protocol.setBody(response);
                    out.add(protocol);
                }
                break;
            case HEARTBEAT:
                break;
            default:
        }
    }
}
