package com.wen.shuzhi.rusticTourism.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wen.shuzhi.rusticTourism.entity.BrowseTime;
import com.wen.shuzhi.rusticTourism.entity.Click;
import com.wen.shuzhi.rusticTourism.entity.Recommend;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface RecommendMapper extends BaseMapper<Recommend> {

    @Select("select user_id from user")
    List<Integer> getAllUserId();

    @Select("select attraction_id from tb_like where user_id =#{userId}")
    List<Integer> getLikedByUserId(Integer userId);

    @Select("select attraction_id from comment where user_id =#{userId}")
    List<Integer> getCommentByUserId(Integer userId);

    @Select("select attraction_id from favorite where user_id =#{userId}")
    List<Integer> getCollectionByUserId(Integer userId);

    @Select("select count(*) from comment where user_id=#{userId}")
    int getCommentNumByUserId(Integer userId);

    @Select("select count(*) from favorite where user_id=#{userId}")
    int getCollectionNumByUserId(Integer userId);

    @Select("select count(*) from tb_like where user_id=#{userId}")
    int getLikedNumByUserId(Integer userId);

    @Select("select count(*) from initial_tag where user_id=#{userId}")
    int isExist(Integer userId);

    @Insert("insert into initial_tag(user_id,initial_tag) values(#{userId},#{tags})")
    int saveInitialTags(Integer userId,String tags);

    @Select("select initial_tag from initial_tag where user_id=#{userId}")
    String getInitialTagByUserId(Integer userId);

    @Select("select attraction_name from user_attraction_tag where user_id=#{userId}")
    ArrayList<String> getHaveBehaviorName(Integer userId);

    @Select("select attraction_tag from user_attraction_tag where user_id=#{userId}")
    List<String> getHaveBehaviorTag(Integer userId);

    @Select("select * from click where user_id =#{userId}")
    List<Click> getClicksNumByUserId(Integer userId);

    @Select("select * from browse_time where user_id=#{userId}")
    List<BrowseTime> getBrowseByUserId(Integer userId);

}
