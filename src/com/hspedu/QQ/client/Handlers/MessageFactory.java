package com.hspedu.QQ.client.Handlers;

import com.hspedu.QQ.client.utils.QQUtils;
import com.hspedu.QQ.common.Message;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Zexi He.
 * @date 2022/11/19 13:49
 * @description: 该方法用于构建由客户端发送给服务端的消息
 */
public class MessageFactory {

    public static Message getMessageItem(String sender, String receiver, String messageType, String content) {
        return new Message(
                sender,
                receiver,
                content,
                QQUtils.getCurrentTime(),
                messageType);
    }
}
