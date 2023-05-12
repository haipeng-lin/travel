package com.wen.shuzhi.rusticTourism.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


/**
 * 浏览时长表
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BrowseTime {
    private Integer browseTimeId; //自增主键
    private Integer attractionId;
    private Integer userId;
    private Integer browseTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    public BrowseTime(Integer attractionId, Integer userId, Integer browseTime, Date addTime, Date updateTime) {
        this.attractionId = attractionId;
        this.userId = userId;
        this.browseTime = browseTime;
        this.addTime = addTime;
        this.updateTime = updateTime;
    }
}
