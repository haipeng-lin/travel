package com.wen.shuzhi.rusticTourism.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wen.shuzhi.rusticTourism.entity.Favorite;
import com.wen.shuzhi.rusticTourism.mapper.FavoriteMapper;
import com.wen.shuzhi.rusticTourism.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {
    @Autowired
    private FavoriteMapper favoriteMapper;

    @Override
    public int saveFavorite(Favorite favorite) {
        return favoriteMapper.saveFavorite(favorite);
    }

    @Override
    public int deleteFavorite(Integer attractionId, Integer userId) {
        return favoriteMapper.deleteFavorite(attractionId,userId);
    }

    @Override
    public List<Favorite> showAllFavorites() {
        return favoriteMapper.queryAllFavorites();
    }

    @Override
    public List<Favorite> showFavoritesByUserId(Integer userId) {
        return favoriteMapper.queryFavoriteByUserId(userId);
    }

    @Override
    public int addUserAttractionTag(Integer userId, String attractionName, String attractionTag, Date nowTime) {
        return favoriteMapper.addUserAttractionTag(userId,attractionName,attractionTag,nowTime);
    }

    @Override
    public int updateTime(Integer userId, String attractionName, Date nowTime) {
        return favoriteMapper.updateTime(userId, attractionName, nowTime);
    }

}
