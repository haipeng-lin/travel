package com.wen.shuzhi.rusticTourism.entity;

/*
@author peng
@create 2023-04-30-15:00
@description 
*/


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;


/**
 * 记录景点数据变化
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AttractionDataChange {

    private Integer attractionDataChangeId;
    private Integer attractionId;
    private String attractionName;
    private Integer clicks;
    private Integer likes;
    private Integer collections;
    private Integer comments;
    private Integer browseTime;
    private String tag;

    private Date addTime;

    public AttractionDataChange(Integer attractionId, String attractionName, Integer clicks, Integer likes, Integer collections, Integer comments, Integer browseTime, String tag, Date addTime) {
        this.attractionId = attractionId;
        this.attractionName = attractionName;
        this.clicks = clicks;
        this.likes = likes;
        this.collections = collections;
        this.comments = comments;
        this.browseTime = browseTime;
        this.tag = tag;
        this.addTime = addTime;
    }
}
