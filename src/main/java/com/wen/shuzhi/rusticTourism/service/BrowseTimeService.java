package com.wen.shuzhi.rusticTourism.service;

/*
@author peng
@create 2023-05-11-14:09
@description 
*/


import com.wen.shuzhi.rusticTourism.entity.BrowseTime;
import org.springframework.stereotype.Service;

@Service
public interface BrowseTimeService {

    int insertBrowseTime(BrowseTime browseTime);

    BrowseTime getBrowseTimeByAttractionIdAndUserId(Integer attractionId,Integer userId);

    int updateBrowseTime(BrowseTime browseTime);
}
