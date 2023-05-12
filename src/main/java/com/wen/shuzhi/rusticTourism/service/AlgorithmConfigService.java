package com.wen.shuzhi.rusticTourism.service;

import com.wen.shuzhi.rusticTourism.entity.DecayFunction;
import com.wen.shuzhi.rusticTourism.entity.RecommendParameter;
import com.wen.shuzhi.rusticTourism.entity.SimilarityConfig;
import com.wen.shuzhi.rusticTourism.entity.UserBehaviorWeight;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AlgorithmConfigService  {



    int addSimilarityConfig(SimilarityConfig similarityConfig);

    List<SimilarityConfig> getSimilarityConfig();

    SimilarityConfig getSimilarityConfigById(Integer id);

    int noUseNowSimilarityWeight();

    int useSimilarityWeight(Integer similarityConfigId);

    int modifySimilarityConfig(SimilarityConfig similarityConfig);

    SimilarityConfig getNowSimilarityConfig();




    RecommendParameter getNowRecommendParameter();

    int modifyRecommendParameter(RecommendParameter recommendParameter);

    List<RecommendParameter> getRecommendParameter();

    RecommendParameter getRecommendParameterById(Integer id);

    int addRecommendParameter(RecommendParameter recommendParameter);

    int useRecommendParameter(Integer recommendParameterId);

    int noUseNowRecommendParameter();




    UserBehaviorWeight getNowUserBehaviorWeight();

    List<UserBehaviorWeight> getUserBehaviorWeight();

    UserBehaviorWeight getBehaviorWeightById(Integer userBehaviorWeightId);

    int modifyUserBehaviorWeight(UserBehaviorWeight userBehaviorWeight);

    int noUseNowUserBehaviorWeight();

    int useUserBehaviorWeight(Integer userBehaviorWeightId);

    int noUseNowDecayFunction();

    int addUserBehaviorWeight(UserBehaviorWeight userBehaviorWeight);




    int useDecayFunction(Integer decayFunctionId);

    DecayFunction getDecayFunctionById(Integer decayFunctionId);

    int modifyDecayFunction(DecayFunction decayFunction);

    int addDecayFunction(DecayFunction decayFunction);

    DecayFunction getNowDecayFunction();

    List<DecayFunction> getDecayFunction();

}
