package com.wen.shuzhi.loginRegister.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("user") //指定访问数据库中的哪张表
public class User implements Serializable {
    private Integer userId;
    private String account;
    private String password;
    private String username;
    private String avatarImageUrl;
    private String sex;
    private Integer age;
    private String email;
    private String role;
    private Date addTime;
    private Date updateTime;


    public User(String account, String password, String username, String avatarImageUrl, String sex, Integer age, String email, String role, Date addTime, Date updateTime) {
        this.account = account;
        this.password = password;
        this.username = username;
        this.avatarImageUrl = avatarImageUrl;
        this.sex = sex;
        this.age = age;
        this.email = email;
        this.role = role;
        this.addTime = addTime;
        this.updateTime = updateTime;
    }
}
