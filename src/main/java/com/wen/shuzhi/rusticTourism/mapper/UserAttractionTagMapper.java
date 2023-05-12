package com.wen.shuzhi.rusticTourism.mapper;

/*
@author peng
@create 2023-05-11-15:32
@description 
*/

import com.wen.shuzhi.rusticTourism.entity.UserAttractionTag;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Date;

@Mapper
public interface UserAttractionTagMapper {

    //插入用户景点行为表
    @Insert("insert into user_attraction_tag(`user_id`,`attraction_name`,`attraction_tag`,`weight`,`add_time`) values(#{userId},#{attractionName},#{attractionTag},#{weight},#{addTime})")
    public int insertUserAttractionTag(UserAttractionTag userAttractionTag);

    //根据用户id和景点名称查询用户有行为的景点个数
    @Select("select * from user_attraction_tag where user_id=#{userId} and attraction_name=#{attractionName}")
    UserAttractionTag getUserAttractionTagByUserIdAndAttractionName(Integer userId, String attractionName);

    //修改用户景点行为表
    @Update("update user_attraction_tag set weight=#{weight},update_time=#{updateTime} where user_id=#{userId} and attraction_name=#{attractionName}")
    int updateUserAttractionTag(UserAttractionTag userAttractionTag);

}
