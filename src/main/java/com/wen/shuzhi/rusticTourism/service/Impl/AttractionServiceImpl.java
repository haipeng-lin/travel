package com.wen.shuzhi.rusticTourism.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wen.shuzhi.rusticTourism.entity.Attraction;
import com.wen.shuzhi.rusticTourism.entity.BrowseTime;
import com.wen.shuzhi.rusticTourism.entity.Click;
import com.wen.shuzhi.rusticTourism.entity.StatsAttraction;
import com.wen.shuzhi.rusticTourism.mapper.AttractionMapper;
import com.wen.shuzhi.rusticTourism.service.AttractionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AttractionServiceImpl extends ServiceImpl<AttractionMapper, Attraction> implements AttractionService {
    @Autowired
    private AttractionMapper attractionMapper;

    @Override
    public int saveAttraction(Attraction attraction) {
        return attractionMapper.insertAttraction(attraction);
    }

    @Override
    public int deleteAttraction(Integer id) {
        return attractionMapper.deleteAttraction(id);
    }


    @Override
    public Attraction getAttractionByAttractionId(Integer attractionId) {
        return attractionMapper.getAttractionByAttractionId(attractionId);
    }

    @Override
    public int modifyAttraction(Attraction attraction) {
        return attractionMapper.updateAttraction(attraction);
    }


    @Override
    public int likeAddOne(Integer attractionId) {
        return attractionMapper.likeAddOne(attractionId);
    }

    @Override
    public int likeSubOne(Integer attractionId) {
        return attractionMapper.likeSubOne(attractionId);
    }

    @Override
    public List<Integer> getAllAttractionId() {

        return attractionMapper.getAllAttractionId();
    }

    @Override
    public String getAttractionNameById(Integer attractionId) {
        return attractionMapper.getAttractionNameById(attractionId);
    }

    @Override
    public int getCollectionNumById(Integer attractionId) {
        return attractionMapper.getCollectionNumById(attractionId);
    }

    @Override
    public int collectionAddOne(Integer attractionId) {
        return attractionMapper.collectionAddOne(attractionId);
    }
    @Override
    public int collectionSubOne(Integer attractionId) {
        return attractionMapper.collectionSubOne(attractionId);
    }

    @Override
    public StatsAttraction getStatsAttractionByTag(String tag) {
        return attractionMapper.getStatsAttractionByTag(tag);
    }

    @Override
    public StatsAttraction getStatsAttractionByAttractionId(Integer attractionId) {
        return attractionMapper.getStatsAttractionByAttractionId(attractionId);
    }

    @Override
    public List<StatsAttraction> listStatsAttraction() {
        return attractionMapper.listStatsAttraction();
    }


    @Override
    public List<Attraction> getAttractionByTag(String tag) {
        return attractionMapper.getAttractionByTag(tag);
    }

    @Override
    public List<Map<String, Integer>> getAttractionCountGroupByLocation() {
        return attractionMapper.getAttractionCountGroupByLocation();
    }

    @Override
    public List<Map<String, Integer>> getAttractionCountGroupByTag() {
        return attractionMapper.getAttractionCountGroupByTag();
    }

    @Override
    public List<Map<String, Integer>> getAttractionOrderByBrowseTime(Integer count) {
        return attractionMapper.getAttractionOrderByBrowseTime(count);
    }

    @Override
    public List<String> getAttractionNameByTag(String tag) {
        return attractionMapper.getAttractionNameByTag(tag);
    }

    @Override
    public Map<String, String> getAttractionNumAndClick() {
        return attractionMapper.getAttractionNumAndClick();
    }

    @Override
    public Integer getAttractionIdByName(String attractionName) {
        return attractionMapper.getAttractionIdByName(attractionName);
    }


    @Override
    public String getAttractionTagById(Integer attractionId) {
        return attractionMapper.getAttractionTagById(attractionId);
    }



    @Override
    public int getLikedNumById(Integer id) {
        return attractionMapper.getLikedNumById(id);
    }


    @Override
    public List<Integer> getAttractionIdByTag(String tag) {
        return attractionMapper.getAttractionIdByTag(tag);
    }

    @Override
    public List<String> getAllTag() {
        return attractionMapper.getAllTag();
    }

    @Override
    public Attraction getAttractionByName(String name) {
        return attractionMapper.getAttractionByName(name);
    }


    @Override
    public int addAttractionClickNum(Integer attractionId) {
        return attractionMapper.addAttractionClickNum(attractionId);
    }


    @Override
    public int addAttractionBrowseTime(Integer attractionId, long browseTime) {
        return attractionMapper.addAttractionBrowseTime(attractionId,browseTime);
    }

    @Override
    public int getCityIdByCityName(String cityName) {
        return attractionMapper.getCityIdByCityName(cityName);
    }


}

