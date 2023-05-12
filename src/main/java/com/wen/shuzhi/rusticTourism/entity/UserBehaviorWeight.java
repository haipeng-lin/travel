package com.wen.shuzhi.rusticTourism.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户各行为的权重表
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserBehaviorWeight {
    private Integer userBehaviorWeightId; //自增主键id
    private double likeWeight; //点赞的权重
    private double collectionWeight; //收藏的权重
    private double commentWeight; //评论的权重
    private double clicksWeight; //点击次数的系数
    private double browseWeight; //浏览时间的系数
    private Integer nowConfig; //1为当前采用的配置，0不是当前采用的配置
}
