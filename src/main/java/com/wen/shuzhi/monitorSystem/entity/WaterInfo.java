package com.wen.shuzhi.monitorSystem.entity;

/*
@author peng
@create 2023-03-20-11:24
@description 
*/
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

//水质信息
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("water_info") //指定访问数据库中的哪张表
public class WaterInfo {
    private Integer water_info_id;  //主键id,自增
    private Double chroma;          //色度
    private Double ph;              //pH
    private Double electricalConductivity; //电导率
    private Double dissolvedOxygen; //溶解氧

    public WaterInfo(Double chroma, Double ph, Double electricalConductivity, Double dissolvedOxygen) {
        this.chroma = chroma;
        this.ph = ph;
        this.electricalConductivity = electricalConductivity;
        this.dissolvedOxygen = dissolvedOxygen;
    }
}
