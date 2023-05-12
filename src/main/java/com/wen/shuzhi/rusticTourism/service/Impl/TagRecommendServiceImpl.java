package com.wen.shuzhi.rusticTourism.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wen.shuzhi.rusticTourism.entity.UserAttractionTag;
import com.wen.shuzhi.rusticTourism.mapper.TagRecommendMapper;
import com.wen.shuzhi.rusticTourism.service.TagRecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TagRecommendServiceImpl extends ServiceImpl<TagRecommendMapper, UserAttractionTag> implements TagRecommendService {
    @Autowired
    private TagRecommendMapper tagRecommendMapper;

}
