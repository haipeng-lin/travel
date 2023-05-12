package com.wen.shuzhi.rusticTourism.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//记录 某个用户对某个景点的权重
//权重占比  点赞:10 收藏:20 评论:10
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Weight implements Comparable<Weight>{
    private int attractionId; //景点id
    private double weight; //权重

    @Override
    public int compareTo(Weight o) {

        return weight > o.weight ? -1 : 1;
    }

    @Override
    public String toString() {
        return "Weight{" +
                "attractionId=" + attractionId +
                ", weight=" + weight +
                '}';
    }

}
