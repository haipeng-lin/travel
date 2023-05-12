package com.wen.shuzhi.rusticTourism.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 标签：用户id  景点名  标签名  标签权重(点赞+1 收藏+2 评论+1)
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("user_attraction_tag")
public class UserAttractionTag {
    private Integer userAttractionTagId;
    private int userId;
    private String attractionName;
    private String attractionTag;
    private Double weight; //点赞+1 收藏+2 评论+1

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    public UserAttractionTag(int userId, String attractionName, String attractionTag, Double weight, Date addTime, Date updateTime) {
        this.userId = userId;
        this.attractionName = attractionName;
        this.attractionTag = attractionTag;
        this.weight = weight;
        this.addTime = addTime;
        this.updateTime = updateTime;
    }
}
