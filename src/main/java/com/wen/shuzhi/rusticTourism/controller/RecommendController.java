package com.wen.shuzhi.rusticTourism.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.wen.shuzhi.loginRegister.entity.R;
import com.wen.shuzhi.rusticTourism.entity.*;
import com.wen.shuzhi.rusticTourism.service.Impl.*;
import com.wen.shuzhi.rusticTourism.utils.CosineSimilarity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 优化版
 */
@Slf4j
@RestController
public class RecommendController {

    @Autowired
    private RecommendServiceImpl recommendService;

    @Autowired
    private AttractionServiceImpl attractionService;

    @Autowired
    private TagRecommendServiceImpl tagRecommendService;

    @Autowired
    private AlgorithmConfigServiceImpl algorithmConfigService;

    @Autowired
    private UserManageServiceImpl userManageService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public static String[] initialTag;

    List<Integer> recommendIdList = new LinkedList<>();

    List<Integer> addOtherList = new ArrayList<>();

    List<Integer> neighboringList = new ArrayList<>();

    static double locationWeight; //计算景点相似度中，地理位置权重
    static double tagWeight; //计算景点相似度中，景点标签权重
    static double likeCollectionCommentWeight; //计算景点相似度中，点赞/收藏/评论权重
    static double clicksWeight; //计算景点相似度中，被点击次数权重
    static double keyWordWeight; //计算景点相似度中，关键词权重

    static int policyDivision; //策略划分值(用户行为数)，<基于标签，>=协同过滤
    static int similarityAttractionNum; //附加几个相似度高的景点
    static int neighboringNum; //基于用户行为，寻找几个最邻近用户

    static double likeWeight; //基于用户行为，点赞权重
    static double collectionWeight; //基于用户行为，收藏权重
    static double commentWeight; //基于用户行为，评论权重
    static double clicksWeight1; //基于用户行为,点击次数系数
    static double browseWeight; //基于用户行为,浏览时长系数

    static double init; //热度衰减函数，初始值，上限
    static int m; //热度衰减函数，天数，x轴长度
    static double finish; //热度衰减函数，最低值，下限

    //判断是否需要弹出选择标签弹窗
    @GetMapping("/isChooseTag")
    public R isChooseTag(@RequestParam("userId")Integer userId){
        int exist = recommendService.isExist(userId);
        if(exist == 0){
            //首次，需要添加标签
            return new R(false,"用户还没有添加过标签");
        }
        return new R(true,"用户已经添加过标签了，不需要弹窗");
    }

    //提交选择的标签
    @PostMapping("/initialTag")
    public R initialTag(@RequestParam("userId")Integer userId,
                        @RequestParam("tags") List<String> tags){
        log.info(tags.toString());
        log.info("userId==="+userId);
        log.info("tags==="+tags);

        if(tags.size()==0 || tags==null){ //无喜好标签
            log.info("用户无喜好标签");
            //保存到数据库中
            recommendService.saveInitialTags(userId,null);
            return new R(true,"您没有选择标签");
        }


        StringBuffer sb = new StringBuffer();
        for(int i=0;i<tags.size();i++){
            sb.append(tags.get(i));
            if(i < tags.size()-1){
                sb.append(",");
            }
        }
        log.info(sb.toString());
        //保存到数据库中
        recommendService.saveInitialTags(userId,sb.toString());
        return new R(true,"选择标签成功,请查看推荐旅游点");
    }

    //判断是否要弹出评分弹窗
    @GetMapping("/isShowScore")
    public boolean isShowScore(@RequestParam("userId")Integer userId){
        String key="recommend_userId:"+userId;

        //判断用户是否存有推荐景点
        Boolean isExist = stringRedisTemplate.hasKey(key);
        if(isExist){
            //1、存在，重新设置该值
            log.info("存在，访问值+1");
            Integer newValue=Integer.valueOf(stringRedisTemplate.opsForValue().get(key))+1;
            log.info("newValue:"+newValue);
            stringRedisTemplate.opsForValue().set(key,String.valueOf(newValue));
            if(newValue==5){
                //访问次数为5，弹出评分弹窗并删除该推荐信息，下次推荐重新评分
                stringRedisTemplate.delete(key);
                return true;
            }else{
                return false;
            }
        }
        //不存在，设置为1
        stringRedisTemplate.opsForValue().set(key,"1");
        return false;

    }

    //提交推荐景点评分和建议
    @GetMapping("/scoreSurveys")
    public R scoreSurveys(@RequestParam("userId")Integer userId,
                          @RequestParam("score")Integer score,
                          @RequestParam("advice")String advice){
        log.info("userId=="+userId);
        log.info("score=="+score);
        log.info("advice=="+advice);

        RecommendParameter nowRecommendParameter = algorithmConfigService.getNowRecommendParameter();
        policyDivision = nowRecommendParameter.getPolicyDivision();

        int likedNum = recommendService.getLikedNumByUserId(userId);
        int collectionNum = recommendService.getCollectionNumByUserId(userId);
        int commentNum = recommendService.getCommentNumByUserId(userId);
        int sum = likedNum + collectionNum + commentNum;
        log.info("用户" + userId + "的行为次数为:" + sum);
        //保存score到 behavior_recommend_res 或 tag_recommend_res表中
        if(sum < policyDivision){
            int ret = userManageService.addScoreByTag(userId, score,advice);
            if(ret!=1){
                return new R(false,"提交失败,系统出错了,请稍后再试");
            }
            return new R(true,"评分和建议提交成功!");
        }else{
            int ret2 = userManageService.addScoreByBehavior(userId, score,advice);
            if(ret2!=1){
                return new R(false,"提交失败,系统出错了,请稍后再试");
            }
            return new R(true,"评分和建议提交成功!");
        }
    }


    /**
     * 推荐总方法
     */
    @GetMapping("/recommend")
    public R recommend(@RequestParam(value="userId")Integer userId,
                       @RequestParam(value = "pn", defaultValue = "1") Integer pn) throws ParseException {

        //加载推荐算法的配置信息
        initAlgorithmConfig();

        log.info("===当前的配置信息===");
        log.info("locationWeight==="+locationWeight);
        log.info("tagWeight==="+tagWeight);
        log.info("likeCollectionCommentWeight==="+likeCollectionCommentWeight);
        log.info("clicksWeight==="+clicksWeight);
        log.info("keyWordWeight==="+keyWordWeight);

        log.info("policyDivision==="+policyDivision);
        log.info("similarityAttractionNum==="+similarityAttractionNum);
        log.info("neighboringNum==="+neighboringNum);

        log.info("likeWeight==="+likeWeight);
        log.info("collectionWeight==="+collectionWeight);
        log.info("commentWeight==="+commentWeight);
        log.info("clicksWeight1==="+clicksWeight1);
        log.info("browseWeight==="+browseWeight);

        log.info("init==="+init);
        log.info("m==="+m);
        log.info("finish==="+finish);
        log.info("==================");

        //统计用户的行为次数(点赞/收藏/评论)
        int likedNum = recommendService.getLikedNumByUserId(userId);
        int collectionNum = recommendService.getCollectionNumByUserId(userId);
        int commentNum = recommendService.getCommentNumByUserId(userId);
        int sum = likedNum + collectionNum + commentNum;
        log.info("用户" + userId + "的行为次数为:" + sum);

        //获取该用户的初始喜欢景点标签
        String tagStr = recommendService.getInitialTagByUserId(userId);
        log.info("当前用户的初始喜好标签===" + tagStr);


        //1.用户行为<policyDivision -> 基于标签
        if (sum < policyDivision) {
            //1.1 无初始标签
            if (tagStr == null) {
                //a. 无行为 -> 默认热门推荐
                if (sum == 0) {
                    recommendIdList = new LinkedList<>();
                    defaultRecommend(pn);
                } else { //b. 有行为
                    recommendIdList = new LinkedList<>();
                    recommendIdList = tagsRecommend(userId,pn);
                }
            } else { //1.2 有初始标签
                //a. 无行为
                if (sum == 0) {
                    recommendIdList = new LinkedList<>();
                    recommendIdList = tagsRecommend(userId,pn);
                } else { //b. 有行为
                    recommendIdList = new LinkedList<>();
                    recommendIdList = tagsRecommend(userId,pn);
                }
            }


        } else { //2.用户行为>=policyDivision -> 基于用户协同过滤
            recommendIdList = userBehaviorRecommend(userId,pn);
            if (recommendIdList == null) {
                log.info("找不到最邻近用户 使用基于标签推荐算法");
                //找不到最邻近用户 使用基于标签推荐算法
                tagsRecommend(userId,pn);
            }
        }

        log.info("推荐景点id的list===" + recommendIdList);


        //分页查找数据
        Page<Attraction> goodsPage = new Page<Attraction>(pn, 10);
        //条件查询
        QueryWrapper<Attraction> wrapper = new QueryWrapper<>();

        //按找recommendAttractionIdList的顺序查询 评分降序
        int length = recommendIdList.size();
        StringBuilder orderSql = new StringBuilder();
        orderSql.append("order by field(attraction_id,");
        for (int i = 0; i < length; i++) {
            if (i == 0) {
                orderSql.append(recommendIdList.get(i));
            } else {
                orderSql.append(",")
                        .append(recommendIdList.get(i));
            }
            if (i == length - 1) {
                orderSql.append(")");
            }
        }
        wrapper.in(CollectionUtil.isNotEmpty(recommendIdList), "attraction_id",
                recommendIdList).last(orderSql.toString());


        Page<Attraction> page = attractionService.page(goodsPage, wrapper);

        return new R(true,page,"景点推荐成功！");
    }

    /**
     * 初始化推荐算法配置信息
     */
    public void initAlgorithmConfig() {

        SimilarityConfig nowSimilarityConfig = algorithmConfigService.getNowSimilarityConfig();
        RecommendParameter nowRecommendParameter = algorithmConfigService.getNowRecommendParameter();
        UserBehaviorWeight nowUserBehaviorWeight = algorithmConfigService.getNowUserBehaviorWeight();
        DecayFunction nowDecayFunction = algorithmConfigService.getNowDecayFunction();

        locationWeight = nowSimilarityConfig.getLocationWeight(); //计算景点相似度中，地理位置权重
        tagWeight = nowSimilarityConfig.getTagWeight(); //计算景点相似度中，景点标签权重
        likeCollectionCommentWeight = nowSimilarityConfig.getLikeCollectionCommentWeight(); //计算景点相似度中，点赞/收藏/评论权重
        clicksWeight = nowSimilarityConfig.getClicksWeight(); //计算景点相似度中，被点击次数权重
        keyWordWeight = nowSimilarityConfig.getKeyWordWeight(); //计算景点相似度中，关键词权重

        similarityAttractionNum = nowRecommendParameter.getSimilarityAttractionNum(); //基于标签，附加几个相似度高的景点
        neighboringNum = nowRecommendParameter.getNeighboringNum(); //基于用户行为，寻找几个最邻近用户
        policyDivision = nowRecommendParameter.getPolicyDivision(); //策略划分值(用户行为数)，<基于标签，>=协同过滤

        likeWeight = nowUserBehaviorWeight.getLikeWeight(); //基于用户行为，点赞权重
        collectionWeight = nowUserBehaviorWeight.getCollectionWeight(); //基于用户行为，收藏权重
        commentWeight = nowUserBehaviorWeight.getCommentWeight(); //基于用户行为，评论权重
        clicksWeight1 = nowUserBehaviorWeight.getClicksWeight(); //基于用户行为,点击次数系数
        browseWeight = nowUserBehaviorWeight.getBrowseWeight(); //基于用户行为,浏览时长系数

        init = nowDecayFunction.getInit(); //热度衰减函数，初始值，上限
        m = nowDecayFunction.getM(); //热度衰减函数，天数，x轴长度
        finish = nowDecayFunction.getFinish(); //热度衰减函数，最低值，下限

    }

    //用户-景点对应关系    用户id      景点      该用户对景点 点赞/收藏/评论的次数
    public static Map<Integer, Map<String, Integer>> user_attraction = new HashMap<>();
    //用户-标签对应关系    用户id      标签      用户对该标签的权重:点赞+1 收藏+2 评论+1 随时间衰减
    public static Map<Integer, Map<String, Double>> user_tags = new HashMap<>();
    //景点-标签对应关系    景点       标签      该景点对应标签权重:点赞+1 收藏+2 评论+1 随时间衰减
    public static Map<String, Map<String, Double>> attraction_tags = new HashMap<>();
    //标签-景点对应关系    标签       景点      景点被点赞/收藏/评论的次数
    public static Map<String, Map<String, Integer>> tag_attraction = new HashMap<>();

    //初始化  用户id     景点      该用户对景点 点赞/收藏/评论的次数
    public void addValueToMat1(Map<Integer, Map<String, Integer>> mat, Integer key, String value) {
        if (!mat.containsKey(key)) {
            mat.put(key, new HashMap<>());
            mat.get(key).put(value, 1);
        } else {
            if (!mat.get(key).containsKey(value)) {
                mat.get(key).put(value, 1);
            } else {
                mat.get(key).put(value, 1);
            }
        }
    }

    //初始化  标签       景点      景点被点赞/收藏/评论的次数
    public void addValueToMat2(Map<String, Map<String, Integer>> mat, String key, String value) {
        if (!mat.containsKey(key)) {
            mat.put(key, new HashMap<>());
            mat.get(key).put(value, 1);
        } else {
            if (!mat.get(key).containsKey(value)) {
                mat.get(key).put(value, 1);
            } else {
                mat.get(key).put(value, mat.get(key).get(value) + 1);
            }
        }
    }

    //初始化  景点       标签      该景点对应标签权重:点赞+1 收藏+2 评论+1
    public void addValueToMat3(Map<String, Map<String, Double>> mat, String key, String value, Double weight) {
        if (!mat.containsKey(key)) {
            mat.put(key, new HashMap<>());
            mat.get(key).put(value, weight);
        } else {
            if (!mat.get(key).containsKey(value)) {
                mat.get(key).put(value, weight);
            } else {
                mat.get(key).put(value, mat.get(key).get(value) + weight);
            }
        }
    }

    //初始化  用户id      标签      用户对该标签的权重:点赞+1 收藏+2 评论+1 随时间衰减
    public void addValueToMat4(Map<Integer, Map<String, Double>> mat, Integer key, String value, Double weight) {
        //用户id      标签      用户对该标签的权重:点赞+1 收藏+2 评论+1 随时间衰减
        if (!mat.containsKey(key)) { //不存在该用户
            mat.put(key, new HashMap<>());
            mat.get(key).put(value, weight);
        } else { //存在该用户
            if (!mat.get(key).containsKey(value)) { //该用户无该标签
                mat.get(key).put(value, weight);
            } else { //该用户有该标签
                mat.get(key).put(value, mat.get(key).get(value) + weight);
            }
        }
    }

    //基于标签的推荐算法----用户行为少于指定行为次数，并且有行为
    //预处理
    public List<Integer> tagsRecommend(Integer userId,Integer pn) throws ParseException {

        List<UserAttractionTag> list = tagRecommendService.list();
        log.info("查询的总行为："+list);
        //log.info(list.toString());

        user_attraction.clear();
        user_tags.clear();
        attraction_tags.clear();
        tag_attraction.clear();

        /**
         * 通过所有用户的行为 来计算各景点的权重 各景点标签的权重
         */
        for (UserAttractionTag userAttractionTag : list) {

            int uid = userAttractionTag.getUserId();
            String attractionName = userAttractionTag.getAttractionName();
            String attractionTag = userAttractionTag.getAttractionTag();
            Double weight = userAttractionTag.getWeight();


            Date time = userAttractionTag.getUpdateTime();

            //判断最新更新时间是否为空，若为空，ze则采用添加时间
            if(time==null){
                time=userAttractionTag.getAddTime();
            }

            Date nowTime = new Date();
            //计算距离上次点赞/收藏/评论的天数 根据热度衰减函数计算权重系数
            int dayDiffer = getDayDiffer(time, nowTime);
            double coefficient = exponential_decay(dayDiffer, init, m, finish);
            weight *= coefficient;


            //赋值
            addValueToMat1(user_attraction, uid, attractionName);
            addValueToMat4(user_tags, uid, attractionTag, weight);
            addValueToMat3(attraction_tags, attractionName, attractionTag, weight);
            addValueToMat2(tag_attraction, attractionTag, attractionName);
        }

        log.info(user_attraction.toString());
        log.info(user_tags.toString());
        log.info(attraction_tags.toString());
        log.info(tag_attraction.toString());


        //生成推荐列表
        List<String> recommendList = recommend_tags(userId);
        System.out.println("推荐列表：" + recommendList);

        //要保存的用户的推荐列表
        StringBuilder sbRecommendList = new StringBuilder();
        for(int i=0;i<recommendList.size();i++){
            sbRecommendList.append(recommendList.get(i));
            if(i != recommendList.size()-1){
                sbRecommendList.append(",");
            }
        }
        log.info("存储的推荐列表==="+sbRecommendList.toString());

        Map<String, Double> tagFreq = tagPopularity();
        System.out.println("标签流行度：" + tagFreq);

        //转为景点id
        for (String attractionName : recommendList) {
            recommendIdList.add(attractionService.getAttractionIdByName(attractionName));
        }
        log.info("---------"+String.valueOf(recommendService.getHaveBehaviorName(userId).size() == 0));

        //推荐列表
        log.info("recommendList===" + recommendList);


        //只有用户有行为 才能附加高度相似的景点
        //用户无行为 无法附加于用户有行为景点高度相似的景点
        List<String> similarity = new ArrayList<>();
        if(recommendService.getHaveBehaviorName(userId).size() != 0){
            //最相似的五个景点
            similarity = cosineSim(userId);

            log.info("最相似的五个景点" + similarity.toString());

            for (String attractionsName : similarity) {
                recommendIdList.add(attractionService.getAttractionIdByName(attractionsName));
            }

            StringBuffer sbAddOtherList = new StringBuffer();
            for (int i = 0; i < similarity.size(); i++) {
                sbAddOtherList.append(similarity.get(i));
                if (i != similarity.size() - 1) {
                    sbAddOtherList.append(",");
                }
            }
            log.info("存储的附加列表===" + sbAddOtherList.toString());

            ArrayList<String> haveBehavior = recommendService.getHaveBehaviorName(userId);
            StringBuffer haveBehaviorList = new StringBuffer();
            for (int i = 0; i < haveBehavior.size(); i++) {
                haveBehaviorList.append(haveBehavior.get(i));
                if (i != haveBehavior.size() - 1) {
                    haveBehaviorList.append(",");
                }
            }
            log.info("用户有行为的景点===" + haveBehaviorList);

            StringBuffer sbTagName = new StringBuffer();
            StringBuffer sbTagWeight = new StringBuffer();
            Map<String, Double> map = user_tags.get(userId);

            int i = 0;
            for (Map.Entry<String, Double> entry : map.entrySet()) {
                String tag = entry.getKey();
                Double weight = entry.getValue();
                sbTagName.append(tag);
                //保留两位小数
                String str = String.format("%.2f", weight);
                sbTagWeight.append(str);
                if (i != map.size() - 1) {
                    sbTagName.append(",");
                    sbTagWeight.append(",");
                }
                i++;
                log.info(tag + "===" + weight);
            }

            log.info("用户有行为的景点标签===" + sbTagName);
            log.info("对应景点标签的权重===" + sbTagWeight);

            TagRecommendRes TRR = userManageService.getTagRecommendResByUserId(userId);
            if (TRR == null) { //该用户没有对应推荐记录
                //将推荐列表保存到数据库中
                userManageService.saveRecommendListByTag(userId, sbRecommendList.toString(), sbAddOtherList.toString(), haveBehaviorList.toString(), sbTagName.toString(), sbTagWeight.toString());
            } else { //有 则更新推荐记录
                userManageService.updateRecommendListByTag(userId, sbRecommendList.toString(), sbAddOtherList.toString(), haveBehaviorList.toString(), sbTagName.toString(), sbTagWeight.toString());
            }

        }else{ //用户无操作行为 仅保存user_id 和 recommend_list
            TagRecommendRes TRR = userManageService.getTagRecommendResByUserId(userId);
            if (TRR == null) { //该用户没有对应推荐记录
                //将推荐列表保存到数据库中
                userManageService.saveRecommendListByTag(userId, sbRecommendList.toString(), null, null, null, null);
            } else { //有 则更新推荐记录
                userManageService.updateRecommendListByTag(userId, sbRecommendList.toString(), null, null, null, null);
            }
        }

        log.info("最终的推荐列表====" + recommendIdList.toString());

        return recommendIdList;
    }


    /**
     * 基于标签的推荐算法
     * 计算推荐列表
     */
    public List<String> recommend_tags(Integer userId) {
        //记录用户感兴趣的景点标签
        List<String> likeTag = new LinkedList<>();

        //获取用户的初始标签
        String tagStr = recommendService.getInitialTagByUserId(userId);
        if(initialTag != null){
            log.info("initialTag===" + initialTag);
            initialTag = tagStr.split(",");
        }

        Map<String, Double> recommendList = new HashMap<>();
        //获取该用户的 景点map: 景点名-景点操作次数
        Map<String, Integer> taggedItem = user_attraction.get(userId);

        //表明 用户无行为 结合初始标签即可 按照标签的流行度降序推荐
        if (taggedItem == null) {
            List<String> recommendRes = new ArrayList<>();
            Map<String, Double> tagFreq = tagPopularity();
            System.out.println("标签流行度：" + tagFreq);
            for(Map.Entry<String, Double> entry : tagFreq.entrySet()){
                String key = entry.getKey();
                for(int i=0;i<initialTag.length;i++){
                    if(key.equals(initialTag[i])){
                        List<String> list = attractionService.getAttractionNameByTag(initialTag[i]);
                        recommendRes.addAll(list);
                    }
                }
            }
            log.info("有初始标签无行为==="+recommendRes);
            return recommendRes;
        }


        for (Map.Entry<String, Double> entry : user_tags.get(userId).entrySet()) {
            String tag = entry.getKey(); //该用户对应 标签名
            Double weight = entry.getValue(); //对应标签权重
            likeTag.add(tag); //记录用户有行为的标签
            //获取标签对应的 景点名和景点被点赞/收藏/评论的次数
            for (Map.Entry<String, Integer> itemEntry : tag_attraction.get(tag).entrySet()) {
                String attractionName = itemEntry.getKey(); //景点名
                Integer num = itemEntry.getValue(); //数量

                //对于该用户没有行为的全部物品
                //计算 用户对感兴趣标签的权重*景点被点赞/收藏/评论过的次数作为 推荐系数
                if (!taggedItem.containsKey(attractionName)) { //该物品 用户没有
                    if (!recommendList.containsKey(attractionName)) { //该物品 不在推荐列表中
                        recommendList.put(attractionName, weight * num); //存入推荐列表中
                    } else { //该物品 在推荐列表 则
                        recommendList.put(attractionName, recommendList.get(attractionName) + weight * num);
                    }
                }
            }
        }
        LinkedHashMap<String, Double> collect = recommendList.entrySet().stream().sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        log.info(collect.toString());


        //顺序
        //1. 用户有行为的标签对应其它用户也有行为的景点 权重降序 基于标签算法
        //2. 用户有行为的标签对应其它用户无行为的景点 降序排序 遍历寻找
        //3. 结合用户初始的喜好标签

        //1.
        //list：记录由基于标签算出要推荐的景点
        LinkedList<String> list = new LinkedList<>();
        for (Map.Entry<String, Double> entry : collect.entrySet()) {
            list.add(entry.getKey());
        }

        //记录用户有行为的景点名
        ArrayList<String> haveBehaviorName = recommendService.getHaveBehaviorName(userId);
        log.info("用户有行为的景点名===" + haveBehaviorName);

        //2.
        //找有行为的标签对应其它无行为的景点
        //最终要推荐的景点名recommendAttractionList
        LinkedList<String> recommendAttractionList = list;
        for (String tag : likeTag) {
            List<String> attractionIdByTag = attractionService.getAttractionNameByTag(tag);
            for (String name : attractionIdByTag) {
                if (!haveBehaviorName.contains(name) && !list.contains(name)) {
                    //不存在于有行为的景点 和 本身就有推荐的list中
                    recommendAttractionList.add(name);
                }
            }
        }
        log.info("recommendAttractionList=====" + recommendAttractionList.toString());


        //获取该用户的初始喜欢景点标签
        if (tagStr == null) {
            log.info("该用户无初始喜好标签===");
            initialTag = null;
            return recommendAttractionList;
        }
        initialTag = tagStr.split(",");
        log.info("初始喜好标签===" + initialTag);

        //结合初始喜好标签 推送景点
        for (String tag : initialTag) {
            if (!likeTag.contains(tag)) {
                recommendAttractionList.addAll(attractionService.getAttractionNameByTag(tag));
            }
        }
        log.info("recommendAttractionList===" + recommendAttractionList.toString());

        return recommendAttractionList;
    }

    /**
     * 基于用户行为的协同过滤推荐算法
     * 预处理
     */
    public List<Integer> userBehaviorRecommend(Integer userId,Integer pn) throws ParseException {

        //获取所有用户id
        List<Integer> userIdList = recommendService.getAllUserId();

        List<AW> AWList = new ArrayList<>();

        for (Integer id : userIdList) {
            //1. 获取该用户点赞的所有景点id
            List<Integer> likedList = recommendService.getLikedByUserId(id);
            //2. 获取该用户收藏的所有景点id
            List<Integer> collectionList = recommendService.getCollectionByUserId(id);
            //3. 获取该用户评论的所有景点id
            List<Integer> commentList = recommendService.getCommentByUserId(id);
            //4. 获取该用户点击过的所有景点id - 对应点击次数
            List<Click> clickList = recommendService.getClicksByUserId(id);
            //5. 获取该用户所有浏览过的景点id - 对应时长
            List<BrowseTime> browseList = recommendService.getBrowseByUserId(id);
            log.info("userId=====" + id);
            log.info("点赞的景点id" + likedList.toString());
            log.info("收藏的景点id" + collectionList.toString());
            log.info("评论的景点id" + commentList.toString());
            log.info("点击过的景点id" + clickList.toString());
            log.info("浏览过的景点id" + browseList.toString());

            //4. 统计所有用户对所有点赞/收藏/评论过的景点的权重
            //点赞:likeWeight 收藏:collectionWeight 评论:commentWeight 点击次数:clicksWeight1 浏览时长(s):browseWeight

            //5. 统计所有用户的景点权重
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
                    weightList.add(new Weight(attractionsId, existWeight + collectionWeight));
                } else {
                    weightList.add(new Weight(attractionsId, collectionWeight));
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


            //6. 封装为AW类 便于进行算法计算
            AW aw = new AW(id, weightList);
            log.info("AW===" + aw.toString());
            AWList.add(aw);
        }

        log.info("=================");
        //6. 进行基于用户行为的协同过滤算法
        log.info(String.valueOf(userId));
        log.info(AWList.toString());

        //1. 得到了最邻近用户有行为的景点 但我们无行为的景点
        List<Weight> recommendationAttractions = recommend_usrBehavior(userId, AWList);
        if (recommendationAttractions == null) {
            return null;
        }
        log.info("1111"+recommendationAttractions);
        //要推荐给当前用户的景点id recommendAttractionsIdList
        ArrayList<Integer> recommendAttractionsIdList = new ArrayList<>();
        for (Weight weight : recommendationAttractions) {
            double attractionsWeight = weight.getWeight();
            int attractionsId = weight.getAttractionId();
            System.out.println("景点id为：" + attractionsId + " ,评分：" + attractionsWeight);
            recommendAttractionsIdList.add(weight.getAttractionId());
        }
        log.info("2222"+String.valueOf(recommendAttractionsIdList));

        //2. 结合用户的喜好标签 附加景点
        //获取当前用户有行为的景点 和 对应权重
        //u1为当前用户
        AW u1 = new AW(userId);
        for (AW user : AWList) {
            if (userId.equals(user.userId)) {
                u1 = user;
            }
        }
        List<Weight> rating1 = u1.getWeightList();
        //用户有行为的景点id
        ArrayList<Integer> list2 = new ArrayList<>();
        //当前用户 景点id - 权重
        HashMap<Integer, Double> rating1Map = new HashMap<>();
        for (Weight weight : rating1) {
            rating1Map.put(weight.getAttractionId(), weight.getWeight());
            list2.add(weight.getAttractionId());
        }
        log.info("当前用户 景点id - 权重" + rating1Map);

        //有行为的 景点标签 - 权重(累加)
        HashMap<String, Double> likeTagWeight = new HashMap<>();
        for (Map.Entry<Integer, Double> entry : rating1Map.entrySet()) {
            Integer attractionsId = entry.getKey();
            Double weight = entry.getValue();
            String tag = attractionService.getAttractionTagById(attractionsId);
            if (!likeTagWeight.containsKey(tag)) {
                likeTagWeight.put(tag, weight);
            } else {
                likeTagWeight.put(tag, likeTagWeight.get(tag) + weight);
            }
        }
        //按权重降序排序likeTagWeight
        HashMap<String, Double> collect = likeTagWeight.entrySet().stream().sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        log.info("当前用户 景点标签 - 权重:" + collect);


        List<Integer> list3 = attractionService.getAllAttractionId();
        list3.removeAll(recommendAttractionsIdList);
        list3.removeAll(list2);
        log.info("recommendAttractionsIdList===" + recommendAttractionsIdList);
        log.info("list2===" + list2);
        log.info("list3===" + list3);

        /**
         *  以下为获取用户的推荐信息 保存到数据库中 -> 基于行为过滤
         */

        List<Integer> recommendList = recommendAttractionsIdList;
        //获取推荐列表 存储到数据中
        StringBuffer sbRecommendList = new StringBuffer();
        for(int i=0;i<recommendList.size();i++){
            sbRecommendList.append(recommendList.get(i));
            if(i != recommendList.size()-1){
                sbRecommendList.append(",");
            }
        }


        //附加了N个相似景点的最终推荐列表 - recommendAttractionsIdListResult
        List<Integer> recommendAttractionsIdListResult = additionalSimilarAttraction(recommendAttractionsIdList, list2, list3, similarityAttractionNum);
        //获取附加的推荐列表 存储到数据库中
        StringBuffer sbAddOtherList = new StringBuffer();

        log.info("sbAddOtherList:"+sbAddOtherList);
        for(int i=0;i<addOtherList.size();i++){
            sbAddOtherList.append(addOtherList.get(i));
            if(i != addOtherList.size()-1){
                sbAddOtherList.append(",");
            }
        }
        log.info("推荐列表==="+sbRecommendList);
        log.info("附加景点==="+sbAddOtherList);

        StringBuffer sbNeighborList = new StringBuffer();
        for(int i=0;i<neighboringList.size();i++){
            sbNeighborList.append(neighboringList.get(i));
            if(i != neighboringList.size()-1){
                sbNeighborList.append(",");
            }
        }
        log.info("邻近用户==="+sbNeighborList);


        BehaviorRecommendRes TRR  = userManageService.getBehaviorRecommendResByUserId(userId);
        log.info("TTR=="+TRR);
        if (TRR == null) { //该用户没有对应推荐记录
            //将推荐列表保存到数据库中
            userManageService.saveRecommendListByBehavior(userId, sbRecommendList.toString(), sbAddOtherList.toString(), sbNeighborList.toString());
        } else { //有 则更新推荐记录
            userManageService.updateRecommendListByBehavior(userId, sbRecommendList.toString(), sbAddOtherList.toString(), sbNeighborList.toString());
        }

        log.info("最终推荐景点id列表===" + recommendAttractionsIdListResult);

        return recommendAttractionsIdListResult;
    }



    /**
     * 基于用户行为的协同过滤推荐算法
     * 计算推荐列表
     */
    public List<Weight> recommend_usrBehavior(Integer userId, List<AW> AWList) throws ParseException {
        //找到最近邻
        //key:相似度 value:用户id
        Map<Double, Integer> distances = computeNearestNeighbor(userId, AWList);

        //相似度
        List<Double> coefficientList = new ArrayList<>();

        Double max = Double.MIN_VALUE;
        //皮尔逊相关系数范围[-1,1]
        //取相关系数在[-1,1]中  绝对值最大的即相关性最大
        for (Map.Entry<Double, Integer> entry : distances.entrySet()) {
            Double coefficient = entry.getKey();
            coefficientList.add(coefficient);
            if (entry.getKey().isNaN() || coefficient < -1 || coefficient > 1) {
                continue;
            }
            max = Math.max(max, Math.abs(coefficient));
        }


        Collections.sort(coefficientList, new Comparator<Double>() {
            public int compare(Double o1, Double o2) {
                if (Math.abs(o1) > Math.abs(o2)) {
                    return -1;
                } else if (Math.abs(o1) < Math.abs(o2)) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        log.info(coefficientList.toString());

        //取最邻近的neighboringNum个用户
        int num = 0;
        List<Double> userList = new ArrayList<>();
        for (int i = 0; i < coefficientList.size(); i++) {
            //在[-1,1]范围内
            if (coefficientList.get(i) < 1 && coefficientList.get(i) > -1) {
                userList.add(coefficientList.get(i));
                num++;
            }
            if (num == neighboringNum) {
                break;
            }
        }
        log.info("最邻近的" + neighboringNum + "个用户，相似度为" + userList.toString());

        List<Integer> nearest = new ArrayList<>(); //最邻近的neighboringNum个用户的id
        neighboringList = nearest;
        for (Double n : userList) {
            Integer id = distances.get(n);
            nearest.add(id);
        }


        log.info("==================");
        System.out.println("皮尔逊相关系数最大为:" + max);
        System.out.println("最邻近的" + neighboringNum + "个用户的id为:" + nearest);
        if (nearest.size() == 0) { //找不到邻近用户
            log.info("找不到邻近用户(皮尔逊系数[-1,1])");
            return null;
        }

        //找到最近邻点赞/收藏/评论过的景点，但是我们没点赞/收藏/评论的景点，计算推荐

        List<AW> neighborRatings = new ArrayList<>();
        for (AW aw : AWList) {
            for (Integer id : nearest) {
                if (id == aw.getUserId()) {
                    neighborRatings.add(aw);
                }
            }

        }
        System.out.println("N个最近邻点赞/收藏/评论过的景点 -> " + neighborRatings);

        //找到当前用户点赞/收藏/评论过的景点
        AW userRatings = new AW();
        for (AW user : AWList) {
            if (userId == user.getUserId()) {
                userRatings = user;
            }
        }
        System.out.println("该用户点赞/收藏/评论过的景点 -> " + userRatings.getWeightList().toString());

        List<Weight> uList = userRatings.getWeightList();
        List<Integer> u1 = new ArrayList<>();
        for(Weight w : uList){
            u1.add(w.getAttractionId());
        }
        log.info(u1.toString());

        //根据自己和最邻近用户的景点 计算推荐的景点
        List<Weight> recommendationAttraction = new ArrayList<>();
        for (AW aw : neighborRatings) {
            for (Weight weight : aw.getWeightList()) {
                if (userRatings.find(weight.getAttractionId()) == null) {
                    //当前用户没有点赞/收藏/评论过的景点 加入推荐列表中
                    recommendationAttraction.add(weight);
                }
            }
        }
        log.info("recommendationAttraction==="+recommendationAttraction);

        //转为Set按attractionId取重
        Set<Weight> playerSet = new TreeSet<>(Comparator.comparing(Weight::getAttractionId));
        playerSet.addAll(recommendationAttraction);
        //转换回list
        ArrayList<Weight> list = new ArrayList<>(playerSet);

        //list按权重降序排列推荐
        Collections.sort(list);
        list.stream().distinct().collect(Collectors.toList());

        log.info("list==="+list);

        return list;
    }


    /**
     * @param list1:要附加的推荐列表
     * @param list2:该用户有行为的景点
     * @param list3:在list3中寻找与list2中相似景点 不包含list2和list1中的
     * @param N:附加N个最相似的景点
     * @return
     */
    public List<Integer> additionalSimilarAttraction(List<Integer> list1, List<Integer> list2
            , List<Integer> list3, int N) throws ParseException {
        //预处理：统计数据来获取标签流行度
        List<UserAttractionTag> list = tagRecommendService.list();
        user_attraction.clear();
        user_tags.clear();
        attraction_tags.clear();
        tag_attraction.clear();
        for (UserAttractionTag userAttractionTag : list) {
            int uid = userAttractionTag.getUserId();
            String attractionName = userAttractionTag.getAttractionName();
            String attractionTag = userAttractionTag.getAttractionTag();
            Double weight = userAttractionTag.getWeight();

            Date time = userAttractionTag.getUpdateTime();
            //判断最新更新时间是否为空，若为空，则采用添加时间
            if(time==null){
                time=userAttractionTag.getAddTime();
            }

            Date nowTime = new Date();
            //计算距离上次点赞/收藏/评论的天数 根据热度衰减函数计算权重系数
            int dayDiffer = getDayDiffer(time, nowTime);
            double coefficient = exponential_decay(dayDiffer, init, m, finish);
            weight *= coefficient;

            //赋值
            addValueToMat1(user_attraction, uid, attractionName);
            addValueToMat4(user_tags, uid, attractionTag, weight);
            addValueToMat3(attraction_tags, attractionName, attractionTag, weight);
            addValueToMat2(tag_attraction, attractionTag, attractionName);
        }

        //相似度结果集  景点id - 相似度
        Map<Integer, Double> simililarity = new HashMap<>();

        for (Integer attractionId1 : list2) {
            for (Integer attractionId2 : list3) {
                Double num = cosineSimilarity(attractionService.getAttractionByAttractionId(attractionId1),
                        attractionService.getAttractionByAttractionId(attractionId2));
                simililarity.put(attractionId2, num);
            }
        }

        //相似度降序
        LinkedHashMap<Integer, Double> collect = simililarity.entrySet().stream().sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        log.info("collect===" + collect);

        int num = 0;

        for (Map.Entry<Integer, Double> entry : collect.entrySet()) {
            log.info("景点相似度===" + entry.getValue());
            list1.add(entry.getKey());
            addOtherList.add(entry.getKey());
            num++;
            if (num == N) {
                break;
            }
        }

        return list1;

    }


    /**
     * 在给定username的情况下，计算其他用户和它的距离并排序
     */
    private Map<Double, Integer> computeNearestNeighbor(Integer userId, List<AW> AWList) {
        Map<Double, Integer> distances = new TreeMap<>();

        //u1为当前用户
        AW u1 = new AW(userId);
        for (AW user : AWList) {
            if (userId.equals(user.userId)) {
                u1 = user;
            }
        }

        //u2为其它用户
        for (int i = 0; i < AWList.size(); i++) {
            AW u2 = AWList.get(i);
            log.info("userId" + u2.getUserId());
            if (!u2.userId.equals(userId)) {
                //计算当前用户与其它用户的距离并排序
                double distance = pearson_dis(u2.getWeightList(), u1.getWeightList());
                //记录与其它用户的 距离 用户id
                distances.put(distance, u2.getUserId());
            }

        }
        System.out.println("该用户与其他用户的皮尔森相关系数 -> " + distances);

        return distances;
    }

    /**
     * 计算2个打分序列间的pearson距离
     * 选择公式四进行计算
     *
     * @param rating1:其它用户的景点权重list
     * @param rating2:当前用户的景点权重list
     * @return
     */
    private double pearson_dis(List<Weight> rating1, List<Weight> rating2) {
        HashMap<Integer, Double> rating1Map = new HashMap<>();
        HashMap<Integer, Double> rating2Map = new HashMap<>();
        for (Weight weight : rating1) {
            rating1Map.put(weight.getAttractionId(), weight.getWeight());
        }
        for (Weight weight : rating2) {
            rating2Map.put(weight.getAttractionId(), weight.getWeight());
        }

        log.info("rating1Map===" + rating1Map);
        log.info("rating2Map===" + rating2Map);

        //1. 筛选出rating1中rating2中公共的景点id
        ArrayList<Integer> commonId = new ArrayList<>();
        for (Map.Entry<Integer, Double> entry : rating1Map.entrySet()) {
            Integer attractionId = entry.getKey();
            if (rating2Map.containsKey(attractionId)) {
                commonId.add(attractionId);
            }
        }
        log.info(commonId.toString());

        int n = commonId.size();
        Double Ex = 0.0;
        Double Ey = 0.0;
        Double Exy = 0.0;
        Double Ex2 = 0.0;
        Double Ey2 = 0.0;

        //取共同的景点id
        for (Integer attractionId : commonId) {
            Double x = rating1Map.get(attractionId); //获取对应权重
            Double y = rating2Map.get(attractionId); //获取对应权重
            Ex += x; //累加权重
            Ey += y; //累加权重
            Exy += x * y; //累加权重积的和
            Ex2 += Math.pow(x, 2); //累加权重的平方和
            Ey2 += Math.pow(y, 2); //累加权重的平方和
        }

        //分子
        double numerator = Exy - Ex * Ey / n;
        //分母
        double denominator = Math.sqrt((Ex2 - Math.pow(Ex, 2) / n) * (Ey2 - Math.pow(Ey, 2) / n));

        log.info("n===" + n);
        log.info("Ex===" + Ex);
        log.info("Ey===" + Ey);
        log.info("Exy===" + Exy);
        log.info("Ex2===" + Ex2);
        log.info("Ey2===" + Ey2);
        log.info("numerator===" + numerator);
        log.info("denominator===" + denominator);
        log.info("===" + numerator / denominator);

        if (denominator == 0) return 0.0;

        return numerator / denominator;
    }


    /**
     * 计算两个景点的余弦相似度
     * 维度: 地理位置  被点赞/收藏/评论的次数  标签流行度  被点击次数  景点关键词
     * 占比   0.2           0.1             0，1        0，1       0，5
     * 返回最相似的五个景点名
     */
    public List<String> cosineSim(Integer userId) throws ParseException {
        ArrayList<Attraction> haveBehaviorAttraction = new ArrayList<>();
        //获取有行为的景点
        ArrayList<String> nameList = recommendService.getHaveBehaviorName(userId);
        for (String name : nameList) {
            haveBehaviorAttraction.add(attractionService.getAttractionByName(name));
        }

        //1.1 获取该用户有行为的景点标签
        List<String> haveBehaviorTag = recommendService.getHaveBehaviorTag(userId);
        //haveBehaviorTag去重
        List<String> tagList = haveBehaviorTag.stream().distinct().collect(Collectors.toList());

        //所有景点标签中去除 有行为的标签 初始喜好的标签
        List<String> allTag = attractionService.getAllTag();
        log.info("所有标签" + allTag.toString());
        log.info("有行为的标签" + haveBehaviorTag);
        if (initialTag != null) {
            log.info("初始喜好的标签" + Arrays.asList(initialTag));
            allTag.removeAll(Arrays.asList(initialTag));
        }
        allTag.removeAll(haveBehaviorTag);

        log.info("其它景点标签" + allTag.toString());

        //1.2 获取该用户初始喜好标签
        List<String> otherTag = allTag;

        //相似度结果集
        Map<String, Double> simililarity = new HashMap<>();


        //3. 在其它标签中筛选高相似度的景点
        log.info("otherTag===" + otherTag);
        List<Attraction> otherAttraction = new ArrayList<>();
        for (String tag : otherTag) {
            List<Attraction> attractionByTag = attractionService.getAttractionByTag(tag);
            otherAttraction.addAll(attractionByTag);
        }

        for (Attraction attraction1 : haveBehaviorAttraction) {
            for (Attraction attraction2 : otherAttraction) {
                Double res = cosineSimilarity(attraction1, attraction2);
                simililarity.put(attraction2.getAttractionName(), res);
            }
        }

        //相似度降序
        LinkedHashMap<String, Double> collect = simililarity.entrySet().stream().sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        //最相似的similarityAttractionNum个景点名
        List<String> similarity = new LinkedList<>();
        for (int i = 0; i < similarityAttractionNum; i++) {
            similarity.add((String) collect.keySet().toArray()[i]);
            log.info("相似度为===" + collect.values().toArray()[i]);
            log.info("景点为===" + similarity.get(i));
        }


        return similarity;
    }


    /**
     * 统计标签流行度
     */
    public Map<String, Double> tagPopularity() {
        Map<String, Double> tagFreq = new HashMap<>();
        for (Integer user : user_tags.keySet()) {
            //System.out.println("user==="+user);
            for (String tag : user_tags.get(user).keySet()) {
                //System.out.println("tag==="+tag);
                if (!tagFreq.containsKey(tag)) {
                    //不存在 加入该用户对应标签的数量
                    tagFreq.put(tag, user_tags.get(user).get(tag));
                } else {
                    //存在 累加上该用户对应标签的数量
                    tagFreq.put(tag, tagFreq.get(tag) + user_tags.get(user).get(tag));
                }
            }
        }
        //排序tagFreq 降序标签的流行度
        List<Map.Entry<String, Double>> list = new ArrayList<>(tagFreq.entrySet());
        list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        Map<String, Double> sortedTagFreq = new LinkedHashMap<>();
        for (Map.Entry<String, Double> entry : list) {
            sortedTagFreq.put(entry.getKey(), entry.getValue());
        }
        return sortedTagFreq;
    }


    /**
     * 计算余弦相似度
     *
     * @param attraction1:用户有行为的景点
     * @param attraction2:其它景点标签中对应的景点
     * @return
     */
    public Double cosineSimilarity(Attraction attraction1, Attraction attraction2) throws ParseException {

        //各景点标签的流行度
        Map<String, Double> tagFreq = tagPopularity();
        log.info("景点标签流行度===" + tagFreq);

        String name1 = attraction1.getAttractionName();
        String str1 = attraction1.getKeyWord();
        List<String> keyWord1 = Arrays.asList(str1.split(","));
        log.info(keyWord1.toString());

        String location1 = attraction1.getLocation();
        Integer likedNum1 = attraction1.getLikes();
        Integer collectionNum1 = attraction1.getCollections();
        Integer sum1 = likedNum1 + collectionNum1;
        String tag1 = attraction1.getTag();
        Double popularity1 = tagFreq.get(tag1);
        Integer clicks1 = attraction1.getClicks();

        String str2 = attraction2.getKeyWord();
        List<String> keyWord2 = Arrays.asList(str2.split(","));
        log.info(keyWord2.toString());

        String name2 = attraction2.getAttractionName();
        String location2 = attraction2.getLocation();
        Integer likedNum2 = attraction2.getLikes();
        Integer collectionNum2 = attraction2.getCollections();
        Integer sum2 = likedNum2 + collectionNum2;
        String tag2 = attraction2.getTag();
        Double popularity2 = tagFreq.get(tag2);
        Integer clicks2 = attraction2.getClicks();


        Double keyWordWeightNum = CosineSimilarity.getCosineSimilarity(keyWord1, keyWord2);
        log.info(keyWordWeightNum.toString());

        double a = 0; //地区的一个权重

        if (location1.equals(location2)) {
            a = locationWeight;
        }
        log.info("sum1===" + sum1);
        log.info("sum2===" + sum2);
        log.info("popularity1===" + popularity1);
        log.info("popularity2===" + popularity2);
        log.info("clicks1===" + clicks1);
        log.info("clicks2===" + clicks2);

        double ret = sum1 * sum2 + popularity1 * popularity2 + clicks1 * clicks2;
        double denominator = Math.sqrt(Math.pow(sum1, 2) + Math.pow(popularity1, 2) + Math.pow(clicks1, 2))
                * Math.sqrt(Math.pow(sum2, 2) + Math.pow(popularity2, 2) + Math.pow(clicks2, 2));

        double tlc = tagWeight+likeCollectionCommentWeight+clicksWeight;
        double res = (ret / denominator) * tlc + a + keyWordWeightNum * keyWordWeight;
        log.info("ret===" + ret);
        log.info("denominator===" + denominator);
        log.info("流行度,点/收/评,点击相似度===" + (ret / denominator) * tlc);
        log.info("地区相似度===" + a);
        log.info("标签相似度===" + keyWordWeightNum * keyWordWeight);
        log.info("地区===" + location1 + "=" + location2);
        log.info("景点:" + name1 + "===景点:" + name2 + "===相似度" + res);
        log.info("--------------------------------------------");
        return res;
    }


    /**
     * 热度系数衰减函数
     */
    public static double exponential_decay(double t, double init, double m, double finish) {
        double alpha = Math.log(init / finish) / m;
        double l = -Math.log(init) / alpha;
        double decay = Math.exp(-alpha * (t + l));
        return decay;
    }


    /**
     * 获取两段时间的相差的天数
     */
    public static int getDayDiffer(Date startDate, Date endDate) throws ParseException {
        //判断是否跨年
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        String startYear = yearFormat.format(startDate);
        String endYear = yearFormat.format(endDate);
        if (startYear.equals(endYear)) {
            /*  使用Calendar跨年的情况会出现问题    */
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);
            int startDay = calendar.get(Calendar.DAY_OF_YEAR);
            calendar.setTime(endDate);
            int endDay = calendar.get(Calendar.DAY_OF_YEAR);
            return endDay - startDay;
        } else {
            /*  跨年不会出现问题，需要注意不满24小时情况（2016-03-18 11:59:59 和 2016-03-19 00:00:01的话差值为 0）  */
            //  只格式化日期，消除不满24小时影响
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            long startDateTime = dateFormat.parse(dateFormat.format(startDate)).getTime();
            long endDateTime = dateFormat.parse(dateFormat.format(endDate)).getTime();
            return (int) ((endDateTime - startDateTime) / (1000 * 3600 * 24));
        }
    }


    /**
     * 热门默认推荐
     */
    public List<Integer> defaultRecommend(@RequestParam(value = "pn", defaultValue = "1") Integer pn) {
        List<Attraction> attractionList = attractionService.list();
        //key:id value:点赞和收藏的总数
        HashMap<Integer, Integer> attractionMap = new HashMap<>();

        for (Attraction attraction : attractionList) {
            int likedNum = attractionService.getLikedNumById(attraction.getAttractionId());
            int collectionNum = attractionService.getCollectionNumById(attraction.getAttractionId());
            attractionMap.put(attraction.getAttractionId(), likedNum + collectionNum);
        }
        log.info(attractionMap.toString());
        //key:id value:点赞和收藏的总数 按value降序排序
        LinkedHashMap<Integer, Integer> defaultRecommendMap = attractionMap.entrySet().stream().sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        log.info(defaultRecommendMap.toString());

        for (Map.Entry<Integer, Integer> map : defaultRecommendMap.entrySet()) {
            recommendIdList.add(map.getKey());
        }
        log.info(recommendIdList.toString());

        return recommendIdList;
    }

    /**
     * 判断该用户的weightList是否存在已经 点赞/评论/收藏的景点id
     * 存在 return 权重
     * 不存在 return 0
     */
    public double isExist(List<Weight> weightList, Integer attractionId) {
        for (Weight weight : weightList) {
            if (weight.getAttractionId() == attractionId) {
                return weight.getWeight();
            }
        }
        return 0.0;
    }
}
