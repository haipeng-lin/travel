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
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/imgs/**")
                .addResourceLocations("classpath:/static/imgs/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .excludePathPatterns( //登录、注册、获取验证码、判断账号是否可用图片路径不拦截
                        "/login",
                        "/register",
                        "/kaptcha/getKaptchaImage",
                        "/checkAccount",
                        "/imgs/**",
                        "/getAttractionAndWeight"
//                        "/blog/upload-img"
                ).order(1);


        //拦截所有路径，除了注册路径，目的为了刷新token有效期，优先级最高
        registry.addInterceptor(new RefreshTokenInterceptor(stringRedisTemplate))
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/user/register",
                        "/imgs/**"  // 在这里添加对 "/imgs/**" 的排除
                )
                .order(2);

    }


}
