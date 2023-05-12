package com.wen.shuzhi.rusticTourism.service;

/*
@author peng
@create 2023-05-11-14:09
@description 
*/


import com.baomidou.mybatisplus.extension.service.IService;
import com.wen.shuzhi.rusticTourism.entity.Click;
import org.springframework.stereotype.Service;

@Service
public interface ClickService extends IService<Click> {

    int insertClick(Click click);

    Click getClickByAttractionIdAndUserId(Integer attractionId,Integer userId);

    int updateClick(Click click);
}
