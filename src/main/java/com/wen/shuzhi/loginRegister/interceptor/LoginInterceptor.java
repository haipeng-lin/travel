package com.wen.shuzhi.loginRegister.interceptor;

import com.wen.shuzhi.loginRegister.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //1、如果为预检请求，则直接通过：因为预检请求不会带上token请求头
        String method = request.getMethod();
        log.info("拦截器拦截请求的方法:"+method);
        if(method.equals("OPTIONS")){
            log.info("预检请求，直接通过");
            return true;
        }

        //2、通过是否有mark:login标记来判断用户是否需要拦截
        String mark = (String) request.getAttribute("mark");
        if(mark!=null && mark.equals("login")){
            //没有 需要拦截 设置状态码
            log.info("用户不存在，拦截,请登录");
            response.setStatus(401);
            return false;
        }

        return true;
    }
}
