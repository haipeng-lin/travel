package com.wen.shuzhi.rusticTourism.controller;

/*
@author peng
@create 2023-04-30-16:48
@description 
*/

import com.wen.shuzhi.loginRegister.entity.R;
import com.wen.shuzhi.rusticTourism.entity.AttractionDataChange;
import com.wen.shuzhi.rusticTourism.service.Impl.AttractionDataChangeServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class AttractionDataChangeController {

    @Autowired
    AttractionDataChangeServiceImpl attractionDataChangeService;

    //根据景点类别查询、日期分组查询景点数据变化，降序排列，查询前count个
    @GetMapping("/listAttractionDataChangeByTagGroupByDate")
    public R listAttractionDataChangeByTagGroupByDate(@RequestParam("tag")String tag,
                                                      @RequestParam("count")Integer count){
        List<AttractionDataChange> ret = attractionDataChangeService.listAttractionDataChangeByTagGroupByDate(tag,count);
        return new R(true,ret,"查找如下");
    }

}
