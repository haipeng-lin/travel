package com.wen.shuzhi.rusticTourism.entity;

/*
@author peng
@create 2023-03-26-22:05
@description 
*/

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("tb_like")
public class Like {
    private Integer likeId;
    private Integer attractionId;
    private Integer userId;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    public Like(Integer attractionId, Integer userId, Date addTime, Date updateTime) {
        this.attractionId = attractionId;
        this.userId = userId;
        this.addTime = addTime;
        this.updateTime = updateTime;
    }
}
