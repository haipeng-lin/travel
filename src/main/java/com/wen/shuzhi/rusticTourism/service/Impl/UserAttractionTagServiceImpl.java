package com.wen.shuzhi.rusticTourism.service.Impl;

/*
@author peng
@create 2023-05-11-15:47
@description 
*/

import com.wen.shuzhi.rusticTourism.entity.UserAttractionTag;
import com.wen.shuzhi.rusticTourism.mapper.UserAttractionTagMapper;
import com.wen.shuzhi.rusticTourism.service.UserAttractionTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserAttractionTagServiceImpl implements UserAttractionTagService {

    @Autowired
    UserAttractionTagMapper userAttractionTagMapper;


    @Override
    public int insertUserAttractionTag(UserAttractionTag userAttractionTag) {
        return userAttractionTagMapper.insertUserAttractionTag(userAttractionTag);
    }

    @Override
    public UserAttractionTag getUserAttractionTagByUserIdAndAttractionName(Integer userId, String attractionName) {
        return userAttractionTagMapper.getUserAttractionTagByUserIdAndAttractionName(userId,attractionName);
    }

    @Override
    public int updateUserAttractionTag(UserAttractionTag userAttractionTag) {
        return userAttractionTagMapper.updateUserAttractionTag(userAttractionTag);
    }
}
