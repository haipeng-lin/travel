package com.wen.shuzhi.rusticTourism.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("blog_comment")
public class BlogComment {
    private Integer blogCommentId;  //自增主键id
    private Integer userId;         //用户id
    private Integer blogId;         //游记id
    private String content;         //评价内容
    private Integer like;           //点赞数
    private String addTime;         //创建时间
    private String updateTime;      //最新更新时间
}
