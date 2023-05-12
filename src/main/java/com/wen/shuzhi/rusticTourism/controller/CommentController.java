package com.wen.shuzhi.rusticTourism.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.wen.shuzhi.loginRegister.entity.R;
import com.wen.shuzhi.loginRegister.entity.User;
import com.wen.shuzhi.loginRegister.service.Impl.UserServiceImpl;
import com.wen.shuzhi.rusticTourism.entity.Attraction;
import com.wen.shuzhi.rusticTourism.entity.AttractionDataChange;
import com.wen.shuzhi.rusticTourism.entity.Comment;
import com.wen.shuzhi.rusticTourism.entity.UserAttractionTag;
import com.wen.shuzhi.rusticTourism.service.Impl.AttractionDataChangeServiceImpl;
import com.wen.shuzhi.rusticTourism.service.Impl.AttractionServiceImpl;
import com.wen.shuzhi.rusticTourism.service.Impl.CommentServiceImpl;
import com.wen.shuzhi.rusticTourism.service.Impl.UserAttractionTagServiceImpl;
import com.wen.shuzhi.rusticTourism.utils.PageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;


@Slf4j
@RestController
public class CommentController {

    @Autowired
    private CommentServiceImpl commentService;

    @Autowired
    private AttractionServiceImpl attractionsService;

    @Autowired
    private UserAttractionTagServiceImpl userAttractionTagService;

    @Autowired
    private AttractionDataChangeServiceImpl attractionDataChangeService;

    @Autowired
    private UserServiceImpl userService;

    //1、发布评论
    @PostMapping("/comment")
    public R postComment(Comment comment,
                         @RequestParam(value="pn",defaultValue = "1",required = false) Integer pn){

        log.info("用户提交的评论信息:"+comment);

        //1、检查评论是否为空
        if(comment.getCommentContent().length()==0){
            return new R(false,"还未填写评论,发布评论失败");
        }

        //2、检查评论的景点是否为空
        int attractionId = comment.getAttractionId();
        Attraction attraction = attractionsService.getAttractionByAttractionId(attractionId);
        if(attraction==null){
            return new R(false,"评论失败，评论的景点为空");
        }

        //3、设置添加时间
        comment.setAddTime(new Date());

        //4、判断是否添加成功
        int ret = commentService.saveComment(comment);
        if(ret!=1){
            //4.1、添加不成功：返回错误信息
            return new R(false,pn,"发布评论失败,系统出错了,请稍后再试");
        }

        //4.2、添加成功

        //5、添加到景点点击表中
        AttractionDataChange adc = attractionDataChangeService.getAttractionDataChangeByAttractionIdAndDate(attractionId, new java.sql.Date(new Date().getTime()));
        //log.info("查询的景点点击表信息为:"+adc);
        if(adc==null){
            //今天该景点还没有任何操作，新建一个景点数据变化表
            attractionDataChangeService.insertAttractionDataChange(
                    new AttractionDataChange(attraction.getAttractionId(),attraction.getAttractionName(),
                            0,0,0,1,
                            0,attraction.getTag(), new java.sql.Date(new Date().getTime() )));
        }else{
            //今天该景点已经存在，评论数+1：注意，实际上点击数也会+1，因为需要重新查询评论
            attractionDataChangeService.updateAttractionDataChange(new AttractionDataChange(adc.getAttractionDataChangeId(),
                    adc.getAttractionId(),adc.getAttractionName(), adc.getClicks(),
                    adc.getLikes(),adc.getCollections(),adc.getComments()+1,
                    adc.getBrowseTime(),adc.getTag(), new java.sql.Date(new Date().getTime())));
        }

        int userId = comment.getUserId();
        String attractionName = attractionsService.getAttractionNameById(attractionId);
        String attractionTag = attractionsService.getAttractionTagById(attractionId);

        //6、更新用户最近的行为时间 无则创建 有则更新时间累加权重+1
        UserAttractionTag userAttractionTag = userAttractionTagService.getUserAttractionTagByUserIdAndAttractionName(userId, attractionName);
        //log.info("查询的用户景点行为数据为"+userAttractionTag.toString());
        if(userAttractionTag == null){ //没有该景点的行为
            userAttractionTagService.insertUserAttractionTag(new UserAttractionTag(userId,attractionName,attractionTag, (double) 1,new Date(),null));
        }else{ //有该景点的行为

            userAttractionTag.setWeight(userAttractionTag.getWeight()+1);
            userAttractionTag.setUpdateTime(new Date());
            log.info("即将更新用户景点行为数据的信息为:"+userAttractionTag);
            userAttractionTagService.updateUserAttractionTag(userAttractionTag);
        }

        //3.1、添加成功：返回成功信息
        return new R(true,pn,"成功发表评论");
    }


    //2、分页查看评论
    @GetMapping("/comment")
    public R myComment(
                    @RequestParam(value = "attractionId",required = false)Integer attractionId,
                    @RequestParam(value = "userId",required = false)Integer userId,
                    @RequestParam(value="pn",defaultValue = "1") Integer pn){

        //分页查找数据
        Page<Comment> goodsPage = new Page<Comment>(pn,5);
        log.info("当前用户id为:"+userId);

        //分页构造器
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();

        log.info("查询评论attractionId为:"+attractionId);
        log.info("查询评论userId为:"+userId);

        //条件查询
        //有用户id
        if(!StringUtils.isEmpty(userId)){
            wrapper.eq("user_id",userId);
        }
        //有景点id
        if(!StringUtils.isEmpty(attractionId)){
            wrapper.eq("attraction_id",attractionId);
        }

        Page<Comment> page = commentService.page(goodsPage,wrapper);

        HashMap<String, Object> hashMap = new PageUtils().getNewCommentPage(userService,attractionsService, page);

        return new R(true,hashMap,"查询评论信息如下");
    }





}
