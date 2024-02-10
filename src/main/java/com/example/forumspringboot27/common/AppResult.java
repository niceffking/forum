package com.example.forumspringboot27.common;

import com.fasterxml.jackson.annotation.JsonInclude;

public class AppResult<T> {
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private int code;
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private String message;
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private T data;

    public AppResult(){};

    public AppResult(int code, String message) {
        this.code = code;
        this.message = message;
        this.data = null;
    }

    public AppResult(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // =========================================== 》》 成功 封装的常用方法
    // 下面是重载方法，方便别人更加个性地去返回信息
    // success(), uccess(T data), success(String msg, T data)
    /**
     * 成功
     * Para success
     */

    // 只需要返回成功的信息，不需要返回数据
    public static AppResult success() {
        return new AppResult(ResultCode.SUCCESS.getCode(),ResultCode.SUCCESS.getMessage());
    }

    /**
     * 需要携带数据，并且成功信息只需要返回封装的信息即可
     * @param data 将要返回的数据
     * @return
     * @param <T>  数据类型
     */
    public static <T> AppResult<T> success(T data) {
        return new AppResult<>(ResultCode.SUCCESS.getCode(),ResultCode.SUCCESS.getMessage(),data);
    }

    /**
     * 需要自定义返回的信息，同时需要携带数据
     * @param msg 自定义的信息
     * @param data 返回的数据
     * @return 带有返回值
     * @param <T>
     */
    public static <T> AppResult<T> success(String msg, T data) {
        return new AppResult<>(ResultCode.SUCCESS.getCode(), msg, data);
    }

    // =========================================== 》》 失败 封装的常用方法

    /**
     * 失败， 不携带data数据，不传入任何参数
     * @return
     */
    public static AppResult failed() {
        return new AppResult(ResultCode.FAILED.getCode(), ResultCode.FAILED.getMessage());
    }

    /**
     * 失败，需要自定义错误信息，但是不返回任何数据
     * @param msg 需要返回的自定义错误提示信息
     * @return
     */
    public static AppResult failed(String msg) {
        return new AppResult(ResultCode.FAILED.getCode(), msg);
    }

    /**
     * 返回一个错误信息，但是需要携带错误的数据
     * @param data 返回的错误的数据
     * @return 返回值
     * @param <T> 范型
     */
    public static <T> AppResult<T> failed(T  data) {
        return new AppResult<>(ResultCode.FAILED.getCode(), ResultCode.FAILED.getMessage(), data);
    }

    /**
     *  返回一个错误状态
     * @param resultCode 错误状态
     * @return
     */
    public static AppResult failed(ResultCode resultCode) {
        return new AppResult(resultCode.getCode(), resultCode.getMessage());
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
