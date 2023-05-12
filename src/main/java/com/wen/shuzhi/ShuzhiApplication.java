package com.wen.shuzhi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.wen.shuzhi.loginRegister.mapper")
@MapperScan("com.wen.shuzhi.monitorSystem.mapper")
@MapperScan("com.wen.shuzhi.rusticTourism.mapper")
public class ShuzhiApplication {

    public static void main(String[] args) {
        SpringApplication.run(
                ShuzhiApplication.class, args);
    }

}
