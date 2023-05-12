package com.wen.shuzhi.monitorSystem.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wen.shuzhi.monitorSystem.entity.WaterInfo;
import com.wen.shuzhi.monitorSystem.mapper.WaterMapper;
import com.wen.shuzhi.monitorSystem.service.WaterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WaterServiceImpl extends ServiceImpl<WaterMapper, WaterInfo> implements WaterService {
    @Autowired
    private WaterMapper waterMapper;

}
