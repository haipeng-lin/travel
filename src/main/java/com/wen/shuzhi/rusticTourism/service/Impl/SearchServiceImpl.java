package com.wen.shuzhi.rusticTourism.service.Impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wen.shuzhi.rusticTourism.entity.Attraction;
import com.wen.shuzhi.rusticTourism.mapper.SearchMapper;
import com.wen.shuzhi.rusticTourism.service.SearchService;
import org.springframework.stereotype.Service;

@Service
public class SearchServiceImpl extends ServiceImpl<SearchMapper, Attraction> implements SearchService {

}
