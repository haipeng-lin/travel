package com.wen.shuzhi.rusticTourism.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 热度衰减函数的三个参数配置
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DecayFunction {
    private Integer decayFunctionId; //自增主键id
    private double init; //初始值，上限
    private Integer m; //天数，x轴长度
    private double finish; //最低值，下限
    private Integer nowConfig; //1为当前采用的配置，0不是当前采用的配置
}
