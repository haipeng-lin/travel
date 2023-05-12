package com.wen.shuzhi.rusticTourism.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wen.shuzhi.rusticTourism.entity.Attraction;
import com.wen.shuzhi.rusticTourism.entity.BrowseTime;
import com.wen.shuzhi.rusticTourism.entity.Click;
import com.wen.shuzhi.rusticTourism.entity.StatsAttraction;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface AttractionMapper extends BaseMapper<Attraction> {

    //增加景点
    @Insert("insert into attraction(attraction_name,location,introduction,strategy,image_url,likes,collections,comments,clicks,browse_time,tag,key_word,add_time) " +
            "values(#{attractionName},#{location},#{introduction},#{strategy},#{imageUrl},#{likes},#{collections},#{comments},#{clicks},#{browseTime},#{tag},#{keyWord},#{addTime})")
    int insertAttraction(Attraction attraction);

    //删除景点
    @Delete("delete from attraction where attraction_id=#{attractionId}")
    int deleteAttraction(Integer attractionId);

    //根据景点id查询景点
    @Select("select * from attraction where attraction_id=#{attractionId}")
    Attraction getAttractionByAttractionId(Integer attractionId);

    //查询所有的景点
    @Select("select attraction_id from attraction")
    List<Integer> getAllAttractionId();

    //修改景点
    @Update("update attraction set attraction_name=#{attractionName},location=#{location},introduction=#{introduction},strategy=#{strategy},likes=#{likes},collections=#{collections}," +
            "image_url=#{imageUrl},tag=#{tag},comments=#{comments},clicks=#{clicks},browse_time=#{browseTime},key_word=#{keyWord},update_time=#{updateTime} where attraction_id=#{attractionId}")
    int updateAttraction(Attraction attraction);

    //点赞+1
    @Update("update attraction set likes=likes+1 where attraction_id =#{attractionId}")
    int likeAddOne(Integer attractionId);

    //点赞-1
    @Update("update attraction set likes=likes-1 where attraction_id =#{attractionId}")
    int likeSubOne(Integer attractionId);

    //收藏+1
    @Update("update attraction set collections=collections+1 where attraction_id =#{attractionId}")
    int collectionAddOne(Integer attractionId);

    //收藏-1
    @Update("update attraction set collections=collections-1 where attraction_id =#{attractionId}")
    int collectionSubOne(Integer attractionId);


    @Update("update attraction set browse_time=browse_time+#{browseTime} where attraction_id=#{attractionId}")
    int addAttractionBrowseTime(Integer attractionId,Long browseTime);


    //根据景点id查找景点标签
    @Select("select tag from attraction where attraction_id=#{attractionId}")
    String getAttractionTagById(Integer attractionId);

    //根据景点id查找景点名称
    @Select("select attraction_name from attraction where attraction_id=#{attractionId}")
    String getAttractionNameById(Integer attractionId);

    //根据
    @Select("select collections from attraction where attraction_id=#{attractionId}")
    int getCollectionNumById(Integer attractionId);

    //根据景点类别查看点赞数量、收藏数数量、点击数、浏览时间
    @Select("select sum(attraction.likes) likes,sum(attraction.collections) collections, sum(attraction.comments)comments,sum(attraction.clicks)clicks,sum(attraction.browse_time) browse_time\n" +
            "from attraction \n" +
            "where attraction.tag=#{tag}  ")
    StatsAttraction getStatsAttractionByTag(String tag);

    //统计所有景点的点赞数、收藏数、点击数、浏览时间
    @Select("select sum(likes) as likes,sum(collections) as collections,sum(comments) as comments,sum(clicks) as clicks,sum(browse_time) as browse_time\n" +
            "from attraction group by tag")
    List<StatsAttraction> listStatsAttraction();

    //根据景点id查看点赞数量、收藏数量、点击数、浏览时间
    @Select("select attraction.likes likes,attraction.collections collections,attraction.clicks clicks,attraction.browse_time browse_time\n" +
            "from attraction \n" +
            "where attraction.attraction_id=#{attractionId}")
    StatsAttraction getStatsAttractionByAttractionId(Integer attractionId);

    //根据省份分组查询景点数量
    @Select("select location,count(*) count\n" +
            "from attraction\n" +
            "group by location")
    List<Map<String, Integer>> getAttractionCountGroupByLocation();

    //根据标签分组查询景点数量
    @Select("select tag,count(*) count\n" +
            "from attraction\n" +
            "group by tag")
    List<Map<String, Integer>> getAttractionCountGroupByTag();

    //根据浏览时长降序查询前count个景点信息
    @Select("select attraction_name,browse_time\n" +
            "from attraction\n" +
            "order by browse_time desc \n" +
            "limit 0,#{count}")
    List<Map<String, Integer>> getAttractionOrderByBrowseTime(Integer count);

    //查询景点数量和点击数
    @Select("select count(*) as attractionNum,sum(clicks) as attractionClick\n" +
            "from attraction")
    Map<String,String> getAttractionNumAndClick();


    //根据标签查询所有景点
    @Select("select * from attraction where tag=#{tag}")
    List<Attraction> getAttractionByTag(String tag);

    //根据标签查询景点名称
    @Select("select attraction_name from attraction where tag=#{tag}")
    List<String> getAttractionNameByTag(String tag);

    //根据景点名称查询景点id
    @Select("select attraction_id from attraction where attraction_name=#{attractionName}")
    Integer getAttractionIdByName(String attractionName);


    @Select("select likes from attraction where attraction_id=#{attractionId}")
    int getLikedNumById(Integer attractionId);

    @Select("select attraction_id from attraction where tag=#{tag}")
    List<Integer> getAttractionIdByTag(String tag);

    @Select("select tag from attraction where attraction_name=#{attractionName}")
    String getAttractionTagByName(String attractionName);

    @Select("select tag from attraction group by tag")
    List<String> getAllTag();

    @Select("select * from attraction where attraction_name=#{attractionName}")
    Attraction getAttractionByName(String attractionName);

    @Update("update attraction set clicks=clicks+1 where attraction_id=#{attractionId}")
    int addAttractionClickNum(Integer attractionId);

}