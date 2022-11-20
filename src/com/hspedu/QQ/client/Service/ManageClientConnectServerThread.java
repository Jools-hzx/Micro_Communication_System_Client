package com.hspedu.QQ.client.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Zexi He.
 * @date 2022/11/17 23:11
 * @description:    管理客户端连接到服务端的线程的集合
 */
public class ManageClientConnectServerThread {

    //该Map 存储连接到服务端的线程，key 为 username, value 为一个线程
    private static Map<String, ClientConnectServerThread> map = new HashMap<>();

    //将某个线程加入到集合中
    public static void addThreadToMap(String userId, ClientConnectServerThread thread) {
        map.put(userId, thread);
    }

    //将某个线程从集合中删除
    public static void removeThreadFromMap(String userId) {
        map.remove(userId);
    }

    //获取一个线程，通过一个 username
    public static ClientConnectServerThread getThreadByUserId(String userId) {
        return map.get(userId);
    }
}
