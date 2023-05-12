package com.wen.shuzhi.loginRegister.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 *  返回前端的用户信息，不对外暴露账户的密码
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO implements Serializable {
    private Integer userId;
    private String account;
    private String username;
    private String sex;
    private Integer age;
    private String email;
    private String role;
    private String avatarImageUrl;
}
