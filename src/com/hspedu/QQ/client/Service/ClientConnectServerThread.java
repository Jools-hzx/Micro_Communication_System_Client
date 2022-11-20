package com.hspedu.QQ.client.Service;

import com.hspedu.QQ.client.Handlers.TransportMessageHandler;
import com.hspedu.QQ.common.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author Zexi He.
 * @date 2022/11/17 23:02
 * @description:
 */
public class ClientConnectServerThread implements Runnable {


    //持有一个 Socket 属性
    private Socket socket = null;
    //该属性持有一个消息处理对象
    private TransportMessageHandler messageHandler;

    public ClientConnectServerThread(Socket socket) {
        this.socket = socket;
        this.messageHandler = new TransportMessageHandler();
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        //因为 Thread 需要在后台和服务器进行通信，因此我们需要使用 while 循环
        ObjectInputStream OIS = null;
        while (!socket.isClosed()) {
            //读取服务端回复的消息
            try {
                OIS = new ObjectInputStream(socket.getInputStream());
                //如果服务器没有发送消息对象，线程为阻塞在这里
                Message message = (Message) OIS.readObject();
                //根据消息对象的消息类属性，进行后续操作
                messageHandler.handlerMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //该方法用于终止该线程
    public void shutDown() {
        try {
            getSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
