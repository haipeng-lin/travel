package com.wen.shuzhi.rusticTourism.mapper;

import com.wen.shuzhi.rusticTourism.entity.DecayFunction;
import com.wen.shuzhi.rusticTourism.entity.RecommendParameter;
import com.wen.shuzhi.rusticTourism.entity.SimilarityConfig;
import com.wen.shuzhi.rusticTourism.entity.UserBehaviorWeight;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface AlgorithmConfigMapper {

    @Select("select * from similarity_config where now_config=1")
    SimilarityConfig getNowSimilarityConfig();

    @Update("update similarity_config set location_weight=#{locationWeight},tag_weight=#{tagWeight},like_collection_comment_weight=#{likeCollectionCommentWeight},clicks_weight=#{clicksWeight},key_word_weight=#{keyWordWeight} where similarity_config_id=#{similarityConfigId}")
    int modifySimilarityConfig(SimilarityConfig similarityConfig);

    @Update("update recommend_parameter set policy_division=#{policyDivision},similarity_attraction_num=#{similarityAttractionNum},neighboring_num=#{neighboringNum} where recommend_parameter_id=#{recommendParameterId}")
    int modifyRecommendParameter(RecommendParameter recommendParameter);

    @Select("select * from similarity_config")
    List<SimilarityConfig> getSimilarityConfig();

    @Select("select * from similarity_config where id=#{id}")
    SimilarityConfig getSimilarityConfigById(Integer id);

    @Insert("insert into similarity_config(location_weight,tag_weight,like_collection_comment_weight,clicks_weight,key_word_weight,now_config) values(#{locationWeight},#{tagWeight},#{likeCollectionCommentWeight},#{clicksWeight},#{keyWordWeight},0)")
    int addSimilarityConfig(SimilarityConfig similarityConfig);

    @Update("update similarity_config set now_config=0 where now_config=1")
    int noUseNowSimilarityWeight();

    @Update("update similarity_config set now_config=1 where similarity_config_id=#{similarityConfigId}")
    int useSimilarityWeight(Integer similarityConfigId);


    @Select("select * from recommend_parameter")
    List<RecommendParameter> getRecommendParameter();

    @Select("select * from recommend_parameter where id=#{id}")
    RecommendParameter getRecommendParameterById(Integer id);

    @Insert("insert into recommend_parameter(policy_division,similarity_attraction_num,neighboring_num,now_config) values(#{policyDivision},#{similarityAttractionNum},#{neighboringNum},0)")
    int addRecommendParameter(RecommendParameter recommendParameter);

    @Update("update recommend_parameter set now_config=0 where now_config=1")
    int noUseNowRecommendParameter();

    @Update("update recommend_parameter set now_config=1 where recommend_parameter_id=#{recommendParameterId}")
    int useRecommendParameter(Integer recommendParameterId);

    @Select("select * from user_behavior_weight")
    List<UserBehaviorWeight> getUserBehaviorWeight();

    @Select("select * from decay_function")
    List<DecayFunction> getDecayFunction();


    @Select("select * from recommend_parameter where now_config=1")
    RecommendParameter getNowRecommendParameter();

    @Select("select * from user_behavior_weight where id=#{id}")
    UserBehaviorWeight getBehaviorWeightById(Integer id);

    @Update("update user_behavior_weight set like_weight=#{likeWeight},collection_weight=#{collectionWeight},comment_weight=#{commentWeight},clicks_weight=#{clicksWeight},browse_weight=#{browseWeight} where user_behavior_weight_id=#{userBehaviorWeightId}")
    int modifyUserBehaviorWeight(UserBehaviorWeight userBehaviorWeight);


    @Update("update user_behavior_weight set now_config=0 where now_config=1")
    int noUseNowUserBehaviorWeight();

    @Update("update user_behavior_weight set now_config=1 where user_behavior_weight_id=#{id}")
    int useUserBehaviorWeight(Integer userBehaviorWeightId);

    @Update("update decay_function set now_config=0 where now_config=1")
    int noUseNowDecayFunction();

    @Update("update decay_function set now_config=1 where decay_function_id=#{decayFunctionId}")
    int useDecayFunction(Integer decayFunctionId);

    @Insert("insert into user_behavior_weight(like_weight,collection_weight,comment_weight,clicks_weight,browse_weight,now_config) values(#{likeWeight},#{collectionWeight},#{commentWeight},#{clicksWeight},#{browseWeight},0)")
    int addUserBehaviorWeight(UserBehaviorWeight userBehaviorWeight);

    @Select("select * from user_behavior_weight where now_config=1")
    UserBehaviorWeight getNowUserBehaviorWeight();



    @Select("select * from decay_function where decay_function_id =#{decayFunctionId}")
    DecayFunction getDecayFunctionById(Integer decayFunctionId);

    @Update("update decay_function set init=#{init},m=#{m},finish=#{finish} where decay_function_id=#{decayFunctionId}")
    int modifyDecayFunction(DecayFunction decayFunction);

    @Insert("insert into decay_function(init,m,finish,now_config) values(#{init},#{m},#{finish},0)")
    int addDecayFunction(DecayFunction decayFunction);


    @Select("select * from decay_function where now_config=1")
    DecayFunction getNowDecayFunction();
}
