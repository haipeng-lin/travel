package com.wen.shuzhi.rusticTourism.controller;

import com.wen.shuzhi.loginRegister.entity.R;
import com.wen.shuzhi.rusticTourism.entity.*;
import com.wen.shuzhi.rusticTourism.service.Impl.AlgorithmConfigServiceImpl;
import com.wen.shuzhi.rusticTourism.service.Impl.AttractionServiceImpl;
import com.wen.shuzhi.rusticTourism.service.Impl.RecommendServiceImpl;
import com.wen.shuzhi.rusticTourism.service.Impl.UserManageServiceImpl;
import lombok.extern.slf4j.Slf4j;
//import org.apache.avro.generic.GenericData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 推荐景点过程
 */
@RestController
@Slf4j
public class showAttractionRecommendProcess {
    @Autowired
    private AttractionServiceImpl attractionsService;

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

    /**
     * 后台展示用户各个景点标签的权重 - 柱状图
     */
   @RequestMapping("/getTagNameAndWeight")
    public R getTagNameAndWeight(@RequestParam("userId")Integer userId){
        List<AttractionTag> attractionTagList = new ArrayList<>();

        log.info("查询的用户id:"+userId);
        TagRecommendRes tagRecommendRes = userManageService.getTagRecommendResByUserId(userId);

        List<String> tagNameList = Arrays.asList((tagRecommendRes.getTagNameList()).split(","));
        List<String> tagWeightList = Arrays.asList((tagRecommendRes.getTagWeightList()).split(","));

        for(int i=0;i<tagNameList.size();i++){
            String tagName = tagNameList.get(i);
            double tagWeight = Double.parseDouble(tagWeightList.get(i));
            attractionTagList.add(new AttractionTag(tagName,tagWeight));
        }

        log.info(attractionTagList.toString());

        return new R(true,attractionTagList,"用户各个景点标签的权重如下");
    }


    //后台展示基于行为计算后 该用户有行为的景点权重
    @RequestMapping("/getAttractionAndWeight")
    public R getAttractionAndWeight(@RequestParam("userId")Integer userId) {
        //获取配置参数
        RecommendParameter nowRecommendParameter = algorithmConfigService.getNowRecommendParameter();
        policyDivision = nowRecommendParameter.getPolicyDivision();
        UserBehaviorWeight nowUserBehaviorWeight = algorithmConfigService.getNowUserBehaviorWeight();
        likeWeight = nowUserBehaviorWeight.getLikeWeight();
        collectionWeight = nowUserBehaviorWeight.getCollectionWeight();
        commentWeight = nowUserBehaviorWeight.getCommentWeight();
        clicksWeight1 = nowUserBehaviorWeight.getClicksWeight();
        browseWeight = nowUserBehaviorWeight.getBrowseWeight();

        /**
         * 统计该用户点赞/收藏/评论/点击/浏览过的景点权重
         */
        //1. 获取该用户点赞的所有景点id
        List<Integer> likedList = recommendService.getLikedByUserId(userId);
        //2. 获取该用户收藏的所有景点id
        List<Integer> collectionList = recommendService.getCollectionByUserId(userId);
        //3. 获取该用户评论的所有景点id
        List<Integer> commentList = recommendService.getCommentByUserId(userId);
        //4. 获取该用户点击过的所有景点id - 对应点击次数
        List<Click> clickList = recommendService.getClicksByUserId(userId);
        //5. 获取该用户所有浏览过的景点id - 对应时长
        List<BrowseTime> browseList = recommendService.getBrowseByUserId(userId);
        log.info("userId=====" + userId);
        log.info("点赞的景点id" + likedList.toString());
        log.info("收藏的景点id" + collectionList.toString());
        log.info("评论的景点id" + commentList.toString());
        log.info("点击过的景点id" + clickList.toString());
        log.info("浏览过的景点id" + browseList.toString());

        //4. 统计所该用户对所有点赞/收藏/评论/点击/浏览过的景点的权重
        //点赞:likeWeight 收藏:collectionWeight 评论:commentWeight 点击次数:clicksWeight1 浏览时长(s):browseWeight

        //5. 统计该用户的景点权重
        ArrayList<Weight> weightList = new ArrayList<>();
        //累加点赞的权重
        for (Integer attractionsId : likedList) {
            //判断该用户是否已经对该景点 点赞
            double existWeight = isExist(weightList, attractionsId);
            if (existWeight != 0.0) {
                //已经点赞/收藏/评论过
                weightList.remove(new Weight(attractionsId, existWeight));
                weightList.add(new Weight(attractionsId, existWeight + likeWeight));
            } else {
                weightList.add(new Weight(attractionsId, likeWeight));
            }
        }
        //累加收藏的权重
        for (Integer attractionsId : collectionList) {
            //判断该用户是否已经对该景点 收藏
            double existWeight = isExist(weightList, attractionsId);
            if (existWeight != 0.0) {
                //已经点赞/收藏/评论过
                weightList.remove(new Weight(attractionsId, existWeight));
                weightList.add(new Weight(attractionsId, existWeight + UserManageController.collectionWeight));
            } else {
                weightList.add(new Weight(attractionsId, UserManageController.collectionWeight));
            }

        }
        //累加评论的权重
        for (Integer attractionsId : commentList) {
            //判断该用户是否已经对该景点 评论
            double existWeight = isExist(weightList, attractionsId);
            if (existWeight != 0.0) {
                //已经点赞/收藏/评论过
                weightList.remove(new Weight(attractionsId, existWeight));
                weightList.add(new Weight(attractionsId, existWeight + commentWeight));
            } else {
                weightList.add(new Weight(attractionsId, commentWeight));
            }
        }
        //累加点击次数的权重
        for (Click clicks : clickList) {
            Integer attractionsId = clicks.getAttractionId();
            Integer clicksNum = clicks.getClicks();
            double existWeight = isExist(weightList, attractionsId);
            if (existWeight != 0.0) {
                //已经点赞/收藏/评论过
                weightList.remove(new Weight(attractionsId, existWeight));
                weightList.add(new Weight(attractionsId, existWeight + clicksNum * clicksWeight1));
            } else {
                weightList.add(new Weight(attractionsId, clicksNum * clicksWeight1));
            }
        }
        //累加浏览时长的权重
        for (BrowseTime browse : browseList) {
            Integer attractionsId = browse.getAttractionId();
            Integer browseTime = browse.getBrowseTime();
            double existWeight = isExist(weightList, attractionsId);
            if (existWeight != 0.0) {
                //已经点赞/收藏/评论过
                weightList.remove(new Weight(attractionsId, existWeight));
                weightList.add(new Weight(attractionsId, existWeight + browseTime * browseWeight));
            } else {
                weightList.add(new Weight(attractionsId, browseTime * browseWeight));
            }
        }

        List<Weight> attractionWeightList = new ArrayList<>();
        //权重保留两位小数
        for(Weight Weight : weightList){
            int attractionsId = Weight.getAttractionId();
            String name = attractionsService.getAttractionNameById(attractionsId);
            //保留两位小数
            double weight = Double.parseDouble(String.format("%.2f", Weight.getWeight()));
            attractionWeightList.add(new Weight(attractionsId,weight));
        }

        log.info(attractionWeightList.toString());
        return new R(true,attractionWeightList,"该用户有行为的景点权重");
    }

    /**
     * 判断该用户的weightList是否存在已经 点赞/评论/收藏的景点id
     * 存在 return 权重
     * 不存在 return 0
     */
    public double isExist(List<Weight> weightList, Integer attractionsId) {
        for (Weight weight : weightList) {
            if (weight.getAttractionId() == attractionsId) {
                return weight.getWeight();
            }
        }
        return 0.0;
    }
}
