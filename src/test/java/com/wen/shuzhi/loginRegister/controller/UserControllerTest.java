package com.wen.shuzhi.loginRegister.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedisTest {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Test
    public void testDeleteValue() {
        boolean deleteCount1 = redisTemplate.delete("testKey");
        boolean deleteCount2 = redisTemplate.delete("testKey");
        assertEquals(true, deleteCount1);
        assertEquals(false, deleteCount2);
    }

    @Test
    public void testSaveAndRetrieveValue() {
        redisTemplate.opsForValue().set("testKey", "testValue");
        String retrievedValue = redisTemplate.opsForValue().get("testKey");
        assertNotNull(retrievedValue);
        assertEquals("testValue", retrievedValue);
    }

    @Test
    public void testExpiration() throws InterruptedException {
        redisTemplate.opsForValue().set("testKey2", "testValue2", 10, TimeUnit.SECONDS);
        String retrievedValueWithinTime = redisTemplate.opsForValue().get("testKey2");
        assertNotNull(retrievedValueWithinTime);
        assertEquals("testValue2", retrievedValueWithinTime);
        TimeUnit.SECONDS.sleep(15);
        String retrievedValueAfterTime = redisTemplate.opsForValue().get("testKey2");
        assertNull(retrievedValueAfterTime);
    }

    @BeforeEach
    public void setUp() {
        List<String> initialList = Arrays.asList("value1", "value2", "value3");
        redisTemplate.opsForList().rightPushAll("testListKey", initialList);

        redisTemplate.opsForSet().add("testSetKey", "userId=10", "userId=15", "userId=25");

    }

    @Test
    public void testListOperation() {
        Long initialSize = redisTemplate.opsForList().size("testListKey");
        redisTemplate.opsForList().leftPush("testListKey", "new");
        Long sizeAfterInsertion = redisTemplate.opsForList().size("testListKey");
        assertEquals(initialSize + 1, sizeAfterInsertion);
    }

    @Test
    public void testSetOperation() {
        Long initialSize = redisTemplate.opsForSet().size("testSetKey");
        redisTemplate.opsForSet().add("testSetKey", "userId=20");
        Long sizeAfterInsertion = redisTemplate.opsForSet().size("testSetKey");
        assertEquals(initialSize + 1, sizeAfterInsertion);
    }

}