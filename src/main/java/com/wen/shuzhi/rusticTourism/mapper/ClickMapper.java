package com.wen.shuzhi.rusticTourism.mapper;

/*
@author peng
@create 2023-05-11-13:55
@description 
*/

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wen.shuzhi.rusticTourism.entity.Click;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ClickMapper extends BaseMapper<Click> {

    //插入点击表
    @Insert("insert into click(attraction_id,user_id,clicks,add_time) values(#{attractionId},#{userId},#{clicks},#{addTime})")
    int insertClick(Click click);

    //根据景点id和用户id查询点击记录
    @Select("select * from click where attraction_id=#{attractionId} and user_id=#{userId}")
    Click getClickByAttractionIdAndUserId(Integer attractionId, Integer userId);

    //根据景点id和用户id修改点击表
    @Update("update click set clicks=#{clicks},update_time=#{updateTime} where attraction_id=#{attractionId} and user_id=#{userId}")
    int updateClick(Click click);
}
