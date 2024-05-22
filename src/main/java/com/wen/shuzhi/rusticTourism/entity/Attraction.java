package com.wen.shuzhi.rusticTourism.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

/**
 * 景点类
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("attraction")
public class Attraction {

    private Integer attractionId;   //主键id
    private String attractionName;  //景点名称
    private String location;        //地点
    private String introduction;    //简介
    private String strategy;        //攻略
    private String imageUrl;        //景点图片访问路径

    private Integer clicks;         //点击数
    private Integer likes;          //点赞数
    private Integer collections;    //收藏数
    private Integer comments;       //评论数
    private Integer browseTime;     //浏览时间
    private String tag;             //景点标签
    private String keyWord;         //关键词
    private Date addTime;           //添加时间
    private Date updateTime;        //修改时间

    public Attraction(String attractionName, String location, Integer likes, Integer collections, Integer comments) {
        this.attractionName = attractionName;
        this.location = location;
        this.likes = likes;
        this.collections = collections;
        this.comments = comments;
    }

    public Attraction(String attractionName, String location, String introduction, String strategy, String imageUrl, Integer clicks, Integer likes, Integer collections, Integer comments, Integer browseTime, String tag, String keyWord, Date addTime, Date updateTime) {
        this.attractionName = attractionName;
        this.location = location;
        this.introduction = introduction;
        this.strategy = strategy;
        this.imageUrl = imageUrl;
        this.clicks = clicks;
        this.likes = likes;
        this.collections = collections;
        this.comments = comments;
        this.browseTime = browseTime;
        this.tag = tag;
        this.keyWord = keyWord;
        this.addTime = addTime;
        this.updateTime = updateTime;
    }

}
