package com.wen.shuzhi.loginRegister.config;


import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * Kaptcha验证码相关配置
 */
@Configuration
public class KaptchaConfig {
    /**
     * 边框
     */
    @Value("${kaptcha.border}")
    private String border;

    /**
     * 边框颜色
     */
    @Value("${kaptcha.border.color}")
    private String borderColor;

    /**
     * 字体颜色
     */
    @Value("${kaptcha.textproducer.font.color}")
    private String textproducerFontColor;

    /**
     * 字体大小
     */
    @Value("${kaptcha.textproducer.font.size}")
    private String textproducerFontSize;

    /**
     * 字体样式
     */
    @Value("${kaptcha.textproducer.font.names}")
    private String textproducerFontNames;

    /**
     * 验证码长度
     */
    @Value("${kaptcha.textproducer.char.length}")
    private String textproducerCharLength;

    /**
     * 图片宽度
     */
    @Value("${kaptcha.image.width}")
    private String imageWidth;

    /**
     * 图片高度
     */
    @Value("${kaptcha.image.height}")
    private String imageHeight;

    /**
     * 存储的 Session Key
     */
    @Value("${kaptcha.session.key}")
    private String sessionKey;

    /**
     * 配置 Kapcha 参数
     * @return
     */



    @Bean
    public DefaultKaptcha getDefaultKapcha() {
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        properties.setProperty("kaptcha.border", border);
        properties.setProperty("kaptcha.border.color", borderColor);
        properties.setProperty("kaptcha.textproducer.font.color", textproducerFontColor);
        properties.setProperty("kaptcha.textproducer.font.size", textproducerFontSize);
        properties.setProperty("kaptcha.textproducer.font.names", textproducerFontNames);
        properties.setProperty("kaptcha.textproducer.char.length", textproducerCharLength);
        properties.setProperty("kaptcha.image.width", imageWidth);
        properties.setProperty("kaptcha.image.height", imageHeight);
        properties.setProperty("kaptcha.session.key", sessionKey);

        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }

}
