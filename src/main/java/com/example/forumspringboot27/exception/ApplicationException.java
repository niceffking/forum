package com.example.forumspringboot27.exception;


import com.example.forumspringboot27.common.AppResult;

/**
 * 自定义异常
 */
public class ApplicationException extends RuntimeException{
    // 自定义错误信息
    protected AppResult errorResult;

    private static final long  serialVersionUID = -3533806916645793660L;
    public ApplicationException(AppResult error) {
        super(error.getMessage());
        this.errorResult = error;
    }

    public ApplicationException() {
        super();
    }

    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApplicationException(Throwable cause) {
        super(cause);
    }

    public AppResult getErrorResult() {
        return errorResult;
    }
}
