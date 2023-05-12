package com.wen.shuzhi.rusticTourism.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wen.shuzhi.rusticTourism.entity.BrowseTime;
import com.wen.shuzhi.rusticTourism.entity.Click;
import com.wen.shuzhi.rusticTourism.entity.Recommend;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public interface RecommendService extends IService<Recommend> {

    List<Integer> getAllUserId();

    List<Integer> getLikedByUserId(Integer userId);

    List<Integer> getCommentByUserId(Integer userId);

    List<Integer> getCollectionByUserId(Integer userId);

    int getCommentNumByUserId(Integer userId);

    int getCollectionNumByUserId(Integer userId);

    int getLikedNumByUserId(Integer userId);

    int isExist(Integer userId);

    int saveInitialTags(Integer userId,String tags);

    String getInitialTagByUserId(Integer userId);

    ArrayList<String> getHaveBehaviorName(Integer userId);

    List<String> getHaveBehaviorTag(Integer userId);

    List<Click> getClicksByUserId(Integer userId);

    List<BrowseTime> getBrowseByUserId(Integer userId);
}
