package com.wen.shuzhi.loginRegister.mapper;

import com.wen.shuzhi.loginRegister.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserMapperTest {

    @Autowired
    UserMapper userMapper;

//    @Test
//    void insertUser1() {
//        userMapper.insertUser(new User("abc","123456",22,"男","3063273404@qq.com"));
//    }
//
//    @Test
//    void insertUser2() {
//        userMapper.insertUser(new User("abc","123456",22,"女","3063273404@qq.com"));
//    }
//
//    @Test
//    void insertUser3() {
//        userMapper.insertUser(new User("a","123456",22,"男","3063273404@qq.com"));
//    }
//
//    @Test
//    void insertUser4() {
//        userMapper.insertUser(new User("abcdefghijk","123456",22,"男","3063273404@qq.com"));
//    }
//
//    @Test
//    void insertUser5() {
//        userMapper.insertUser(new User("abc","123",22,"男","3063273404@qq.com"));
//    }
//
//    @Test
//    void insertUser6() {
//        userMapper.insertUser(new User("abc","12345678999",22,"男","3063273404@qq.com"));
//    }
//
//    @Test
//    void insertUser7() {
//        userMapper.insertUser(new User("abc","123456",0,"男","3063273404@qq.com"));
//    }
//
//    @Test
//    void insertUser8() {
//        userMapper.insertUser(new User("abc","123456",130,"男","3063273404@qq.com"));
//    }
//
//    @Test
//    void insertUser9() {
//        userMapper.insertUser(new User("abc","123456",22,"我我","3063273404@qq.com"));
//    }
//
//    @Test
//    void insertUser10() {
//        userMapper.insertUser(new User("abc","123456",22,"男","33qq.com"));
//    }

    @Test
    void deleteUser1() {
        userMapper.deleteUser(22);
    }

//    @Test
//    void deleteUser2() {
//        userMapper.deleteUser(ABC);
//    }

    @Test
    void deleteUser3() {
        userMapper.deleteUser(100000);
    }
}