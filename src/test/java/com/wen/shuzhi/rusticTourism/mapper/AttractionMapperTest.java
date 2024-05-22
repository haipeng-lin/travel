package com.wen.shuzhi.rusticTourism.mapper;

import com.wen.shuzhi.rusticTourism.entity.Attraction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class AttractionMapperTest {

    @Autowired
    AttractionMapper attractionMapper;

    @Test
    void insertAttraction1() {
        attractionMapper.insertAttraction(new Attraction("水乡","广东",10,10,10));
    }

    @Test
    void insertAttraction2() {
        attractionMapper.insertAttraction(new Attraction("水","广东",10,10,10));
    }

    @Test
    void insertAttraction3() {
        attractionMapper.insertAttraction(new Attraction("水乡水乡水乡水乡水乡水乡水乡","广东",10,10,10));
    }

    @Test
    void insertAttraction4() {
        attractionMapper.insertAttraction(new Attraction("水乡","广东12333",10,10,10));
    }
    @Test
    void insertAttraction5() {
        attractionMapper.insertAttraction(new Attraction("水乡","广南",10,10,10));
    }

    @Test
    void insertAttraction6() {
        attractionMapper.insertAttraction(new Attraction("水乡","广东",-10,10,10));
    }
    @Test
    void insertAttraction7() {
        attractionMapper.insertAttraction(new Attraction("水乡","广东",10,-10,10));
    }

    @Test
    void insertAttraction8() {
        attractionMapper.insertAttraction(new Attraction("水乡","广东",10,10,-10));
    }


}