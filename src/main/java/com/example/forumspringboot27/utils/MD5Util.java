package com.example.forumspringboot27.utils;

import org.springframework.util.DigestUtils;

/**
 * md5加密工具类
 */
public class MD5Util {
    /**
     * 将明文密码简单加密 得到md5密文
     * @param str 原密码
     * @return
     */
    public static String md5(String str) {
        return DigestUtils.md5DigestAsHex(str.getBytes());
    }

    /**
     * 对用户密码进行加密
     * 规则：先将密码进行md5加密，然后拼接上盐值，拼接成盐值之后再次进行md5加密
     * @param str 明文密码
     * @param salt 盐值
     * @return
     */
    public static String md5Salt(String str, String salt) {
        return md5(md5(str) + salt);
    }
}
