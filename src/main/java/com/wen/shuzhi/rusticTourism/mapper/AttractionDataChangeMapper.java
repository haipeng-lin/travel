package com.wen.shuzhi.rusticTourism.mapper;

/*
@author peng
@create 2023-04-30-15:09
@description 
*/

import com.wen.shuzhi.rusticTourism.entity.AttractionDataChange;
import org.apache.ibatis.annotations.*;
import org.springframework.data.relational.core.sql.In;

import java.sql.Date;
import java.util.List;

@Mapper
public interface AttractionDataChangeMapper {

    //保存新的一天景点数据变化
    @Insert("insert into attraction_data_change(attraction_id,attraction_name,clicks,likes,collections,comments,browse_time,tag,add_time)\n" +
            "values(#{attractionId},#{attractionName},#{clicks},#{likes},#{collections},#{comments},#{browseTime},#{tag},#{addTime})")
    public int insertAttractionDataChange(AttractionDataChange attractionDataChange);

    //根据id删除景点数据变化
    @Delete("delete from attraction_data_change where attraction_data_change_id=#{attractionDataChangeId}")
    public int deleteAttractionDataChange(Integer attractionDataChangeId);

    //根据景点类别查询、日期分组查询景点数据变化，升序排列，查询前count个
    @Select("select sum(clicks) as clicks,sum(likes) as likes,sum(collections) as collections,sum(comments) as comments,sum(browse_time) as browse_time,tag,add_time\n" +
            "from attraction_data_change\n" +
            "where attraction_data_change.tag=#{tag}\n" +
            "group by add_time\n" +
            "order by add_time\n"+
            "desc\n"+
            "limit 0,#{count}")
    public List<AttractionDataChange> listAttractionDataChangeByTagGroupByDate(String tag,Integer count);

    //根据景点id和日期判断该景点是否存在
    @Select("select * \n" +
            "from attraction_data_change\n" +
            "where attraction_id=#{attractionId} and add_time=#{addTime}")
    public AttractionDataChange getAttractionDataChangeByAttractionIdAndDate(Integer attractionId, Date addTime);

    //根据id修改景点数据变化
    @Update("update attraction_data_change set attraction_id=#{attractionId},attraction_name=#{attractionName},clicks=#{clicks},likes=#{likes},collections=#{collections}," +
            "comments=#{comments},browse_time=#{browseTime} where attraction_data_change_id=#{attractionDataChangeId}")
    public int updateAttractionDataChange(AttractionDataChange attractionDataChange);
}
