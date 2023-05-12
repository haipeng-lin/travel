package com.wen.shuzhi.rusticTourism.controller;


import com.wen.shuzhi.loginRegister.entity.R;
import com.wen.shuzhi.rusticTourism.entity.DecayFunction;
import com.wen.shuzhi.rusticTourism.entity.RecommendParameter;
import com.wen.shuzhi.rusticTourism.entity.SimilarityConfig;
import com.wen.shuzhi.rusticTourism.entity.UserBehaviorWeight;
import com.wen.shuzhi.rusticTourism.service.Impl.AlgorithmConfigServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 推荐算法参数配置
 */

@Slf4j
@RequestMapping("/algorithmConfig")
@RestController
public class AlgorithmConfigController {

    @Autowired
    private AlgorithmConfigServiceImpl algorithmConfigService;


    //展示各种配置信息 - 当前采用的----暂时不用
    @GetMapping("/getAlgorithmConfig")
    public R getAlgorithmConfig() {

        List<Object> retList=new ArrayList<>();
        List<SimilarityConfig> similarityConfigList = algorithmConfigService.getSimilarityConfig();
        SimilarityConfig nowSimilarityConfig = new SimilarityConfig();
        //寻找当前采用的配置 - similarityConfig
        for (SimilarityConfig similarityConfig : similarityConfigList) {
            if (similarityConfig.getNowConfig() == 1) {
                nowSimilarityConfig = similarityConfig;
            }
        }
        similarityConfigList.remove(nowSimilarityConfig);
        List<SimilarityConfig> otherSimilarityConfig = similarityConfigList;

        retList.add(nowSimilarityConfig);
        retList.add(otherSimilarityConfig);

        List<RecommendParameter> recommendParameterList = algorithmConfigService.getRecommendParameter();
        RecommendParameter nowRecommendParameter = new RecommendParameter();
        //寻找当前采用的配置 - recommendParameter
        for (RecommendParameter recommendParameter : recommendParameterList) {
            if (recommendParameter.getNowConfig() == 1) {
                nowRecommendParameter = recommendParameter;
            }
        }
        recommendParameterList.remove(nowRecommendParameter);
        List<RecommendParameter> otherRecommendParameter = recommendParameterList;

        retList.add(nowRecommendParameter);
        retList.add(otherRecommendParameter);

        List<UserBehaviorWeight> userBehaviorWeightList = algorithmConfigService.getUserBehaviorWeight();
        UserBehaviorWeight nowUserBehaviorWeight = new UserBehaviorWeight();
        //寻找当前采用的配置 - UserBehaviorWeight
        for (UserBehaviorWeight userBehaviorWeight : userBehaviorWeightList) {
            if (userBehaviorWeight.getNowConfig() == 1) {
                nowUserBehaviorWeight = userBehaviorWeight;
            }
        }
        userBehaviorWeightList.remove(nowUserBehaviorWeight);
        List<UserBehaviorWeight> otherBehaviorWeight = userBehaviorWeightList;

        retList.add(nowUserBehaviorWeight);
        retList.add(otherBehaviorWeight);


        List<DecayFunction> decayFunctionList = algorithmConfigService.getDecayFunction();
        DecayFunction nowDecayFunction = new DecayFunction();
        //寻找当前采用的配置 - DecayFunction
        for (DecayFunction decayFunction : decayFunctionList) {
            if (decayFunction.getNowConfig() == 1) {
                nowDecayFunction = decayFunction;
            }
        }
        decayFunctionList.remove(nowDecayFunction);
        List<DecayFunction> otherDecayFunction = decayFunctionList;

        retList.add(nowDecayFunction);
        retList.add(otherDecayFunction);

        return new R(true,retList,"查找如下");
    }


    //获得当前推荐算法设置的各个配置
    @GetMapping("/curConfig")
    public R curParameter(){
        SimilarityConfig nowSimilarityConfig = algorithmConfigService.getNowSimilarityConfig();
        RecommendParameter nowRecommendParameter = algorithmConfigService.getNowRecommendParameter();
        UserBehaviorWeight nowUserBehaviorWeight = algorithmConfigService.getNowUserBehaviorWeight();
        DecayFunction nowDecayFunction = algorithmConfigService.getNowDecayFunction();
        log.info("nowSimilarityConfig"+nowSimilarityConfig);
        log.info("nowRecommendParameter"+nowRecommendParameter);
        log.info("nowUserBehaviorWeight"+nowUserBehaviorWeight);
        log.info("nowDecayFunction"+nowDecayFunction);
        List<Object> list=new ArrayList<>();
        list.add(nowDecayFunction);
        list.add(nowRecommendParameter);
        list.add(nowUserBehaviorWeight);
        list.add(nowSimilarityConfig);

        return new R(true,list,"当前推荐算法的各个配置如下");
    }

    //1、添加计算景点相似度
    @PostMapping("/similarityConfig")
    public R addSimilarityConfig(SimilarityConfig similarityConfig) {
        log.info("similarityConfig:"+similarityConfig);

        //1、判断权重之和是否为1
        if (similarityConfig.getLocationWeight() + similarityConfig.getTagWeight() + similarityConfig.getLikeCollectionCommentWeight()
                + similarityConfig.getClicksWeight() + similarityConfig.getKeyWordWeight() != 1) {
            return new R(false,"修改失败,权重总和要等于1.0");
        }


        int ret = algorithmConfigService.addSimilarityConfig(similarityConfig);
        if (ret != 1) {
            log.info("添加失败");
            return new R(false,"添加失败,系统出错了,请稍后再试");
        }
        return new R(true,"添加成功!");



    }

    //2、寻找计算景点相似度所有的配置 - similarityConfig
    @GetMapping("/similarityConfig")
    public R getAllSimilarityConfig(){

        List<Object> retList=new ArrayList<>();
        List<SimilarityConfig> similarityConfigList = algorithmConfigService.getSimilarityConfig();
        SimilarityConfig nowSimilarityConfig = new SimilarityConfig();
        //寻找当前采用的配置 - similarityConfig
        for (SimilarityConfig similarityConfig : similarityConfigList) {
            if (similarityConfig.getNowConfig() == 1) {
                nowSimilarityConfig = similarityConfig;
            }
        }
        similarityConfigList.remove(nowSimilarityConfig);
        List<SimilarityConfig> otherSimilarityConfig = similarityConfigList;

        retList.add(nowSimilarityConfig);
        retList.add(otherSimilarityConfig);

        return new R(true,retList,"获得SimilarityConfig的所有配置");
    }

    //3、修改计算景点相似度的参数
    @PutMapping("/similarityConfig")
    public R modifySimilarityWeight(SimilarityConfig similarityConfig) {
        //权重总和一定要等于1 才能修改
        log.info("similarityConfig:"+similarityConfig);

        //1、判断权重之和是否为1
        if (similarityConfig.getLocationWeight() + similarityConfig.getTagWeight() + similarityConfig.getLikeCollectionCommentWeight()
                + similarityConfig.getClicksWeight() + similarityConfig.getKeyWordWeight() != 1) {
            return new R(false,"修改失败,权重总和要等于1.0");
        }

        //2、判断是否修改成功
        int ret = algorithmConfigService.modifySimilarityConfig(similarityConfig);
        if (ret != 1) {
            return new R(false,"修改失败,系统出错了,请稍后再试");
        }
        return new R(true,"成功修改编号为"+similarityConfig.getSimilarityConfigId()+"的设置");
    }

    //4、应用选中的相似度参数配置
    @GetMapping("/useSimilarityConfig")
    public R useSimilarityWeight(@RequestParam(value="similarityConfigId")Integer similarityConfigId) {
        //1. 当前配置的now_config置为0
        int i = algorithmConfigService.noUseNowSimilarityWeight();
        if(i!=1){
            return new R(false,"应用失败,系统出错了,请稍后再试");
        }
        //2. 选中的配置now_config置为1
        int j = algorithmConfigService.useSimilarityWeight(similarityConfigId);
        if(j!=1){
            return new R(false,"应用失败,系统出错了,请稍后再试");
        }
        return new R(true,"成功应用编号为"+similarityConfigId+"的配置");
    }


    //1、添加推荐策略
    @PostMapping("/recommendParameter")
    public R addRecommendParameter(RecommendParameter recommendParameter) {

        log.info("recommendParameter:"+recommendParameter);

        int ret = algorithmConfigService.addRecommendParameter(recommendParameter);
        if(ret!=1){
            return new R(false,"添加失败,系统出错了,请稍后再试");
        }
        return new R(true,"添加推荐策略成功!");
    }

    //2、寻找推荐策略所有的配置 - recommendParameter
    @GetMapping("/recommendParameter")
    public R getAllRecommendParameter(){

        List<Object> retList=new ArrayList<>();

        List<RecommendParameter> recommendParameterList = algorithmConfigService.getRecommendParameter();
        RecommendParameter nowRecommendParameter = new RecommendParameter();
        //寻找当前采用的配置 - recommendParameter
        for (RecommendParameter recommendParameter : recommendParameterList) {
            if (recommendParameter.getNowConfig() == 1) {
                nowRecommendParameter = recommendParameter;
            }
        }
        recommendParameterList.remove(nowRecommendParameter);
        List<RecommendParameter> otherRecommendParameter = recommendParameterList;

        retList.add(nowRecommendParameter);
        retList.add(otherRecommendParameter);

        return new R(true,retList,"获得recommendParameter的所有配置");
    }

    //3、修改推荐策略
    @PutMapping("/recommendParameter")
    public R modifyRecommendParameter(RecommendParameter recommendParameter) {

        log.info("recommendParameter:"+recommendParameter);
        int ret = algorithmConfigService.modifyRecommendParameter(recommendParameter);
        if (ret != 1) {
            return new R(false,"修改失败,系统出错了,请稍后再试");
        }
        return new R(true,"成功修改编号为"+recommendParameter.getRecommendParameterId()+"的设置");

    }

    //4、采用选中的推荐策略参数配置
    @GetMapping("/useRecommendParameter")
    public R useRecommendParameter(@RequestParam(value="recommendParameterId")Integer recommendParameterId) {
        //1. 当前配置的now_config置为0
        int i = algorithmConfigService.noUseNowRecommendParameter();

        if(i!=1){
            return new R(false,"应用失败,系统出错了,请稍后再试");
        }
        //2. 选中的配置now_config置为1
        int j = algorithmConfigService.useRecommendParameter(recommendParameterId);
        if(j!=1){
            return new R(false,"应用失败,系统出错了,请稍后再试");
        }
        return new R(true,"成功应用编号为"+recommendParameterId+"的配置");
    }


    //1、添加用户行为参数
    @PostMapping("/userBehaviorWeight")
    public R addUserBehaviorWeight(UserBehaviorWeight userBehaviorWeight) {

        log.info("userBehaviorWeight:"+userBehaviorWeight);

        int ret = algorithmConfigService.addUserBehaviorWeight(userBehaviorWeight);
        if (ret != 1) {
            return new R(false,"添加失败,系统出错了,请稍后再试");
        }

        return new R(true,"添加成功!");

    }

    //2、获得用户行为的所有配置信息
    @GetMapping("/userBehaviorWeight")
    public R getAllUserBehaviorWeight(){

        List<Object> retList=new ArrayList<>();

        List<UserBehaviorWeight> userBehaviorWeightList = algorithmConfigService.getUserBehaviorWeight();
        UserBehaviorWeight nowUserBehaviorWeight = new UserBehaviorWeight();
        //寻找当前采用的配置 - UserBehaviorWeight
        for (UserBehaviorWeight userBehaviorWeight : userBehaviorWeightList) {
            if (userBehaviorWeight.getNowConfig() == 1) {
                nowUserBehaviorWeight = userBehaviorWeight;
            }
        }
        userBehaviorWeightList.remove(nowUserBehaviorWeight);
        List<UserBehaviorWeight> otherBehaviorWeight = userBehaviorWeightList;

        retList.add(nowUserBehaviorWeight);
        retList.add(otherBehaviorWeight);

        return new R(true,retList,"获得userBehaviorWeight的所有配置");
    }

    //3、修改用户行为参数
    @PutMapping("/userBehaviorWeight")
    public R modifyUserBehaviorWeight(UserBehaviorWeight userBehaviorWeight) {

        int ret = algorithmConfigService.modifyUserBehaviorWeight(userBehaviorWeight);
        if (ret != 1) {
            return new R(false,"修改失败,系统出错了,请稍后再试");
        }
        return new R(true,"成功修改编号为"+userBehaviorWeight.getUserBehaviorWeightId()+"的配置信息");

    }

    //4、采用选中的用户行为权重配置
    @GetMapping("/useUserBehaviorWeight")
    public R useUserBehaviorWeight(@RequestParam(value="userBehaviorWeightId")Integer userBehaviorWeightId) {

        //1. 当前配置的now_config置为0
        int i = algorithmConfigService.noUseNowUserBehaviorWeight();

        if(i!=1){
            return new R(false,"应用失败,系统出错了,请稍后再试");
        }
        //2. 选中的配置now_config置为1
        int j = algorithmConfigService.useUserBehaviorWeight(userBehaviorWeightId);
        if(j!=1){
            return new R(false,"应用失败,系统出错了,请稍后再试");
        }
        return new R(true,"成功应用编号为"+userBehaviorWeightId+"的配置");
    }



    //1、添加热度衰减函数的配置
    @PostMapping("/decayFunction")
    public R addDecayFunction(DecayFunction decayFunction) {

        log.info("decayFunction:"+decayFunction);

        int ret = algorithmConfigService.addDecayFunction(decayFunction);
        if (ret != 1) {
            return new R(false,"添加失败,系统出错了,请稍后再试");
        }

        return new R(true,"成功添加！");
    }

    //2、获得热度衰减函数的全部参数
    @GetMapping("/decayFunction")
    public R getAllDecayFunction(){
        List<Object> retList=new ArrayList<>();

        List<DecayFunction> decayFunctionList = algorithmConfigService.getDecayFunction();
        DecayFunction nowDecayFunction = new DecayFunction();
        //寻找当前采用的配置 - DecayFunction
        for (DecayFunction decayFunction : decayFunctionList) {
            if (decayFunction.getNowConfig() == 1) {
                nowDecayFunction = decayFunction;
            }
        }
        decayFunctionList.remove(nowDecayFunction);
        List<DecayFunction> otherDecayFunction = decayFunctionList;

        retList.add(nowDecayFunction);
        retList.add(otherDecayFunction);

        return new R(true,retList,"获得userBehaviorWeight的所有配置");
    }

    //3、修改热度衰减函数的参数
    @PutMapping("/decayFunction")
    public R modifyDecayFunction(DecayFunction decayFunction) {
        int ret = algorithmConfigService.modifyDecayFunction(decayFunction);
        if (ret != 1) {
            log.info("===修改成功");
            return new R(false,"修改失败,系统出错了,请稍后再试");
        }

        return new R(true,"成功修改编号为"+decayFunction.getDecayFunctionId()+"的配置信息");

    }


    //4、采用选中的衰减函数配置
    @GetMapping("/useDecayFunction")
    public R useDecayFunction(@RequestParam(value="decayFunctionId")Integer decayFunctionId) {
        //1. 当前配置的now_config置为0
        int i = algorithmConfigService.noUseNowDecayFunction();

        if(i!=1){
            return new R(false,"应用失败,系统出错了,请稍后再试");
        }
        //2. 选中的配置now_config置为1
        int j = algorithmConfigService.useDecayFunction(decayFunctionId);
        if(j!=1){
            return new R(false,"应用失败,系统出错了,请稍后再试");
        }
        return new R(true,"成功应用编号为"+decayFunctionId+"的配置");

    }

}
