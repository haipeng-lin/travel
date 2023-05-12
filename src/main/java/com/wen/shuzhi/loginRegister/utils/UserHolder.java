package com.wen.shuzhi.loginRegister.utils;

/*
@author peng
@create 2023-03-20-9:46
@description 
*/


import com.wen.shuzhi.loginRegister.entity.UserDTO;

/**
 * 使用ThreadLocal替代Session完成保存用户登录信息功能
 *
 * 使用ThreadLocal替代Session的好处：可以在同一线程中很方便的获取用户信息，不需要频繁的传递session对象。
 *
 * 具体实现流程：
 * 1、在登录业务代码中，当用户登录成功时，生成一个登录凭证存储到redis中，将凭证中的字符串保存在cookie中返回给客户端。
 * 2、使用一个拦截器拦截请求，从cookie中获取凭证字符串与redis中的凭证进行匹配，获取用户信息，将用户信息存储到ThreadLocal中，在本次请求中持有用户信息，即可在后续操作中使用到用户信息。
 */
public class UserHolder {
    public static final ThreadLocal<UserDTO> tl = new ThreadLocal<>();

    public static void saveUser(UserDTO user){
        tl.set(user);
    }
    public static UserDTO getUser(){
        return tl.get();
    }
    public static void removeUser(){
        tl.remove();
    }
}