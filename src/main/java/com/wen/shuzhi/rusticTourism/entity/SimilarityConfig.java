package com.wen.shuzhi.rusticTourism.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 计算景点相似度权重表
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SimilarityConfig {
    private Integer similarityConfigId; //自增主键id
    private double locationWeight; //计算景点相似度中，地理位置权重
    private double tagWeight; //计算景点相似度中，景点标签权重
    private double likeCollectionCommentWeight; //计算景点相似度中，点赞/收藏/评论权重
    private double clicksWeight; //计算景点相似度中，被点击次数权重
    private double keyWordWeight; //计算景点相似度中，关键词权重
    private Integer nowConfig; //1为当前采用的配置，0不是当前采用的配置
}
