package com.wen.shuzhi.loginRegister.utils;

public class RedisConstants {

    //注册验证码有效期为4分钟
    public static final String REGISTER_CODE_KEY = "register:code:";
    public static final Long REGISTER_CODE_TTL = 4L;

    //登录token有效期为30分钟
    public static final String LOGIN_USER_KEY = "login:token:";
    public static final Long LOGIN_USER_TTL = 30L;


}
