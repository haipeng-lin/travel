package com.wen.shuzhi.rusticTourism.entity;

/*
@author peng
@create 2023-04-24-8:23
@description
*/

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 景点名称及其用户对某景点的权重
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AttractionTag {

    private String tagName;
    private Double tagWeight;

}
