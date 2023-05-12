package com.wen.shuzhi.rusticTourism.entity;

/*
@author peng
@create 2023-04-18-22:44
@description 
*/


import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


/**
 * Click点击实体类
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("click")
public class Click {
    private Integer clickId; //自增主键
    private Integer attractionId;
    private Integer userId;
    private Integer clicks;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private java.util.Date addTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private java.util.Date updateTime;


    public Click(Integer attractionId, Integer userId, Integer clicks, Date addTime, Date updateTime) {
        this.attractionId = attractionId;
        this.userId = userId;
        this.clicks = clicks;
        this.addTime = addTime;
        this.updateTime = updateTime;
    }
}
