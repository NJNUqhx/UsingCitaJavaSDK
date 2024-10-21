package com.pojo.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;
import java.util.UUID;

public class Evidence {
    String uuId;
    String businessType;
    String businessId;
    String businessTime;
    String businessData;

    // 空构造函数
    public Evidence() {
    }

    // 构造函数，接受5个BigInteger类型参数
    public Evidence(String uuId, String businessType, String businessId, String businessTime, String businessData) {
        this.uuId = uuId;              // 将BigInteger转换为String类型
        this.businessType = businessType;
        this.businessId = businessId;
        this.businessTime = businessTime;  // 格式化时间
        this.businessData = businessData;
    }

    // 将BigInteger格式的时间转换为yyyy-MM-dd HH:mm:ss格式
    private String formatBusinessTime(BigInteger businessTime) {
        // 假设businessTime为20000630004617形式的BigInteger
        String timeStr = businessTime.toString();
        String year = timeStr.substring(0, 4);
        String month = timeStr.substring(4, 6);
        String day = timeStr.substring(6, 8);
        String hour = timeStr.substring(8, 10);
        String minute = timeStr.substring(10, 12);
        String second = timeStr.substring(12, 14);

        // 返回格式化后的时间字符串
        return year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
    }

    // 成员函数：生成所有字段的随机值
    public void generateRandomValues() {
        this.uuId = generateRandomUUID();
        this.businessType = generateRandomBusinessType();
        this.businessId = generateRandomBusinessId();
        this.businessTime = generateRandomBusinessTime();
        this.businessData = generateRandomBusinessData();
    }

    // 成员函数：生成随机UUID
    public String generateRandomUUID() {
        String uuid = UUID.randomUUID().toString(); // 生成标准的 UUID，包括破折号
        return uuid.length() > 30 ? uuid.substring(0, 30) : uuid; // 截断到30个字符以内
    }

    // 成员函数：生成随机业务类型
    public String generateRandomBusinessType() {
        return generateRandomString(10); // 随机生成10个字符的业务类型
    }

    // 成员函数：生成随机业务ID
    public String generateRandomBusinessId() {
        return generateRandomString(8); // 随机生成8个字符的业务ID
    }

    // 成员函数：生成随机业务时间
    public String generateRandomBusinessTime() {
        Random random = new Random();
        int year = random.nextInt(2023 - 2000 + 1) + 2000; // 随机生成年份
        int month = random.nextInt(12) + 1; // 随机生成月份
        int day = 1;
        if (month == 2) { // 二月
            if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) { // 闰年
                day = random.nextInt(29) + 1; // 闰年的二月有29天
            } else {
                day = random.nextInt(28) + 1; // 平年的二月有28天
            }
        } else if (month == 4 || month == 6 || month == 9 || month == 11) { // 30天的月份
            day = random.nextInt(30) + 1;
        } else { // 31天的月份
            day = random.nextInt(31) + 1;
        }
        int hour = random.nextInt(24); // 随机生成小时
        int minute = random.nextInt(60); // 随机生成分钟
        int second = random.nextInt(60); // 随机生成秒

        return String.format("%04d-%02d-%02d %02d:%02d:%02d", year, month, day, hour, minute, second); // 格式：yyyy-MM-dd'T'HH:mm:ss
    }

    // 成员函数：生成随机业务数据
    public String generateRandomBusinessData() {
        return generateRandomString(20); // 随机生成20个字符的业务数据
    }

    // 成员函数：随机生成指定长度的字符串
    public String generateRandomString(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    // 将Evidence写入指定文件
    public void writeToFile(String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(this.toString());
            writer.newLine(); // 换行
        } catch (IOException e) {
            e.printStackTrace(); // 打印错误信息
        }
    }

    @Override
    public String toString() {
        return "Evidence{" +
                "uuId='" + uuId + '\'' +
                ", businessType='" + businessType + '\'' +
                ", businessId='" + businessId + '\'' +
                ", businessTime='" + businessTime + '\'' +
                ", businessData='" + businessData + '\'' +
                '}';
    }

    // Getter 方法
    public String getUuId() {
        return uuId;
    }

    public String getBusinessType() {
        return businessType;
    }

    public String getBusinessId() {
        return businessId;
    }

    public String getBusinessTime() {
        return businessTime;
    }

    public String getBusinessData() {
        return businessData;
    }
}

