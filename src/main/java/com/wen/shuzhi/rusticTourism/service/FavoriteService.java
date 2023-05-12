package com.wen.shuzhi.rusticTourism.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wen.shuzhi.rusticTourism.entity.Favorite;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public interface FavoriteService extends IService<Favorite> {

    int saveFavorite(Favorite favorite);

    int deleteFavorite(Integer attractionsId, Integer userId);

    List<Favorite> showAllFavorites();

    List<Favorite> showFavoritesByUserId(Integer userId);


    int addUserAttractionTag(Integer userId, String attractionsName, String attractionsTag, Date nowTime);

    int updateTime(Integer userId, String attractionsName, Date nowTime);
}
