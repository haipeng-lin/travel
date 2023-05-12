package com.wen.shuzhi.rusticTourism.service;

/*
@author peng
@create 2023-03-26-22:15
@description 
*/

import com.baomidou.mybatisplus.extension.service.IService;
import com.wen.shuzhi.rusticTourism.entity.Like;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LikeService extends IService<Like> {


    int saveLike(Like like);

    int deleteLike(Integer attractionId, Integer userId);

    int deleteLikeByLikeId(Integer likeId);

    List<Like> showAllLikes();

    Like showLikeByLikeId(Integer likeId);

    List<Like> showLikeByUserId(Integer userId);

    int modifyLike(Like like);



}
