package com.wen.shuzhi.rusticTourism.entity;

/*
@author peng
@create 2023-04-14-17:25
@description 
*/

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class SimpleAttraction {
    private Integer infoId;
    private Integer attractionId;
    private String attractionName;
    private String location;
    private String imageUrl;

    private String tag;
    private Integer userId;
    private String avatarImageUrl;//头像照片访问路径
    private String account;
    private String commentContent;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date joinTime;
}
