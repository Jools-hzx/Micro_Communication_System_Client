package com.hspedu.QQ.client.Handlers;

import com.hspedu.QQ.client.Service.ClientConnectServerThread;
import com.hspedu.QQ.client.Service.ManageClientConnectServerThread;
import com.hspedu.QQ.common.Message;
import com.hspedu.QQ.common.MessageType;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Zexi He.
 * @date 2022/11/19 11:22
 * @description: 该类用于处理从服务端接收到的 Message 对象
 */
public class TransportMessageHandler {

    public void handlerMessage(Message serverToClientMessage) {
        String type = serverToClientMessage.getType();
        //根据消息类型进行对应操作
        if (MessageType.MESSAGE_CONFIRM_EXIT.equals(type)) {
            //客户端退出
            handleClientLoginOut(serverToClientMessage);
        } else if (MessageType.MESSAGE_CURRENT_USERS_LIST_RESPONSE.equals(type)) {
            handleShowActiveList(serverToClientMessage);
        } else if (MessageType.MESSAGE_TO_SINGLE_USER_ACCEPTED.equals(type)) {
            //处理发送消息成功
            handleServerAcceptedResponse(serverToClientMessage);
        } else if (MessageType.MESSAGE_TO_SINGLE_USER_FAILED.equals(type)) {
            //处理发送消息失败
            handleServerRejectedResponse(serverToClientMessage);
        } else if (MessageType.MESSAGE_TO_SINGLE_USER.equals(type)) {
            //处理发送方发送的消息
            handleReceiveMessageFromSender(serverToClientMessage);
        } else if (MessageType.MESSAGE_TO_ALL_USERS_RESPONSE.equals(type)) {
            //处理群发消息的回复
            handleReceiveMessageFromSender(serverToClientMessage);
        } else if (MessageType.MESSAGE_TO_ALL_USERS_ACCEPTED.equals(type)) {
            //处理群发消息成功
            handleServerAcceptedResponse(serverToClientMessage);
        } else if (MessageType.MESSAGE_TO_ALL_USERS_REJECTED.equals(type)) {
            //处理群发消息失败
            handleServerRejectedResponse(serverToClientMessage);
        } else if (MessageType.MESSAGE_FILE_TRANSPORT_RESPONSE.equals(type)) {
            //处理文件传输消息
            handleReceiveFile(serverToClientMessage);
        } else if (MessageType.MESSAGE_FILE_TRANSPORT_ACCEPTED.equals(type)) {
            //处理文件传输成功
            handleServerAcceptedResponse(serverToClientMessage);
        } else if (MessageType.MESSAGE_FILE_TRANSPORT_REJECTED.equals(type)) {
            //处理传输文件失败
            handleServerRejectedResponse(serverToClientMessage);
        }
    }

    //该方法用于处理服务端成功接收操作后的回复
    private void handleServerAcceptedResponse(Message serverToClientMessage) {
        System.out.println("\n" + serverToClientMessage.getContent());
    }

    //该方法用于处理服务端拒绝接收操作后的回复
    private void handleServerRejectedResponse(Message serverToClientMessage) {
        System.out.println("\n" + serverToClientMessage.getContent());
    }

    //该方法用于处理客户端接收到聊天消息
    private void handleReceiveMessageFromSender(Message serverToClientMessage) {
        String sender = serverToClientMessage.getSender();
        String time = serverToClientMessage.getSendTime();
        System.out.println("\n" + time + "  " + sender + " 对你说: " + serverToClientMessage.getContent());
    }

    //该方法用于处理客户端请求退出
    private void handleClientLoginOut(Message serverToClientMessage) {
        ClientConnectServerThread thread = ManageClientConnectServerThread.getThreadByUserId(serverToClientMessage.getReceiver());
        //调用线程的终止方法
        thread.shutDown();
        //从集合中移除该线程
        ManageClientConnectServerThread.removeThreadFromMap(serverToClientMessage.getReceiver());
    }

    private void handleShowActiveList(Message serverToClientMessage) {
        //显示内容
        int count = 1;
        String[] list = serverToClientMessage.getContent().split(",");
        System.out.println("\n---- 当前在线用户列表 ----");
        for (String s : list) {
            System.out.println("\t" + (count++) + " --> " + s);
        }
        System.out.println("-----------------------\n");
    }


    //该方法用于接收别的用户发送文件
    private void handleReceiveFile(Message serverToClientMessage) {
        byte[] bytes = serverToClientMessage.getBytes();
        String destPath = serverToClientMessage.getContent();
        BufferedOutputStream BOS = null;
        try {
            BOS = new BufferedOutputStream(new FileOutputStream(destPath));
            BOS.write(bytes);
            BOS.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (BOS != null) {
                try {
                    BOS.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println(serverToClientMessage.getSendTime() + "   [" + serverToClientMessage.getSender() + "] 向你发送了文件, 文件保存到" + destPath);
    }
}
