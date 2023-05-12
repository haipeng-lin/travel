package com.wen.shuzhi.rusticTourism.mapper;

/*
@author peng
@create 2023-03-26-22:14
@description 
*/

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wen.shuzhi.rusticTourism.entity.Like;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface LikeMapper extends BaseMapper<Like> {

    @Insert("insert into tb_like(attraction_id,user_id,add_time) values(#{attractionId},#{userId},#{addTime})")
    int saveLiked(Like like);

    @Delete("delete from tb_like where attraction_id=#{attractionId} and user_id=#{userId}")
    int deleteLike(Integer attractionId, Integer userId);

    @Delete("delete from tb_like where like_id=#{likeId}")
    int deleteLikeByLikeId(Integer likeId);

    @Select("select * from tb_like")
    List<Like> queryAllLikes();

    @Select("select * from tb_like where like_id=#{likeId}")
    Like queryLikeByLikeId(Integer likeId);

    @Select("select * from tb_like where user_id=#{userId}")
    List<Like> queryLikeByUserId(Integer userId);

    @Update("update tb_like set attraction_id=#{attractionId},user_id=#{userId},join_time=#{joinTime} where like_id=#{likeId}")
    int updateLike(Like like);


}
