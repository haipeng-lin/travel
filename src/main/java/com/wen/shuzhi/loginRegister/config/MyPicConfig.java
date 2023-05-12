package com.wen.shuzhi.loginRegister.config;

/*
@author peng
@create 2023-04-13-18:47
@description 
*/

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 *解决图片上传后，没有重启Springboot不能显示图片的bug，为图片访问设置虚拟路径
 */
@Configuration
public class MyPicConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/imgs/**")
                .addResourceLocations("file:F:/code/Project/ShuZhiXC/src/main/resources/static/imgs/");
    }
}
