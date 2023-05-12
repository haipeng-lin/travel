package com.wen.shuzhi.rusticTourism.service;

/*
@author peng
@create 2023-04-30-16:23
@description 
*/

import com.wen.shuzhi.rusticTourism.entity.AttractionDataChange;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;

@Service
public interface AttractionDataChangeService {

    //保存新的一天景点数据变化
    public int insertAttractionDataChange(AttractionDataChange attractionDataChange);

    //根据id删除景点数据变化
    public int deleteAttractionDataChange(Integer attractionDataChangeId);

    //根据景点类别查询、日期分组查询景点数据变化，降序排列，查询前count个
    public List<AttractionDataChange> listAttractionDataChangeByTagGroupByDate(String tag, Integer count);

    //根据景点id和日期判断该景点是否存在
    public AttractionDataChange getAttractionDataChangeByAttractionIdAndDate(Integer attractionId, Date addTime);

    //根据id修改景点数据变化
    public int updateAttractionDataChange(AttractionDataChange attractionDataChange);


}
