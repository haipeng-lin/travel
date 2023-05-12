package com.wen.shuzhi.rusticTourism.utils;

/*
@author peng
@create 2023-04-14-20:25
@description 
*/

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wen.shuzhi.loginRegister.entity.User;
import com.wen.shuzhi.loginRegister.service.Impl.UserServiceImpl;
import com.wen.shuzhi.rusticTourism.entity.*;
import com.wen.shuzhi.rusticTourism.service.Impl.AttractionServiceImpl;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class PageUtils {


    //丰富景点信息，并返回HashMap对象，作为返回数据
    public HashMap<String,Object> getNewLikePage(UserServiceImpl userService, AttractionServiceImpl attractionsService, Page page){

        log.info("原数组:"+page.getRecords().toArray());

        Object[] objects= page.getRecords().toArray();
        log.info("Object类："+objects);

        List<SimpleAttraction> simpleAttractions=new ArrayList<>();
        for(Object object:objects){
            Like like = new ObjectMapper().convertValue(object, Like.class);

            //根据attractionId查询景点信息
            Integer attractionId = like.getAttractionId();
            Attraction attraction = attractionsService.getAttractionByAttractionId(attractionId);

            //根据useId查询用户信息
            Integer userId = like.getUserId();
            User user = userService.getUserByUserId(userId);

            SimpleAttraction simpleAttraction = new SimpleAttraction(like.getLikeId(), attractionId, attraction.getAttractionName(), attraction.getLocation(), attraction.getImageUrl(), attraction.getTag(),userId,user.getAvatarImageUrl(),user.getAccount(),null,like.getAddTime());
            simpleAttractions.add(simpleAttraction);

        }
        log.info("现在的数组:"+simpleAttractions);

        HashMap<String,Object> hashMap = new HashMap();
        hashMap.put("records",simpleAttractions);
        hashMap.put("total",page.getTotal());
        hashMap.put("size",page.getSize());
        hashMap.put("current",page.getCurrent());
        hashMap.put("pages",page.getPages());
        return hashMap;
    }


    public HashMap<String,Object> getNewCollectionPage(UserServiceImpl userService, AttractionServiceImpl attractionsService, Page page){
        log.info("原数组:"+page.getRecords().toArray());

        Object[] objects= page.getRecords().toArray();
        log.info("Object类："+objects);

        List<SimpleAttraction> simpleAttractions=new ArrayList<>();
        for(Object object:objects){
            Favorite favorite = new ObjectMapper().convertValue(object, Favorite.class);

            //根据attractionId查询景点信息
            Integer attractionId = favorite.getAttractionId();
            Attraction attraction = attractionsService.getAttractionByAttractionId(attractionId);

            //根据useId查询用户信息
            Integer userId = favorite.getUserId();
            User user = userService.getUserByUserId(userId);


            SimpleAttraction simpleAttraction = new SimpleAttraction(favorite.getFavoriteId(), attractionId, attraction.getAttractionName(), attraction.getLocation(), attraction.getImageUrl(), attraction.getTag(),userId,user.getAvatarImageUrl(),user.getAccount(),null, favorite.getAddTime());
            simpleAttractions.add(simpleAttraction);

        }
        log.info("现在的数组:"+simpleAttractions);

        HashMap<String,Object> hashMap = new HashMap();
        hashMap.put("records",simpleAttractions);
        hashMap.put("total",page.getTotal());
        hashMap.put("size",page.getSize());
        hashMap.put("current",page.getCurrent());
        hashMap.put("pages",page.getPages());
        return hashMap;
    }

    public HashMap<String,Object> getNewCommentPage(UserServiceImpl userService, AttractionServiceImpl attractionsService, Page page){

        log.info("原数组:"+page.getRecords().toArray().toString());

        Object[] objects= page.getRecords().toArray();
        log.info("Object类："+objects);

        List<SimpleAttraction> simpleAttractions=new ArrayList<>();
        for(Object object:objects){
            //将object类转化为comment类
            Comment comment = new ObjectMapper().convertValue(object, Comment.class);

            //根据attractionId查询景点信息
            Integer attractionId = comment.getAttractionId();
            Attraction attraction = attractionsService.getAttractionByAttractionId(attractionId);

            //根据useId查询用户信息
            Integer userId = comment.getUserId();
            User user = userService.getUserByUserId(userId);

            SimpleAttraction simpleAttraction = new SimpleAttraction(comment.getCommentId(), attractionId, attraction.getAttractionName(), attraction.getLocation(), attraction.getImageUrl(), attraction.getTag(),userId,user.getAvatarImageUrl(),user.getAccount(),comment.getCommentContent(),comment.getAddTime());
            simpleAttractions.add(simpleAttraction);

        }
        log.info("现在的数组:"+simpleAttractions);

        HashMap<String,Object> hashMap = new HashMap();
        hashMap.put("records",simpleAttractions);
        hashMap.put("total",page.getTotal());
        hashMap.put("size",page.getSize());
        hashMap.put("current",page.getCurrent());
        hashMap.put("pages",page.getPages());
        return hashMap;
    }
}
