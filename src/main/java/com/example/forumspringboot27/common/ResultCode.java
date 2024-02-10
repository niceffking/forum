package com.example.forumspringboot27.common;

public enum ResultCode {
    /** 定义状态码 */
    SUCCESS (0, "成功"),
    FAILED (1000, "操作失败"),
    FAILED_UNAUTHORIZED (1001, "未授权"),
    FAILED_PARAMS_VALIDATE (1002, "参数校验失败"),
    FAILED_FORBIDDEN (1003, "禁⽌访问"),
    FAILED_CREATE (1004, "新增失败"),
    FAILED_NOT_EXISTS (1005, "资源不存在"),
    AILED_USER_EXISTS (1101, "⽤⼾已存在"),
    FAILED_USER_NOT_EXISTS (1102, "⽤⼾不存在"),
    FAILED_USER_ARTICLE_COUNT_ERROR (1103, "⽤⼾文章数量错误"),
    FAILED_LOGIN (1103, "⽤⼾名或密码错误"),
    FAILED_USER_BANNED (1104, "您已被禁⾔, 请联系管理员, 并重新登录."),
    FAILED_TWO_PWD_NOT_SAME (1105, "两次输⼊的密码不⼀致"),
    ERROR_SERVICES (2000, "服务器内部错误"),
    ERROR_IS_NULL (2001, "IS NULL."),

    // 关于板块的状态码
    FAILED_BOARDER_COUNT    (1201,"更新帖子数量失败"),
    FAILED_BOARDER_NOT_EXISTS (1202,"板块不存在"),
    FAILED_BOARDER_ARTICLECOUNT_ERROR (1203,"板块帖子数量错误"),

    FAILD_ARTICLE_NOT_EXISTS (1301,"帖子不存在"),
    FAILD_MESSAGE_NOT_EXISTS (1401,"站内信不存在"),

    FAILD_MYLIKE_NOT_EXISTS (1501,"我的喜欢列表不存在");
    // 状态码
    int code;
    // 状态描述
    String message;
    // 构造⽅法
    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
    public int getCode() {
        return code;
    }
    public String getMessage() {
        return message;
    }
    @Override
    public String toString() {
        return "code = " + code + ", message = " + message + ". ";
    }
}
