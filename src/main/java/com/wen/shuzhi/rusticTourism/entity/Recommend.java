package com.wen.shuzhi.rusticTourism.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 收藏实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("recommend")
public class Recommend {
    private int collectionId; //自增主键
    private int attractionsId; //景点id
    private int userId; //用户id
}
