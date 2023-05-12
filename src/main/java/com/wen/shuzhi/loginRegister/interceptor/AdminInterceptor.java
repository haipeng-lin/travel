package com.wen.shuzhi.loginRegister.interceptor;

/*
@author peng
@create 2023-03-20-15:44
@description
*/

import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AdminInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String origin  = request.getHeader(HttpHeaders.ORIGIN);
        if (origin != null) {

            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT, HEAD");

            //这里设置允许的自定义header参数，非常重要
            response.setHeader("Access-Control-Allow-Headers", "token");
            response.setHeader("Access-Control-Max-Age", "3600");
        }
        return true;
    }
}