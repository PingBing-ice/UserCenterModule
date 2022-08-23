package com.user.util.exception;


import com.user.util.common.B;
import com.user.util.common.ErrorCode;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * @author ice
 * @date 2022/6/19 18:21
 */
// 错误处理器,默认在这里
@RestControllerAdvice
@Log4j2
public class GlobalExceptionHeader {

    @ExceptionHandler(GlobalException.class)
    public B<ErrorCode> businessExceptionHeader(GlobalException e) {
        log.info(e.getMessage(),e.getCode(),e.getDescription(),e);
        return B.error(e.getCode(),e.getMessage(),e.getDescription());
    }
    @ExceptionHandler(RuntimeException.class)
    public B<ErrorCode> runExceptionHeader(RuntimeException e) {
        log.info("runException",e);
        return B.error(ErrorCode.SYSTEM_EXCEPTION,e.getMessage(),"");
    }
}
