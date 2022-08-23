package com.user.usercenter.utils;

import org.springframework.util.DigestUtils;

/**
 * @author ice
 * @date 2022/6/14 15:41
 */

public class MD5 {
    private static final  String SALT = "ice";

    public static String getMD5(String password) {
        // 加密密码
        return DigestUtils.md5DigestAsHex((SALT + password).getBytes());
    }

    public static void main(String[] args) {
        String md5 = getMD5("123456");
        System.out.println(md5);
    }
}
