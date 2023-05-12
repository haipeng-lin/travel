package com.wen.shuzhi.rusticTourism.controller;

import com.wen.shuzhi.loginRegister.entity.R;
import com.wen.shuzhi.loginRegister.entity.User;
import com.wen.shuzhi.rusticTourism.entity.BehaviorRecommendRes;
import com.wen.shuzhi.rusticTourism.entity.RecommendParameter;
import com.wen.shuzhi.rusticTourism.entity.TagRecommendRes;
import com.wen.shuzhi.rusticTourism.entity.UserBehaviorWeight;
import com.wen.shuzhi.rusticTourism.service.AlgorithmConfigService;
import com.wen.shuzhi.rusticTourism.service.Impl.AlgorithmConfigServiceImpl;
import com.wen.shuzhi.rusticTourism.service.Impl.RecommendServiceImpl;
import com.wen.shuzhi.rusticTourism.service.Impl.UserManageServiceImpl;
import com.wen.shuzhi.rusticTourism.service.RecommendService;
import com.wen.shuzhi.rusticTourism.service.UserManageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
public class UserManageController {
    @Autowired
    private UserManageServiceImpl userManageService;

    @Autowired
    private RecommendServiceImpl recommendService;

    @Autowired
    private AlgorithmConfigServiceImpl algorithmConfigService;

    static int policyDivision; //策略划分值(用户行为数)，<基于标签，>=协同过滤
    static double likeWeight;
    static double collectionWeight;
    static double commentWeight;
    static double clicksWeight1;
    static double browseWeight;


    //获取用户的推荐信息 显示在后台页面中
    @GetMapping("/userSpecificInfo")
    public R userSpecificInfo(@RequestParam(value="userId") Integer userId) {

        HashMap<String,Object> retMap=new HashMap<>();
        //1、获取用户的个人信息
        User user = userManageService.getUserByUserId(userId);

        //2、获取该用户的推荐信息
        TagRecommendRes tagRecommendRes = userManageService.getTagRecommendResByUserId(userId);
        if(tagRecommendRes!=null){

            List<String> tagNameList = Arrays.asList((tagRecommendRes.getTagNameList()).split(","));
            List<String> tagWeightList = Arrays.asList((tagRecommendRes.getTagWeightList()).split(","));

            log.info("tagNameList===" + tagNameList);
            log.info("tagWeightList===" + tagWeightList);

            retMap.put("tagRecommendRes",tagRecommendRes);

        }

        //3、统计用户的行为次数(点赞/收藏/评论)
        int likedNum = recommendService.getLikedNumByUserId(userId);
        int collectionNum = recommendService.getCollectionNumByUserId(userId);
        int commentNum = recommendService.getCommentNumByUserId(userId);
        int sum = likedNum + collectionNum + commentNum;
        log.info("用户" + userId + "的行为次数为:" + sum);

        //4、获取用户初始喜好标签
        String initialTag = recommendService.getInitialTagByUserId(userId);
        if(initialTag!=null){
            retMap.put("initialTag",initialTag);
        }else{
            retMap.put("initialTag","");
        }

        RecommendParameter nowRecommendParameter = algorithmConfigService.getNowRecommendParameter();
        policyDivision = nowRecommendParameter.getPolicyDivision();

        UserBehaviorWeight nowUserBehaviorWeight = algorithmConfigService.getNowUserBehaviorWeight();
        likeWeight = nowUserBehaviorWeight.getLikeWeight();
        collectionWeight = nowUserBehaviorWeight.getCollectionWeight();
        commentWeight = nowUserBehaviorWeight.getCommentWeight();
        clicksWeight1 = nowUserBehaviorWeight.getClicksWeight();
        browseWeight = nowUserBehaviorWeight.getBrowseWeight();

        BehaviorRecommendRes behaviorRecommendRes = userManageService.getBehaviorRecommendResByUserId(userId);

        if(behaviorRecommendRes!=null){
            retMap.put("behaviorRecommendRes",behaviorRecommendRes);
        }
        if (sum < policyDivision) {
            //跳转 基于标签的推荐分析

            retMap.put("html","userSpecificInfoByTag");
            return new R(true,retMap,"基于标签的推荐分析结果如下");
        } else {
            //跳转 基于用户行为协同过滤的推荐分析
            retMap.put("html","userSpecificInfoByUserBehavior");
            return new R(true,retMap,"基于用户行为协同过滤的推荐分析结果如下");

        }

    }

}
