package com.wen.shuzhi.rusticTourism.entity;


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
@TableName("comment")
public class Comment {
    private int commentId;
    private int attractionId;
    private int userId;
    private String commentContent;


    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;


    public Comment(int attractionId, int userId, String commentContent, Date addTime, Date updateTime) {
        this.attractionId = attractionId;
        this.userId = userId;
        this.commentContent = commentContent;
        this.addTime = addTime;
        this.updateTime = updateTime;
    }
}