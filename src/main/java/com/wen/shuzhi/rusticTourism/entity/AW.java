package com.wen.shuzhi.rusticTourism.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * 封装类
 * 封装了用户 和map<景点id，权重>
 */

public class AW {
    public Integer userId;
    //用户对每个景点的权重list
    public List<Weight> weightList = new ArrayList<>();

    //key:景点id value:权重
    //Map<Integer,Integer> map = new HashMap<>();


    public AW(Integer userId, List<Weight> weightList) {
        this.userId = userId;
        this.weightList = weightList;
    }

    public AW() {
    }

    public AW(Integer userId) {
        this.userId = userId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public List<Weight> getWeightList() {
        return weightList;
    }

    public void setWeightList(List<Weight> weightList) {
        this.weightList = weightList;
    }

    //    public AW(Integer userId, Map<Integer, Integer> map) {
//        this.userId = userId;
//        this.map = map;
//    }
    //给用户添加 景点-权重
    public AW set(Integer attractionId, int weight) {
        this.weightList.add(new Weight(attractionId, weight));
        return this;
    }

    //给用户添加 景点-权重
//    public AW set(Integer attractionId, Integer weight) {
//        this.map.put(attractionId, weight);
//        return this;
//    }

//    public Map<Integer, Integer> getMap() {
//        return map;
//    }
//
//    public void setMap(Map<Integer, Integer> map) {
//        this.map = map;
//    }

    //找该用户 指定景点的 权重实体类
    public Weight find(Integer attractionId) {
        for (Weight weight : weightList) {
            if (weight.getAttractionId() == attractionId) {
                return weight;
            }
        }
        return null;
    }

    //找该用户 指定景点的 权重实体类
//    public Weight find(Integer attractionId) {
//        Integer weight = map.get(attractionId);
//        if(weight != 0){
//            return new Weight(attractionId,weight);
//        }
//        return null;
//    }


    @Override
    public String toString() {
        return "AW{" +
                "userId=" + userId +
                ", weightList=" + weightList +
                '}';
    }
}




