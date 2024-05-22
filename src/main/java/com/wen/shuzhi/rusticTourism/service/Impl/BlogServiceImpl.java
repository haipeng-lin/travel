package com.wen.shuzhi.rusticTourism.service.Impl;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wen.shuzhi.rusticTourism.entity.Blog;
import com.wen.shuzhi.rusticTourism.mapper.BlogMapper;
import com.wen.shuzhi.rusticTourism.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements BlogService {
    @Autowired
    private BlogMapper blogMapper;


}
