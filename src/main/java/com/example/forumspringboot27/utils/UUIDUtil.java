package com.example.forumspringboot27.utils;
import java.util.UUID;

public class UUIDUtil {
    /**
     * 生成一个36位的uuid
     * 这个id中会携带‘-’
     * @return
     */
    public static String uuid_36() {
        return UUID.randomUUID().toString();
    }

    /**
     * 生成一个32位的id
     * @return
     */

    public static String uuid_32() {
        return UUID.randomUUID().toString().replaceAll("-","");
    }
}
