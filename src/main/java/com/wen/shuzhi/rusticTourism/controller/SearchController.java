package com.wen.shuzhi.rusticTourism.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.seg.common.Term;
import com.wen.shuzhi.loginRegister.entity.R;
import com.wen.shuzhi.rusticTourism.entity.Attraction;
import com.wen.shuzhi.rusticTourism.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * 搜索引擎
 */

@RestController
@Slf4j
public class SearchController {

    @Autowired
    private SearchService searchService;


    static String keyWordStr = "自然景观,山水奇观,古代建筑,水乡风光,湖光山色,古老村落,田园风光,人文景观,民俗文化,博物馆,园林景观,特色农业,文化遗产,风景名胜区,休闲度假,神奇地貌,生态旅游,旅游购物";
    static List<String> keyWordList = Arrays.asList(keyWordStr.split(","));

    //提取文本中有实意的关键词进行 模糊搜索
    @GetMapping ("/keyWord")
    public R searchKeyWord(@RequestParam("text")String text,
                           @RequestParam(value="pn",defaultValue = "1")Integer pn){

        List<String> keyWordList = extractWordFromText(text);
        log.info("提取出的关键字有:"+keyWordList.toString());

        Page<Attraction> searchPage = new Page<Attraction>(pn,3);
        QueryWrapper<Attraction> wrapper = new QueryWrapper<>();
        for(String keyWord : keyWordList){
            wrapper.like("key_word",keyWord)
                    .or().like("attraction_name",keyWord).or().like("location",keyWord);
        }
        Page<Attraction> page = searchService.page(searchPage,wrapper);
        if(page.getTotal() == 0){

            return new R(false,"无搜索结果");
        }
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("page",page);
        hashMap.put("keyWordList",keyWordList);
        return new R(true,hashMap,"搜索结果如下");
    }

    /**
     * 提出文本中的景点关键词
     */
    private static List<String> extractWordFromText(String text){
        // resultList 用于保存提取后的结果
        List<String> resultList = new ArrayList<>();

        log.info(keyWordList.toString());
        log.info(String.valueOf(keyWordList.contains(text)));

        StringBuilder builder = new StringBuilder(text);
        //判断text是否包含规定的景点关键字 包含则不参与分词
        for(String keyWord : keyWordList){
            if(text.indexOf(keyWord) != -1) {
                int startIndex = text.indexOf(keyWord);
                int stopIndex = startIndex + keyWord.length();
                builder.delete(startIndex, stopIndex);
                resultList.add(keyWord);
                log.info(builder.toString());
                log.info("包含规定的关键字");
            }
        }


        // 当 text 为空字符串时，使用分词函数会报错，所以需要提前处理这种情况
        if(text.length() == 0){
            return resultList;
        }

        // 分词
        List<Term> termList = HanLP.segment(builder.toString());
        // 提取所有的 1.名词/n ;2.地名/ns 3.规定好的景点关键字
        for (Term term : termList) {
            log.info(String.valueOf(term.word));
            if(keyWordList.contains(term.word)){
                resultList.add(term.word);
                continue;
            }
            if(term.nature == Nature.n ||  term.nature == Nature.ns){
                resultList.add(term.word);
            }
        }
        resultList.remove("景点");
        return resultList;
    }



}
