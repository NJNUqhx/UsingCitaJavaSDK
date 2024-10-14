package util;

import java.util.Random;

public class StringUtil {
    // 方法：生成指定长度的随机字符串
    public static String getRandomString(int length) {
        // 定义随机字符串的字符范围：大小写字母和数字
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);

        // 生成随机字符串
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }

        return sb.toString();
    }
}
