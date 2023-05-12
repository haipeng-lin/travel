package com.wen.shuzhi.rusticTourism.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wen.shuzhi.rusticTourism.entity.Comment;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Date;


@Mapper
public interface CommentMapper extends BaseMapper<Comment> {


    @Insert("insert into comment(attraction_id,user_id,comment_content,add_time) values(#{attractionId},#{userId},#{commentContent},#{addTime})")
    int insertComment(Comment comment);


    @Select("select count(*) comments\n" +
            "from comment\n" +
            "where comment.attraction_id=#{attractionId}")
    int getCommentsByAttractionId(int attractionId);

    @Select("select likes from attraction where attraction_id=#{attractionId}")
    int getLikedNumByAttractionsId(int attractionId);

}
