package com.wen.shuzhi.rusticTourism.service.Impl;

/*
@author peng
@create 2023-04-30-16:29
@description 
*/

import com.wen.shuzhi.rusticTourism.entity.AttractionDataChange;
import com.wen.shuzhi.rusticTourism.mapper.AttractionDataChangeMapper;
import com.wen.shuzhi.rusticTourism.service.AttractionDataChangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;

@Service
public class AttractionDataChangeServiceImpl implements AttractionDataChangeService {


    @Autowired
    AttractionDataChangeMapper attractionDataChangeMapper;

    @Override
    public int insertAttractionDataChange(AttractionDataChange attractionDataChange) {
        return attractionDataChangeMapper.insertAttractionDataChange(attractionDataChange);
    }

    @Override
    public int deleteAttractionDataChange(Integer attractionDataChangeId) {
        return attractionDataChangeMapper.deleteAttractionDataChange(attractionDataChangeId);
    }

    @Override
    public List<AttractionDataChange> listAttractionDataChangeByTagGroupByDate(String tag, Integer count) {
        return attractionDataChangeMapper.listAttractionDataChangeByTagGroupByDate(tag,count);
    }

    @Override
    public AttractionDataChange getAttractionDataChangeByAttractionIdAndDate(Integer attractionId, Date addTime) {
        return attractionDataChangeMapper.getAttractionDataChangeByAttractionIdAndDate(attractionId,addTime);
    }

    @Override
    public int updateAttractionDataChange(AttractionDataChange attractionDataChange) {
        return attractionDataChangeMapper.updateAttractionDataChange(attractionDataChange);
    }
}
