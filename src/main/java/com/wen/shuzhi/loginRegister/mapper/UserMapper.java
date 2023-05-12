package com.wen.shuzhi.loginRegister.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wen.shuzhi.loginRegister.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User>{


    //插入用户
    @Insert("insert into user(`account`,`password`,`username`,`avatar_image_url`,`sex`,`age`,`email`,`role`,`add_time`) values(#{account},#{password},#{username},#{avatarImageUrl},#{sex},#{age},#{email},#{role},#{addTime})")
    int insertUser(User user);

    //删除用户
    @Delete("delete from user where user_id=#{userId}")
    int deleteUser(Integer userId);

    //查询所有用户
    @Select("select * from user")
    List<User> listAllUsers();

    @Select("select * from user where user_id=#{userId}")
    User getUserByUserId(Integer userId);

    @Select("select * from user where account=#{account}")
    User getUserByAccount(String account);

    @Select("select * from user where account=#{account} and password =#{password} and role=#{role}")
    User getUserByAccountAndPasswordAndRole(String account, String password,String role);

    @Update("update user set password=#{password} where user_id =#{userId}")
    Integer modifyPassword(Integer userId,String password);

    @Update("update user set account=#{account},password=#{password},username=#{username},avatar_image_url=#{avatarImageUrl},sex=#{sex},age=#{age},email=#{email},role=#{role},update_time=#{updateTime} where user_id=#{userId}")
    Integer modifyUser(User user);
}

