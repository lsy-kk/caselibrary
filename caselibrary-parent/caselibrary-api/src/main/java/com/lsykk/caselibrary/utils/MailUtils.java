package com.lsykk.caselibrary.utils;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import java.util.Random;

public class MailUtils {
    /**
     * 发送验证码
     * @param email 接收方邮箱
     * @param code 发送的验证码
     */
    public static boolean sendVerifyCode(String email, String code) {
        try {
            SimpleEmail mail = new SimpleEmail();
            // 发送邮件的服务器
            mail.setHostName("smtp.qq.com");
            // 发送邮箱和对应段授权码，授权码是开启SMTP的密码
            mail.setAuthentication("1275917295@qq.com", "pfkbdhrvifhojdaj");
            // 发送邮箱和昵称
            mail.setFrom("1275917295@qq.com", "case-lib官方");
            // 开启SSL加密
            mail.setSSLOnConnect(true);
            // 接收的邮箱
            mail.addTo(email);
            // 邮件标题
            mail.setSubject("[case-lib] 验证码");
            // 邮件内容
            mail.setMsg("[case-lib]Hi~\n 您的验证码为:" + code + "\n"+ "(有效期为五分钟)");
            //发送
            mail.send();
            return true;
        } catch (EmailException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 生成长度为n的随机字母数字串
     * @param n
     * @return
     */
    public static String getRandomCode(int n){
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        int len = chars.length();
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < n; i++) {
            code.append(chars.charAt(random.nextInt(len)));
        }
        System.out.println(code.toString());
        return code.toString();
    }

}
