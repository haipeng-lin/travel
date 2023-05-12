package com.wen.shuzhi.rusticTourism.service.Impl;

/*
@author peng
@create 2023-05-11-14:37
@description 
*/

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wen.shuzhi.rusticTourism.entity.BrowseTime;
import com.wen.shuzhi.rusticTourism.mapper.BrowseTimeMapper;
import com.wen.shuzhi.rusticTourism.service.BrowseTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BrowseTimeServiceImpl extends ServiceImpl<BrowseTimeMapper, BrowseTime> implements BrowseTimeService {

    @Autowired
    BrowseTimeMapper browseTimeMapper;

    @Override
    public int insertBrowseTime(BrowseTime browseTime) {

        return browseTimeMapper.insertBrowseTime(browseTime);
    }

    @Override
    public BrowseTime getBrowseTimeByAttractionIdAndUserId(Integer attractionId, Integer userId) {
        return browseTimeMapper.getBrowseTimeByAttractionIdAndUserId(attractionId,userId);
    }


    @Override
    public int updateBrowseTime(BrowseTime browseTime) {

        return browseTimeMapper.updateBrowseTime(browseTime);
    }
}
