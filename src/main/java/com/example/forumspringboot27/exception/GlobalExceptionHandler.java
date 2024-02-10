package com.example.forumspringboot27.exception;

import com.example.forumspringboot27.common.AppResult;
import com.example.forumspringboot27.common.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 统一异常处理
 */

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理自定义的已知异常
     * @param e ApplicationException
     * @return
     */
    @ResponseBody
    @ExceptionHandler(ApplicationException.class)
    public AppResult handleApplicationException(ApplicationException e) {
        e.printStackTrace();
        log.error(e.getMessage());
        if (e.getErrorResult() != null) {
            return e.getErrorResult();
        }
        return AppResult.failed(e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public AppResult handleException(Exception e) {
        e.printStackTrace();
        log.error(e.getMessage());
        if (e.getMessage() != null) {
            return AppResult.failed(ResultCode.ERROR_SERVICES);
        }
        return AppResult.failed(e.getMessage());
    }
}
