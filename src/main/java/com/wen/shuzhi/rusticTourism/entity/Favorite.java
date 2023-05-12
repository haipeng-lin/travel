package com.wen.shuzhi.rusticTourism.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("favorite")
public class Favorite {
    private Integer favoriteId;
    private Integer attractionId;
    private Integer userId;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;


    public Favorite(Integer attractionId, Integer userId, Date addTime, Date updateTime) {
        this.attractionId = attractionId;
        this.userId = userId;
        this.addTime = addTime;
        this.updateTime = updateTime;
    }
}
