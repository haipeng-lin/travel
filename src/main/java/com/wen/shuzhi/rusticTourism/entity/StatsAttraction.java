package com.wen.shuzhi.rusticTourism.entity;

/*
@author peng
@create 2023-04-19-13:33
@description 
*/

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统计各个类别景点的点赞数、收藏数、评论数、点击数、浏览时间
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StatsAttraction {

    private Integer likes;
    private Integer collections;
    private Integer comments;
    private Integer clicks;
    private Integer browseTime;
}
