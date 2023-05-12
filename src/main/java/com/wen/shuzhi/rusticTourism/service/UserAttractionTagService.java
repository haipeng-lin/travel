package com.wen.shuzhi.rusticTourism.service;

/*
@author peng
@create 2023-05-11-15:46
@description 
*/

import com.wen.shuzhi.rusticTourism.entity.UserAttractionTag;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Service;

@Service
public interface UserAttractionTagService {


    //插入用户景点行为表
    public int insertUserAttractionTag(UserAttractionTag userAttractionTag);

    //根据用户id和景点名称查询用户有行为的景点个数
    UserAttractionTag getUserAttractionTagByUserIdAndAttractionName(Integer userId, String attractionName);

    //修改用户景点行为表
    int updateUserAttractionTag(UserAttractionTag userAttractionTag);

}
