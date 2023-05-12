package com.wen.shuzhi.rusticTourism.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wen.shuzhi.rusticTourism.entity.Comment;
import com.wen.shuzhi.rusticTourism.mapper.CommentMapper;
import com.wen.shuzhi.rusticTourism.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    @Autowired
    private CommentMapper commentMapper;


    @Override
    public int getLikedNumByAttractionsId(int attractionsId) {
        return commentMapper.getLikedNumByAttractionsId(attractionsId);
    }

    @Override
    public int saveComment(Comment comment) {
        return commentMapper.insertComment(comment);
    }

    @Override
    public int getCommentsByAttractionId(Integer attractionId) {
        return commentMapper.getCommentsByAttractionId(attractionId);
    }

}
