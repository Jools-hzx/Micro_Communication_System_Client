package com.hspedu.QQ.client.utils;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/**
 * @author Zexi He.
 * @date 2022/11/17 20:55
 * @description:
 */
public class QQUtils {

    //该属性用于接收用户输入
    private static Scanner scanner = new Scanner(System.in);

    public static Scanner getScanner() {
        return scanner;
    }

    //该方法用于接收用户输入，如果输入为空，则返回默认值
    public static String getInputStringOrDefault(String input, String defaultValue) {
        if (!"".equals(input)) {
            return input;
        } else {
            return defaultValue;
        }
    }

    //该方法用于接收限定字符串长度的输入
    public static String readInputStringByLimited(String input, int limitLength) {
        if (input.length() > limitLength) {
            return "";
        } else {
            return input;
        }
    }

    //该方法用于返回当前的时间
    public static String getCurrentTime() {
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("HH:mm");
        return pattern.format(time);
    }

    //该方法用于读取文件并转化成一个字节数组
    public static byte[] readFileAndConverterToByteArray(File file) {
        byte[] data = null;

        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            int len;
            byte[] buff = new byte[1024];
            while ((len = fileInputStream.read(buff)) != -1) {
                byteArrayOutputStream.write(buff, 0, len);
            }

            data = byteArrayOutputStream.toByteArray();
            fileInputStream.close();
            byteArrayOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}
