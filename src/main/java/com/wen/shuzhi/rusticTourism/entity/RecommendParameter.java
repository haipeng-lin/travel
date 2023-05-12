package com.wen.shuzhi.rusticTourism.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;


/**
 * 推荐策略参数表
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
public class RecommendParameter {
    private Integer recommendParameterId; //自增主键id
    private Integer policyDivision; //策略划分界限
    private Integer similarityAttractionNum; //推荐列表附加相似度高的景点个数
    private Integer neighboringNum; //寻找最邻近用户的个数
    private Integer nowConfig; //1:当前使用配置，0:不是保存的配置
}
