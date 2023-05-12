package com.wen.shuzhi.rusticTourism.service;

import com.wen.shuzhi.loginRegister.entity.User;
import com.wen.shuzhi.rusticTourism.entity.BehaviorRecommendRes;
import com.wen.shuzhi.rusticTourism.entity.TagRecommendRes;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserManageService{


    List<User> getAllUser();

    User getUserByUserId(Integer userId);


    TagRecommendRes getTagRecommendResByUserId(Integer userId);

    int saveRecommendListByTag(Integer userId,String recommendList ,String addOtherList,String haveBehaviorList,String tagNameList,String tagWeightList);

    int updateRecommendListByTag(Integer userId, String recommendList, String addOtherList, String haveBehaviorList, String tagNameList, String tagWeightList);

    BehaviorRecommendRes getBehaviorRecommendResByUserId(Integer userId);

    int saveRecommendListByBehavior(Integer userId, String recommendList, String addOtherList, String neighborList);

    int updateRecommendListByBehavior(Integer userId, String recommendList, String addOtherList, String neighborList);

    int addScoreByBehavior(Integer userId, Integer score,String advice);

    int addScoreByTag(Integer userId, Integer score,String advice);
}
