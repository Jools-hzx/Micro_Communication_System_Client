package com.hspedu.QQ.client.Service;

import com.hspedu.QQ.client.Handlers.MessageFactory;
import com.hspedu.QQ.client.utils.QQUtils;
import com.hspedu.QQ.common.Message;
import com.hspedu.QQ.common.MessageType;
import com.hspedu.QQ.common.User;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;


/**
 * @author Zexi He.
 * @date 2022/11/17 22:44
 * @description: 该类完成用户登录验证和用户注册的功能
 */
public class UserClientService {

    private User user = new User();
    //该属性存储此用户与服务端进行通信的 Socket
    private Socket socket;

    //UserId 和密码到服务器验证该用户是否合法
    public boolean checkValidUser(String username, String password) {
        boolean result = false;
        //设置 User 对象的属性
        user.setUserId(username);
        user.setPassword(password);

        ObjectOutputStream OOS = null;
        ObjectInputStream OIS = null;

        //向服务端发送 User 对象
        try {
            socket = new Socket(InetAddress.getLocalHost(), 9999);
            OOS = new ObjectOutputStream(socket.getOutputStream());
            OOS.writeObject(user);

            //接收服务端回复的消息
            OIS = new ObjectInputStream(socket.getInputStream());
            try {
                Message message = (Message) OIS.readObject();
                if (message.getType().equals(MessageType.MESSAGE_LOGIN_SUCCEED)) {
                    result = true;
                    //创建一个与服务器端保持通信的线程类 --> ClientConnectServerThread
                    //启动客户端的线程
                    ClientConnectServerThread thread = new ClientConnectServerThread(socket);
                    new Thread(thread).start();
                    ManageClientConnectServerThread.addThreadToMap(username, thread);
                } else if (message.getType().equals(MessageType.MESSAGE_LOGIN_FAIL)) {
                    //如果登录失败了，我们就不能启动和服务器连接的线程
                    //关闭socket
                    this.socket.close();
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    //该方法用于客户端正常退出程序
    public void exitSystem() {
        //先向服务端发送退出系统，并断开连接的请求
        try {
            ObjectOutputStream OOS = new ObjectOutputStream(socket.getOutputStream());
            Message message = MessageFactory.getMessageItem(
                    user.getUserId(),
                    InetAddress.getByName("127.0.0.1").toString(),
                    MessageType.MESSAGE_EXIT_SYSTEM,
                    "客户端: 用户 " + user.getUserId() + " 下线了"
            );
            OOS.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //该方法用于客户端向服务端请求显示在线用户列表
    public void getCurrentLiveUsers() {
        //构建消息，发送给服务端
        ObjectOutputStream OOS = null;
        Message clientToServerMessage = null;
        try {
            clientToServerMessage = MessageFactory.getMessageItem(
                    user.getUserId(),
                    InetAddress.getByName("127.0.0.1").toString(),
                    MessageType.MESSAGE_CURRENT_USERS_LIST_REQUEST,
                    "\n客户端: 用户 " + user.getUserId() + " 请求显示在线用户列表"
            );
            OOS = new ObjectOutputStream(socket.getOutputStream());
            OOS.writeObject(clientToServerMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //该方法用于发起私聊
    /*
        1.构建消息类型
        2.客户端构建发送消息和接收消息的用户并通过 Socket 发送到服务端
        3.服务端通过客户端发送的消息解析sender 和 receiver
        4.服务端通过 Receiver 拿到服务端与该用户建立通信的线程，该线程持有socket
        5.通过 Socket 发送将消息发送给对应用户 Receiver
     */
    public void sendMessageToSingleUser(String receiver, String content) {
        //构建消息发送给服务端
        ObjectOutputStream OOS = null;
        Message clientToServerMessage = null;
        clientToServerMessage = MessageFactory.getMessageItem(
                user.getUserId(),
                receiver,
                MessageType.MESSAGE_TO_SINGLE_USER,
                content);
        try {
            OOS = new ObjectOutputStream(socket.getOutputStream());
            OOS.writeObject(clientToServerMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(clientToServerMessage.getSendTime() + " 你对 [" + receiver + "] 说:" + content);
    }

    //该方法用于群发
    /*
      1. 定义消息类型，群发消息 request 和 response
      2. 客户端通过与服务端相互通信的 Socket 发送消息给服务端
      3. 服务端接收到消息后，返回response 给发送方，发送方显示发送群聊成功！
      4. 遍历当前在线的用户列表
      5. 通过每个在线用户的 username 获取到与之保持通信的线程，并发送消息
      6. 所有的接收方显示接收到的消息
     */
    public void sendMessageToAllUsers(String content) {
        ObjectOutputStream OOS;
        Message clientToServerMessage;
        clientToServerMessage = MessageFactory.getMessageItem(
                user.getUserId(),
                "all",
                MessageType.MESSAGE_TO_ALL_USERS_REQUEST,
                content
        );
        try {
            OOS = new ObjectOutputStream(socket.getOutputStream());
            OOS.writeObject(clientToServerMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //该方法用于发送文件
    /*
      1.发送方；利用输入流读取目标文件；如果目标文件不存在，提示信息
      2.如果目标文件存在，利用输入流读取成一个字节数组
      3.构建消息对象，发送给服务端
      4.服务端接收到消息后；判断接收方是否为在线用户；
      5.如果是在线用户，构建消息发送给其对应的客户端并显示结果
      6.如果不是在线用户，返回拒绝结果
     */
    public void sendFileToSingleUser(String receiver, String filePath, String receivePath) {
        File file = new File(filePath);
        if (!file.exists()) {   //如果目标文件不存在，直接退出
            System.out.println("该文件不存在，请重新输入");
            return;
        }
        //读取文件并得到字节数组
        byte[] bytes = QQUtils.readFileAndConverterToByteArray(file);
        Message clientToServerMessage;
        //构建消息对象发送至服务端
        clientToServerMessage = MessageFactory.getMessageItem(
                user.getUserId(),
                receiver,
                MessageType.MESSAGE_FILE_TRANSPORT_REQUEST,
                "destPath," + receivePath
        );
        //持有字节数组
        clientToServerMessage.setBytes(bytes);
        //发送至客户端
        ObjectOutputStream OOS;
        try {
            OOS = new ObjectOutputStream(socket.getOutputStream());
            OOS.writeObject(clientToServerMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
