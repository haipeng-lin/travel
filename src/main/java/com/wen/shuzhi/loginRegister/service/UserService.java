package com.wen.shuzhi.loginRegister.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wen.shuzhi.loginRegister.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService extends IService<User> {



    int insertUser(User user);

    int deleteUser(Integer userId);

    List<User> listAllUsers();

    User getUserByUserId(Integer userId);

    User getUserByAccount(String account);

    User getUserByAccountAndPasswordAndRole(String account, String password,String role);

    int modifyPassword(Integer userId,String password);

    int modifyUser(User user);

}
