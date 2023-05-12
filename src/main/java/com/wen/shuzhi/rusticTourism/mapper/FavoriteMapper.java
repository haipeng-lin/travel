package com.wen.shuzhi.rusticTourism.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wen.shuzhi.rusticTourism.entity.Favorite;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {

    //插入收藏信息
    @Insert("insert into favorite(attraction_id,user_id,add_time) values(#{attractionId},#{userId},#{addTime})")
    int saveFavorite(Favorite favorite);

    //删除收藏信息
    @Delete("delete from favorite where attraction_id=#{attractionId} and user_id=#{userId}")
    int deleteFavorite(Integer attractionId, Integer userId);

    //查询所有收藏信息
    @Select("select * from favorite")
    List<Favorite> queryAllFavorites();

    //根据用户id查询收藏信息
    @Select("select * from favorite where user_id=#{userId}")
    List<Favorite> queryFavoriteByUserId(Integer userId);


    @Insert("insert into user_attraction_tag values(#{userId},#{attractionName},#{attractionTag},2,#{nowTime})")
    int addUserAttractionTag(Integer userId, String attractionName, String attractionTag, Date nowTime);

    @Update("update user_attraction_tag set time=#{nowTime},weight=weight+2 where user_id=#{userId} and attraction_name=#{attractionName}")
    int updateTime(Integer userId, String attractionName, Date nowTime);
}
