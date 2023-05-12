package com.wen.shuzhi.loginRegister.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 处理整个web controller的异常
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    //指定处理的异常， 处理数组异常 空指针异常
    @ExceptionHandler({ArithmeticException.class,
            NullPointerException.class})
    public String handlerArithException(){
    return "/error/4xx";
    }
}
