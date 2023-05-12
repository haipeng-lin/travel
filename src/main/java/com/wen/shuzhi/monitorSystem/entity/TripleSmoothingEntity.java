package com.wen.shuzhi.monitorSystem.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TripleSmoothingEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Double> realDataList;

    private Double lastSinglePredictParam;

    private Double lastSecondPredictParam;

    private Double lastTriplePredictParam;

    private int predictTime;
}

