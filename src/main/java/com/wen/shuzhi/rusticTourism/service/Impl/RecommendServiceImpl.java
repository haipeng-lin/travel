package com.wen.shuzhi.rusticTourism.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wen.shuzhi.rusticTourism.entity.BrowseTime;
import com.wen.shuzhi.rusticTourism.entity.Click;
import com.wen.shuzhi.rusticTourism.entity.Recommend;
import com.wen.shuzhi.rusticTourism.mapper.RecommendMapper;
import com.wen.shuzhi.rusticTourism.service.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class RecommendServiceImpl extends ServiceImpl<RecommendMapper, Recommend> implements RecommendService {
    @Autowired
    private RecommendMapper recommendMapper;

    @Override
    public List<Integer> getAllUserId() {
        return recommendMapper.getAllUserId();
    }

    @Override
    public List<Integer> getLikedByUserId(Integer userId) {
        return recommendMapper.getLikedByUserId(userId);
    }

    @Override
    public List<Integer> getCommentByUserId(Integer userId) {
        return recommendMapper.getCommentByUserId(userId);
    }

    @Override
    public List<Integer> getCollectionByUserId(Integer userId) {
        return recommendMapper.getCollectionByUserId(userId);
    }

    @Override
    public int getCommentNumByUserId(Integer userId) {
        return recommendMapper.getCommentNumByUserId(userId);
    }

    @Override
    public int getCollectionNumByUserId(Integer userId) {
        return recommendMapper.getCollectionNumByUserId(userId);
    }

    @Override
    public int getLikedNumByUserId(Integer userId) {
        return recommendMapper.getLikedNumByUserId(userId);
    }

    @Override
    public int isExist(Integer userId) {
        return recommendMapper.isExist(userId);
    }

    @Override
    public int saveInitialTags(Integer userId,String tags) {
        return recommendMapper.saveInitialTags(userId,tags);
    }

    @Override
    public String getInitialTagByUserId(Integer userId) {
        return recommendMapper.getInitialTagByUserId(userId);
    }

    @Override
    public ArrayList<String> getHaveBehaviorName(Integer userId) {
        return recommendMapper.getHaveBehaviorName(userId);
    }

    @Override
    public List<String> getHaveBehaviorTag(Integer userId) {
        return recommendMapper.getHaveBehaviorTag(userId);
    }


    @Override
    public List<Click> getClicksByUserId(Integer userId) {
        return recommendMapper.getClicksNumByUserId(userId);
    }

    @Override
    public List<BrowseTime> getBrowseByUserId(Integer userId) {
        return recommendMapper.getBrowseByUserId(userId);
    }
}
