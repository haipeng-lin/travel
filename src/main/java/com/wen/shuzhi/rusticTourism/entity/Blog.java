package com.wen.shuzhi.rusticTourism.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("blog")
public class Blog {
    @JsonProperty(value="")
    @TableId(value = "blog_id", type = IdType.AUTO)
    private Integer blogId;        //自增主键
    private Integer userId;        //用户id
    private String title;          //标题
    private String headImageUrl;   //头图路径
    private String content;        //富文本内容
    private Integer liked;         //点赞数
    private Integer collection;    //收藏数
    private Integer browseCount;   //浏览量
    private String addTime;        //发布时间
    private String updateTime;     //最新修改时间

    public Blog(Integer userId, String title, String headImageUrl, String content, Integer liked, Integer collection, Integer browseCount, String addTime, String updateTime) {
        this.userId = userId;
        this.title = title;
        this.headImageUrl = headImageUrl;
        this.content = content;
        this.liked = liked;
        this.collection = collection;
        this.browseCount = browseCount;
        this.addTime = addTime;
        this.updateTime = updateTime;
    }


}
