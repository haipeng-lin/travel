package com.wen.shuzhi.loginRegister.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wen.shuzhi.loginRegister.entity.User;
import com.wen.shuzhi.loginRegister.mapper.UserMapper;
import com.wen.shuzhi.loginRegister.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public int insertUser(User user) {
        return userMapper.insertUser(user);
    }

    @Override
    public int deleteUser(Integer userId) {
        return userMapper.deleteUser(userId);
    }

    @Override
    public List<User> listAllUsers() {
        return userMapper.listAllUsers();
    }

    @Override
    public User getUserByUserId(Integer userId) {
        return userMapper.getUserByUserId(userId);
    }

    @Override
    public User getUserByAccount(String account) {

        return userMapper.getUserByAccount(account);
    }

    @Override
    public User getUserByAccountAndPasswordAndRole(String account, String password,String role) {
        return userMapper.getUserByAccountAndPasswordAndRole(account,password,role);
    }

    @Override
    public int modifyPassword(Integer userId, String password) {

        return userMapper.modifyPassword(userId,password);
    }

    @Override
    public int modifyUser(User user) {
        return userMapper.modifyUser(user);
    }
}
