package com.wen.shuzhi.loginRegister.controller;

import com.google.code.kaptcha.Producer;
import com.wen.shuzhi.loginRegister.entity.R;
import lombok.extern.slf4j.Slf4j;
//import org.junit.platform.commons.logging.Logger;
//import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

import static com.wen.shuzhi.loginRegister.utils.RedisConstants.REGISTER_CODE_KEY;
import static com.wen.shuzhi.loginRegister.utils.RedisConstants.REGISTER_CODE_TTL;


/**
 * 生成验证码的controller 把验证码放入redis中
 */
@Slf4j
@Controller
@RequestMapping("/kaptcha")
public class KaptchaController {

    @Autowired
    private Producer captchaProducer;

//    @Autowired
//    private static Logger logger = LoggerFactory.getLogger(KaptchaController.class);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @RequestMapping("getKaptchaImage")
    public R getKaptchaImage(HttpServletRequest request, HttpServletResponse response) throws Exception {
//        HttpSession session = request.getSession();
//        String code = (String)session.getAttribute(Constants.KAPTCHA_SESSION_KEY);
//        stringRedisTemplate.opsForValue().set(Constants.KAPTCHA_SESSION_CONFIG_KEY,LOGIN_CODE_TTL, TimeUnit.MINUTES);
//        log.debug(("******************验证码是: " + code + "******************"));

        response.setDateHeader("Expires", 0);

        // Set standard HTTP/1.1 no-cache headers.
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");

        // Set IE extended HTTP/1.1 no-cache headers (use addHeader).
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");

        // Set standard HTTP/1.0 no-cache header.
        response.setHeader("Pragma", "no-cache");

        // return a jpeg
        response.setContentType("image/jpeg");

        // create the text for the image
        String capText = captchaProducer.createText();
        log.info("生成的验证码:"+capText);

        //存放验证码到redis中
        stringRedisTemplate.opsForValue().set(REGISTER_CODE_KEY,capText,REGISTER_CODE_TTL, TimeUnit.MINUTES);

        // store the text in the session
        //session.setAttribute(Constants.KAPTCHA_SESSION_KEY, capText);



        // create the image with the text
        BufferedImage bi = captchaProducer.createImage(capText);
        ServletOutputStream out = response.getOutputStream();

        // write the data out
        ImageIO.write(bi, "jpg", out);
        try {
            out.flush();
        } finally {
            out.close();
        }
        return null;
    }

}
