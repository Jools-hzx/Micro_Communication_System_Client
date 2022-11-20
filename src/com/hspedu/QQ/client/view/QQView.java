package com.hspedu.QQ.client.view;

import com.hspedu.QQ.client.Service.UserClientService;
import com.hspedu.QQ.client.utils.QQUtils;
import org.omg.CORBA.PRIVATE_MEMBER;

import java.util.Scanner;

/**
 * @author Zexi He.
 * @date 2022/11/17 20:49
 * @description:
 */
public class QQView {

    //该属性用于控制循环
    private boolean loop = true;
    //该属性用于接收用户输入
    private String key = "";
    private Scanner scanner = QQUtils.getScanner();
    //获取 Service 用于登录服务器
    private UserClientService service = new UserClientService();

    public void init() {
        showMainMenu();
    }

    //显示主菜单
    private void showMainMenu() {
        while (loop) {
            System.out.println("============== QQ 登录界面 =============");
            System.out.println("\t\t 1 登录系统");
            System.out.println("\t\t 2 退出系统");
            System.out.print("请输入你的选择(1/2):");

            //使用工具类接收用户的输入
            String input = QQUtils.getScanner().next();
            key = QQUtils.getInputStringOrDefault(input, "-1");
            //switch 语句判断用户输入
            switch (key) {
                case "1":
                    //登录系统
                    System.out.println("\n============== 登陆系统 =============");
                    System.out.print("请输入你的用户名:");
                    String username = QQUtils.readInputStringByLimited(scanner.next(), 10);
                    System.out.print("请输入你的密码:");
                    String pwd = QQUtils.readInputStringByLimited(scanner.next(), 20);

                    //验证登录
                    boolean valid = service.checkValidUser(username, pwd);
                    if (valid) {
                        //登录验证成功；进入二级菜单
                        System.out.println("\n============== 欢迎 (用户:" + username + ") 登陆系统 =============");
                        showSecondMenu();
                    } else {
                        //如果登录验证失败
                        System.out.println("======= 登录失败！请重新登录=======\n ");
                    }
                    break;
                case "2":
                    System.out.println("退出系统");
                    loop = false;
                    break;
                default:
                    System.out.println("输入有误，请重新输入！");
                    break;
            }
        }
    }

    //该方法用于显示二级菜单
    private void showSecondMenu() {
        while (loop) {
            System.out.println("============== QQ 登录界面(二级菜单) =============");
            System.out.println("\t\t 1 显示在线用户列表");
            System.out.println("\t\t 2 群发消息");
            System.out.println("\t\t 3 私聊消息");
            System.out.println("\t\t 4 发送文件");
            System.out.println("\t\t 9 退出系统");
            System.out.print("请输入你的选择:");
            key = QQUtils.getInputStringOrDefault(scanner.next(), "-1");
            //判断用户输入
            getUserKeyInSecondMenu(key);
        }
    }

    //该方法用于判断用户在二级菜单中的输入
    private void getUserKeyInSecondMenu(String key) {

        switch (key) {
            case "1":
                //显示在线用户列表
                service.getCurrentLiveUsers();
                break;
            case "2":
                System.out.println("------ 进行群发 ------");
                System.out.print("请输入你要群发的内容:");
                String contentToAll = scanner.next();
                service.sendMessageToAllUsers(contentToAll);
                break;
            case "3":
                System.out.println("------ 进行私聊 ------");
                System.out.print("接收方的用户名:");
                String receiver = scanner.next();
                System.out.print("请输入你想发送的内容:");
                String content = scanner.next();
                service.sendMessageToSingleUser(receiver, content);
                break;
            case "4":
                System.out.print("请输入接收方的用户名:");
                String fileReceiver = scanner.next();
                System.out.print("请输入你想要发送的文件路径:");
                String filePath = scanner.next();
                System.out.print("请输入对方接收的路径:");
                String receiverPath = scanner.next();
                service.sendFileToSingleUser(fileReceiver, filePath, receiverPath);
                break;
            case "9":
                System.out.println("--- 退出系统中 ----");
                //添加正常退出逻辑
                service.exitSystem();
                System.out.println("---- 离线成功 ----");
                loop = false;
                break;
            default:
                System.out.println("输入有误！请重新输入！");
                break;
        }
    }
}
