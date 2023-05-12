package com.wen.shuzhi.rusticTourism.service.Impl;

import com.wen.shuzhi.loginRegister.entity.User;
import com.wen.shuzhi.rusticTourism.entity.BehaviorRecommendRes;
import com.wen.shuzhi.rusticTourism.entity.TagRecommendRes;
import com.wen.shuzhi.rusticTourism.mapper.UserManageMapper;
import com.wen.shuzhi.rusticTourism.service.UserManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserManageServiceImpl implements UserManageService {

    @Autowired
    private UserManageMapper userManageMapper;



    @Override
    public List<User> getAllUser() {
        return userManageMapper.getAllUser();
    }

    @Override
    public User getUserByUserId(Integer userId) {
        return userManageMapper.getUserByUserId(userId);
    }

    @Override
    public TagRecommendRes getTagRecommendResByUserId(Integer userId) {
        return userManageMapper.getTagRecommendResByUserId(userId);
    }

    @Override
    public int saveRecommendListByTag(Integer userId,String recommendList,String addOtherList,String haveBehaviorList,String tagNameList,String tagWeightList) {
        return userManageMapper.saveRecommendListByTag(userId,recommendList,addOtherList,haveBehaviorList,tagNameList,tagWeightList);
    }

    @Override
    public int updateRecommendListByTag(Integer userId, String recommendList, String addOtherList,String haveBehaviorList,String tagNameList,String tagWeightList) {
        return userManageMapper.updateRecommendListByTag(userId,recommendList,addOtherList,haveBehaviorList,tagNameList,tagWeightList);
    }

    @Override
    public BehaviorRecommendRes getBehaviorRecommendResByUserId(Integer userId) {
        return userManageMapper.getBehaviorRecommendResByUserId(userId);
    }

    @Override
    public int saveRecommendListByBehavior(Integer userId, String recommendList, String addOtherList, String neighborList) {
        return userManageMapper.saveRecommendListByBehavior(userId,recommendList,addOtherList,neighborList);
    }

    @Override
    public int updateRecommendListByBehavior(Integer userId, String recommendList, String addOtherList, String neighborList) {
        return userManageMapper.updateRecommendListByBehavior(userId,recommendList,addOtherList,neighborList);
    }

    @Override
    public int addScoreByBehavior(Integer userId, Integer score,String advice) {
        return userManageMapper.addScoreByBehavior(userId,score,advice);
    }

    @Override
    public int addScoreByTag(Integer userId, Integer score,String advice) {
        return userManageMapper.addScoreByTag(userId,score,advice);
    }
}
