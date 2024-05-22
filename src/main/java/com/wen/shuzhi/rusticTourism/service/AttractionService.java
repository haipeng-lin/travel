package com.wen.shuzhi.rusticTourism.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wen.shuzhi.rusticTourism.entity.Attraction;
import com.wen.shuzhi.rusticTourism.entity.BrowseTime;
import com.wen.shuzhi.rusticTourism.entity.Click;
import com.wen.shuzhi.rusticTourism.entity.StatsAttraction;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;


@Service
public interface AttractionService extends IService<Attraction> {

    //增加景点
    int saveAttraction(Attraction attraction);

    //删除景点
    int deleteAttraction(Integer attractionId);

    //根据景点id查询景点信息
    Attraction getAttractionByAttractionId(Integer attractionId);

    //修改景点
    int modifyAttraction(Attraction attraction);

    //景点点赞+1
    int likeAddOne(Integer Attraction_id);

    //景点点赞-1
    int likeSubOne(Integer Attraction_id);

    List<Integer> getAllAttractionId();

    String getAttractionNameById(Integer AttractionId);

    int getCollectionNumById(Integer AttractionId);

    int collectionAddOne(Integer AttractionId);

    int collectionSubOne(Integer AttractionId);

    //统计开始
    StatsAttraction getStatsAttractionByTag(String tag);

    StatsAttraction getStatsAttractionByAttractionId(Integer attractionId);

    List<StatsAttraction> listStatsAttraction();
    
    List<Attraction> getAttractionByTag(String tag);

    List<Map<String, Integer>> getAttractionCountGroupByLocation();

    List<Map<String,Integer>> getAttractionCountGroupByTag();

    List<Map<String,Integer>> getAttractionOrderByBrowseTime(Integer count);

    //统计结束

    List<String> getAttractionNameByTag(String tag);

    Map<String,String> getAttractionNumAndClick();

    Integer getAttractionIdByName(String AttractionName);

    String getAttractionTagById(Integer AttractionId);


    int getLikedNumById(Integer id);

    List<Integer> getAttractionIdByTag(String tag);

    List<String> getAllTag();

    Attraction getAttractionByName(String name);




    int addAttractionClickNum(Integer attractionId);

    int addAttractionBrowseTime(Integer attractionId, long browseTime);

    // 通过城市名获取城市的id
    int getCityIdByCityName(String cityName);

}

