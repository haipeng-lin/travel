package com.wen.shuzhi.rusticTourism.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.wen.shuzhi.loginRegister.entity.R;
import com.wen.shuzhi.loginRegister.service.Impl.UserServiceImpl;
import com.wen.shuzhi.rusticTourism.entity.Favorite;
import com.wen.shuzhi.rusticTourism.service.Impl.AttractionServiceImpl;
import com.wen.shuzhi.rusticTourism.service.Impl.FavoriteServiceImpl;
import com.wen.shuzhi.rusticTourism.utils.PageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;


@RestController
@Slf4j
public class FavoriteController {
    @Autowired
    FavoriteServiceImpl favoriteService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private AttractionServiceImpl attractionsService;

    //分页查找收藏信息
    @GetMapping("/favorite")
    public R myCollection(
                        @RequestParam(value = "attractionId",required = false)Integer attractionId,
                        @RequestParam(value = "userId",required = false)Integer userId,
                        @RequestParam(value="pn",defaultValue = "1") Integer pn){
        //分页构造器
        Page<Favorite> goodsPage = new Page<Favorite>(pn,8);
        log.info("查询收藏attractionId为:"+attractionId);
        log.info("查询收藏userId为:"+userId);

        //分页条件
        QueryWrapper<Favorite> wrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(userId)){
            wrapper.eq("user_id",userId);
        }
        if(!StringUtils.isEmpty(attractionId)){
            wrapper.eq("attraction_id",attractionId);
        }

        Page<Favorite> page = favoriteService.page(goodsPage,wrapper);

        HashMap<String, Object> hashMap = new PageUtils().getNewCollectionPage(userService,attractionsService, page);
        return new R(true,hashMap,"查询收藏信息如下");
    }


}
