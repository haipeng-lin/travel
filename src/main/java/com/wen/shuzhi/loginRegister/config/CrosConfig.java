package com.wen.shuzhi.loginRegister.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/*
@author peng
@create 2023-03-20-13:38
@description    设置允许跨域
*/
@Configuration
public class CrosConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedHeaders("*")
                .allowedOriginPatterns("*")
                .allowedMethods("GET","HEAD","POST","DELETE","OPTIONS","PUT")
                .allowCredentials(true)
                .maxAge(3600);
    }
}


