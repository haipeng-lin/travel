package com.wen.shuzhi.rusticTourism.mapper;

/*
@author peng
@create 2023-05-11-13:46
@description 
*/

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wen.shuzhi.rusticTourism.entity.BrowseTime;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;


@Mapper
public interface BrowseTimeMapper extends BaseMapper<BrowseTime> {

    @Insert("insert into browse_time(attraction_id,user_id,browse_time,add_time) values(#{attractionId},#{userId},#{browseTime},#{addTime})")
    int insertBrowseTime(BrowseTime browseTime);

    @Select("select * from browse_time where user_id=#{userId} and attraction_id=#{attractionId}")
    BrowseTime getBrowseTimeByAttractionIdAndUserId(Integer attractionId, Integer userId);

    @Update("update browse_time set browse_time=#{browseTime},update_time=#{updateTime} where user_id=#{userId} and attraction_id=#{attractionId}")
    int updateBrowseTime(BrowseTime browseTime);


}
