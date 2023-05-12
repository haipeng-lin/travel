package com.wen.shuzhi.rusticTourism.service.Impl;

/*
@author peng
@create 2023-03-26-22:16
@description 
*/

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wen.shuzhi.rusticTourism.entity.Like;
import com.wen.shuzhi.rusticTourism.mapper.LikeMapper;
import com.wen.shuzhi.rusticTourism.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LikeServiceImpl extends ServiceImpl<LikeMapper, Like> implements LikeService {

    @Autowired
    LikeMapper likeMapper;

    @Override
    public int saveLike(Like like) {
        return likeMapper.saveLiked(like);
    }

    @Override
    public int deleteLike(Integer attractionId, Integer userId) {
        return likeMapper.deleteLike(attractionId,userId);
    }

    @Override
    public int deleteLikeByLikeId(Integer likeId) {
        return likeMapper.deleteLikeByLikeId(likeId);
    }

    @Override
    public List<Like> showAllLikes() {
        return likeMapper.queryAllLikes();
    }

    @Override
    public Like showLikeByLikeId(Integer likeId) {
        return likeMapper.queryLikeByLikeId(likeId);
    }

    @Override
    public List<Like> showLikeByUserId(Integer userId) {

        return likeMapper.queryLikeByUserId(userId);
    }

    @Override
    public int modifyLike(Like like) {
        return likeMapper.updateLike(like);
    }

}
