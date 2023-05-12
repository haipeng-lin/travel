package com.wen.shuzhi.rusticTourism.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 基于用户行为的 推荐记录
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BehaviorRecommendRes {
    private Integer userId;
    private String recommendList;
    private String addOtherList;
    private String neighborList;
    private Integer score;
    private String advice;
}
