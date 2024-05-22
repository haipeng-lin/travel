package com.wen.shuzhi.rusticTourism.utils;

public class TimeUtils {
    // 去掉日期字符串后的.0
    public static String dealWithTime(String time) {
        String[] str = time.split("\\.");
        return str[0];
    }

}
