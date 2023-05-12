package com.wen.shuzhi.rusticTourism.service.Impl;

import com.wen.shuzhi.rusticTourism.entity.DecayFunction;
import com.wen.shuzhi.rusticTourism.entity.RecommendParameter;
import com.wen.shuzhi.rusticTourism.entity.SimilarityConfig;
import com.wen.shuzhi.rusticTourism.entity.UserBehaviorWeight;
import com.wen.shuzhi.rusticTourism.mapper.AlgorithmConfigMapper;
import com.wen.shuzhi.rusticTourism.service.AlgorithmConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlgorithmConfigServiceImpl implements AlgorithmConfigService {

    @Autowired
    private AlgorithmConfigMapper algorithmConfigMapper;


    @Override
    public int modifySimilarityConfig(SimilarityConfig similarityConfig) {
        return algorithmConfigMapper.modifySimilarityConfig(similarityConfig);
    }

    @Override
    public int modifyRecommendParameter(RecommendParameter recommendParameter) {
        return algorithmConfigMapper.modifyRecommendParameter(recommendParameter);
    }

    @Override
    public List<SimilarityConfig> getSimilarityConfig() {
        return algorithmConfigMapper.getSimilarityConfig();
    }

    @Override
    public SimilarityConfig getSimilarityConfigById(Integer id) {
        return algorithmConfigMapper.getSimilarityConfigById(id);
    }

    @Override
    public int addSimilarityConfig(SimilarityConfig similarityConfig) {
        return algorithmConfigMapper.addSimilarityConfig(similarityConfig);
    }

    @Override
    public int noUseNowSimilarityWeight() {
        return algorithmConfigMapper.noUseNowSimilarityWeight();
    }

    @Override
    public int useSimilarityWeight(Integer similarityConfigId) {
        return algorithmConfigMapper.useSimilarityWeight(similarityConfigId);
    }

    @Override
    public List<RecommendParameter> getRecommendParameter() {
        return algorithmConfigMapper.getRecommendParameter();
    }

    @Override
    public RecommendParameter getRecommendParameterById(Integer id) {
        return algorithmConfigMapper.getRecommendParameterById(id);
    }

    @Override
    public int addRecommendParameter(RecommendParameter recommendParameter) {
        return algorithmConfigMapper.addRecommendParameter(recommendParameter);
    }

    @Override
    public int useRecommendParameter(Integer recommendParameterId) {
        return algorithmConfigMapper.useRecommendParameter(recommendParameterId);
    }

    @Override
    public int noUseNowRecommendParameter() {

        return algorithmConfigMapper.noUseNowRecommendParameter();
    }

    @Override
    public List<UserBehaviorWeight> getUserBehaviorWeight() {

        return algorithmConfigMapper.getUserBehaviorWeight();
    }

    @Override
    public List<DecayFunction> getDecayFunction() {

        return algorithmConfigMapper.getDecayFunction();
    }

    @Override
    public UserBehaviorWeight getBehaviorWeightById(Integer id) {
        return algorithmConfigMapper.getBehaviorWeightById(id);
    }

    @Override
    public int modifyUserBehaviorWeight(UserBehaviorWeight userBehaviorWeight) {
        return algorithmConfigMapper.modifyUserBehaviorWeight(userBehaviorWeight);
    }

    @Override
    public int noUseNowUserBehaviorWeight() {
        return algorithmConfigMapper.noUseNowUserBehaviorWeight();
    }

    @Override
    public int useUserBehaviorWeight(Integer id) {
        return algorithmConfigMapper.useUserBehaviorWeight(id);
    }

    @Override
    public int noUseNowDecayFunction() {
        return algorithmConfigMapper.noUseNowDecayFunction();
    }


    @Override
    public int useDecayFunction(Integer decayFunctionId) {
        return algorithmConfigMapper.useDecayFunction(decayFunctionId);
    }

    @Override
    public int addUserBehaviorWeight(UserBehaviorWeight userBehaviorWeight) {
        return algorithmConfigMapper.addUserBehaviorWeight(userBehaviorWeight);
    }

    @Override
    public DecayFunction getDecayFunctionById(Integer decayFunctionId) {
        return algorithmConfigMapper.getDecayFunctionById(decayFunctionId);
    }

    @Override
    public int modifyDecayFunction(DecayFunction decayFunction) {
        return algorithmConfigMapper.modifyDecayFunction(decayFunction);
    }

    @Override
    public int addDecayFunction(DecayFunction decayFunction) {
        return algorithmConfigMapper.addDecayFunction(decayFunction);
    }

    @Override
    public RecommendParameter getNowRecommendParameter() {
        return algorithmConfigMapper.getNowRecommendParameter();
    }

    @Override
    public SimilarityConfig getNowSimilarityConfig() {
        return algorithmConfigMapper.getNowSimilarityConfig();
    }

    @Override
    public UserBehaviorWeight getNowUserBehaviorWeight() {
        return algorithmConfigMapper.getNowUserBehaviorWeight();
    }

    @Override
    public DecayFunction getNowDecayFunction() {
        return algorithmConfigMapper.getNowDecayFunction();
    }

}
