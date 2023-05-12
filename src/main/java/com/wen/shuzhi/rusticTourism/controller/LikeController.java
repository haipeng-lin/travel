package com.wen.shuzhi.rusticTourism.controller;

/*
@author peng
@create 2023-03-27-22:02
@description 
*/

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.wen.shuzhi.loginRegister.entity.R;
import com.wen.shuzhi.loginRegister.service.Impl.UserServiceImpl;
import com.wen.shuzhi.rusticTourism.entity.Like;
import com.wen.shuzhi.rusticTourism.service.Impl.AttractionServiceImpl;
import com.wen.shuzhi.rusticTourism.service.Impl.LikeServiceImpl;
import com.wen.shuzhi.rusticTourism.utils.PageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@Slf4j
public class LikeController {

    @Autowired
    AttractionServiceImpl attractionsService;

    @Autowired
    UserServiceImpl userService;

    @Autowired
    private LikeServiceImpl likeService;


    //1、增加点赞信息
    @Transactional
    @PostMapping("/like")
    public R addLike(Like like,
                     @RequestParam(value="pn",defaultValue = "1")Integer pn){

        log.info("提交的点赞信息:"+like);

        //1、检查是否完全填写所填信息
        if(like.getAttractionId()==null||like.getUserId()==null||like.getAddTime()==null){
            return new R(false,"增加失败,还有信息未填写");
        }

        //2、判断是否添加成功
        int ret = likeService.saveLike(like);
        if(ret!=1){
            return new R(false,"添加失败,系统出错了,请稍后再试");
        }
        return new R(true,"添加点赞信息成功");

    }

    //2、删除点赞信息
    @Transactional
    @DeleteMapping("/like")
    public R deleteLike(@RequestParam(value = "likeId")Integer likeId,
                        @RequestParam(value="pn",defaultValue = "1") Integer pn){

        //1、判断点赞信息是否存在
        Like like = likeService.showLikeByLikeId(likeId);
        if(like==null){
            return new R(false,"删除失败,该点赞信息不存在");
        }

        //2、判断是否删除成功
        int ret = likeService.deleteLikeByLikeId(likeId);
        if(ret!=1){
            //2.1删除失败
            return new R(false,"删除失败,系统出错了,请重试");
        }

        //2.2、删除成功
        return new R(true,pn,"成功删除点赞编号为"+like.getLikeId()+"的点赞信息");
    }

    //3、分页查看点赞信息
    @GetMapping("/like")
    public R showLikes(
                    @RequestParam(value = "attractionId",required = false)Integer attractionId,
                    @RequestParam(value = "userId",required = false)Integer userId,
                    @RequestParam(value="pn",defaultValue = "1") Integer pn){

        log.info("查询点赞attractionId为:"+attractionId);
        log.info("查询点赞userId为:"+userId);

        //分页构造器
        Page<Like> goodsPage = new Page<Like>(pn,8);

        //条件查询
        QueryWrapper<Like> wrapper = new QueryWrapper<>();

        if(!StringUtils.isEmpty(userId)){
            wrapper.eq("user_id",userId);
        }

        if(!StringUtils.isEmpty(attractionId)){
            wrapper.eq("attraction_id",attractionId);
        }

        Page<Like> page = likeService.page(goodsPage,wrapper);
        HashMap<String, Object> hashMap = new PageUtils().getNewLikePage(userService,attractionsService,page);

        return new R(true,hashMap,"查询点赞信息如下");
    }

    //4、修改点赞信息
    @PutMapping("/like")
    @Transactional
    public R modifyLike(Like like,
                        @RequestParam(value="pn",required = false,defaultValue = "1")Integer pn){

        //1、判断点赞信息是否存在
        Like like2 = likeService.showLikeByLikeId(like.getLikeId());
        if(like2==null){
            return new R(false,"修改失败,该点赞信息不存在");
        }
        log.info("即将修改like的信息为:"+like);

        //2、判断是否修改成功
        int ret = likeService.modifyLike(like);
        if(ret!=1){
            //2.1修改失败
            return new R(false,"修改失败,请重试");
        }
        return new R(true,pn,"成功修改点赞编号为"+like.getLikeId()+"的点赞信息");

    }



}