package com.wen.shuzhi.rusticTourism.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wen.shuzhi.rusticTourism.entity.Comment;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public interface CommentService extends IService<Comment> {
    int getLikedNumByAttractionsId(int attractionsId);

    int saveComment(Comment comment);

    int getCommentsByAttractionId(Integer attractionId);


}
