package com.wen.shuzhi.rusticTourism.service.Impl;

/*
@author peng
@create 2023-05-11-14:16
@description 
*/

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wen.shuzhi.rusticTourism.entity.Click;
import com.wen.shuzhi.rusticTourism.mapper.ClickMapper;
import com.wen.shuzhi.rusticTourism.service.ClickService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClickServiceImpl extends ServiceImpl<ClickMapper, Click> implements ClickService {

    @Autowired
    ClickMapper clickMapper;

    @Override
    public int insertClick(Click click) {
        return clickMapper.insertClick(click);
    }

    @Override
    public Click getClickByAttractionIdAndUserId(Integer attractionId, Integer userId) {
        return clickMapper.getClickByAttractionIdAndUserId(attractionId,userId);
    }


    @Override
    public int updateClick(Click click) {
        return clickMapper.updateClick(click);
    }
}
