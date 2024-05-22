package com.wen.shuzhi.rusticTourism.controller;

import cn.hutool.json.JSONException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jayway.jsonpath.JsonPath;
import com.wen.shuzhi.loginRegister.entity.R;
import com.wen.shuzhi.rusticTourism.service.AttractionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * 天气预报
 */
@RestController
@RequestMapping("/weather")
@Slf4j
public class WeatherController {
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private AttractionService attractionService;


    @RequestMapping("/getWeatherByCityName/{cityName}")
    public R weather(@PathVariable("cityName") String cityName) {

        //1. 通过城市名获取城市id 通过数据库查  已通过爬虫爬取入库
        //只有一个方法 直接写到AttractionMapper中了 不用创其它的东西
        Integer cityNumber = attractionService.getCityIdByCityName(cityName);



        //2. 建立api的连接 获取 天气信息
        String apiBaseURL = "http://aider.meizu.com/app/weather/listWeather?cityIds=";

        String apiURL = apiBaseURL.concat(String.valueOf(cityNumber));

        log.info(apiURL);

        ResponseEntity<String> forEntity = restTemplate.getForEntity(apiURL, String.class);

        if (200 == forEntity.getStatusCodeValue()) {
            log.info(forEntity.getBody());
        } else {
            log.info("error with code:" + forEntity.getStatusCodeValue());
        }


        //3. 使用JsonPath解析器 提取数据

        String body = forEntity.getBody();

        List<String> dateList = JsonPath.read(body, "$..date");
        log.info("近七天日期==="+dateList);

        List<String> heightTempList = JsonPath.read(body, "$..temp_day_c");
        log.info("近七天日最高温==="+heightTempList);

        List<String> lowTempList = JsonPath.read(body, "$..temp_night_c");
        log.info("近七天日最低温==="+lowTempList);

        List<String> weatherList = JsonPath.read(body, "$..weathers..weather");
        log.info("近七天天气情况==="+weatherList);

        List<String> weekList = JsonPath.read(body, "$..week");
        log.info("近七天为周几==="+weekList);



        return new R(true, forEntity, "天气预报");
    }



}