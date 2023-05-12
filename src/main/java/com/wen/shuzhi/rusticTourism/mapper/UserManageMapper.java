package com.wen.shuzhi.rusticTourism.mapper;

import com.wen.shuzhi.loginRegister.entity.User;
import com.wen.shuzhi.rusticTourism.entity.BehaviorRecommendRes;
import com.wen.shuzhi.rusticTourism.entity.TagRecommendRes;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface UserManageMapper {

    @Select("select * from user")
    List<User> getAllUser();

    @Select("select * from user where user_id=#{userId}")
    User getUserByUserId(Integer userId);

    @Select("select * from tag_recommend_res where user_id=#{userId}")
    TagRecommendRes getTagRecommendResByUserId(Integer userId);

    @Insert("insert tag_recommend_res(user_id,recommend_list,add_other_list,have_behavior_list,tag_name_list,tag_weight_list) values(#{userId},#{recommendList},#{addOtherList},#{haveBehaviorList},#{tagNameList},#{tagWeightList})")
    int saveRecommendListByTag(Integer userId, String recommendList, String addOtherList, String haveBehaviorList, String tagNameList, String tagWeightList);

    @Update("update tag_recommend_res set recommend_list=#{recommendList},add_other_list=#{addOtherList},have_behavior_list=#{haveBehaviorList},tag_name_list=#{tagNameList},tag_weight_list=#{tagWeightList} where user_id=#{userId}")
    int updateRecommendListByTag(Integer userId, String recommendList, String addOtherList, String haveBehaviorList, String tagNameList, String tagWeightList);

    @Select("select * from behavior_recommend_res where user_id=#{userId}")
    BehaviorRecommendRes getBehaviorRecommendResByUserId(Integer userId);

    @Insert("insert behavior_recommend_res(user_id,recommend_list,add_other_list,neighbor_list) values(#{userId},#{recommendList},#{addOtherList},#{neighborList})")
    int saveRecommendListByBehavior(Integer userId, String recommendList, String addOtherList, String neighborList);

    @Update("update behavior_recommend_res set user_id=#{userId},recommend_list=#{recommendList},add_other_list=#{addOtherList},neighbor_list=#{neighborList} where user_id=#{userId}")
    int updateRecommendListByBehavior(Integer userId, String recommendList, String addOtherList, String neighborList);

    @Update("update behavior_recommend_res set score=#{score},advice=#{advice} where user_id=#{userId}")
    int addScoreByBehavior(Integer userId, Integer score,String advice);

    @Update("update tag_recommend_res set score=#{score},advice=#{advice} where user_id=#{userId}")
    int addScoreByTag(Integer userId, Integer score,String advice);
}
