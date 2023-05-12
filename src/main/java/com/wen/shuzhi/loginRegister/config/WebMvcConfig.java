package com.wen.shuzhi.loginRegister.config;

//import com.wen.loginRegister.interceptor.AdminInterceptor;
//import com.wen.loginRegister.interceptor.AdminInterceptor;
//import com.wen.loginRegister.interceptor.LoginInterceptor;
//import com.wen.loginRegister.interceptor.AdminInterceptor;
//import com.wen.loginRegister.interceptor.LoginInterceptor;
//import com.wen.loginRegister.interceptor.RefreshTokenInterceptor;

import com.wen.shuzhi.loginRegister.interceptor.LoginInterceptor;
import com.wen.shuzhi.loginRegister.interceptor.RefreshTokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .excludePathPatterns( //登录、注册、获取验证码、判断账号是否可用图片路径不拦截
                        "/login",
                        "/register",
                        "/kaptcha/getKaptchaImage",
                        "/checkAccount",
                        "/imgs/**"
                ).order(2);


        //拦截所有路径，除了注册路径，目的为了刷新token有效期
        registry.addInterceptor(new RefreshTokenInterceptor(stringRedisTemplate))
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/user/register"
                )
                .order(1);

    }
}
