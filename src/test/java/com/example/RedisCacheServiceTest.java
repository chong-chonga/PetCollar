package com.example;

import com.example.service.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

/**
 * @author Lexin Huang
 */
@Slf4j
@SpringBootTest
public class RedisCacheServiceTest {

    @Autowired
    private CacheService cacheService;



    @Test
    public void testGetStringCache(){
        Assert.isNull(cacheService.getStringCache("sdsad"),
                "必定不存在 sdsad 的key!");
    }


    @Test
    public void testRemove(){
        cacheService.saveStringCache("x", "111");
        Assert.isTrue(null != cacheService.getStringCache("x"),
                "没有String类型的key");
        Assert.isTrue(null == cacheService.getObjectCache("x"),
                "没有Object类型的key");
        cacheService.removeStringKey("x");
        Assert.isTrue(null == cacheService.getStringCache("x"),
                "没有删除字符串类型的key");

        cacheService.saveObjectCache("x", "111");
        Assert.isTrue(null != cacheService.getObjectCache("x"),
                "没有String类型的key");
        Assert.isTrue(null == cacheService.getStringCache("x"),
                "没有Object类型的key");
        cacheService.removeObjectKey("x");
        Assert.isTrue(null == cacheService.getObjectCache("x"),
                "没有删除字符串类型的key");
    }
}
