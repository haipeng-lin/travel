package com.wen.shuzhi.rusticTourism.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户的推荐结果类 - 基于标签
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TagRecommendRes {
    private Integer userId; //用户id
    private String recommendList; //推荐列表
    private String addOtherList; //附加的n个相似度高的其它标签的景点
    private Integer score;  //用户对本次推荐的评分
    private String advice;  //用户的建议
    private String haveBehaviorList; //用户有行为的景点
    private String tagNameList; //用户有行为的景点标签名
    private String tagWeightList; //对应上一字段的景点标签权重
}
