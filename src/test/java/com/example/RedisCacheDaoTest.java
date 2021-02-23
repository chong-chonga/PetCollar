package com.example;

import com.example.pojo.User;
import com.example.dao.CacheDao;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Lexin Huang
 */
@Slf4j
@SpringBootTest
public class RedisCacheDaoTest {

    @Autowired
    private CacheDao cacheDao;

    private final static String key1 = "key1";
    private final static String key2 = "key2";


    public void timeConsuming(int count){
        long l = System.currentTimeMillis();
        while(System.currentTimeMillis() - l < count * 1000L){
        }
    }


    @Test
    public void testStringInterface() {
        String val1 = "val1";
        String val2 = "val2";
        String message1 = "测试StringInterface-key1出错!";
        String message2 = "测试StringInterface-key2出错!";


        Assert.isTrue(Objects.isNull(cacheDao.getStringCache(key1)),
                message1);
        Assert.isTrue(Objects.isNull(cacheDao.getStringCache(key2)),
                message2);

        cacheDao.set(key1, val1);
        cacheDao.set(key2, val2, 2L, TimeUnit.SECONDS);
        Assert.isTrue(val1.equals(cacheDao.getStringCache(key1)),
                message1);
        Assert.isTrue(val2.equals(cacheDao.getStringCache(key2)),
                message2);

        timeConsuming(2);
        Assert.isTrue(val1.equals(cacheDao.getStringCache(key1)),
                message1);
        Assert.isTrue(Objects.isNull(cacheDao.getStringCache(key2)),
                message2);

        cacheDao.deleteStringCache(key1);
        Assert.isTrue(Objects.isNull(cacheDao.getStringCache(key1)),
                message1);
    }


    @Test
    public void testUserInterface() {
        User val1 = new User();
        val1.setUserId(1);
        val1.setUsername("nameX");
        val1.setPassword("passwordX");

        User val2 = new User();
        val2.setUserId(2);
        val2.setUsername("nameY");
        val2.setPassword("passwordY");

        String message1 = "测试UserInterface-key1出错!";
        String message2 = "测试UserInterface-key2出错!";

        Assert.isTrue(Objects.isNull(cacheDao.getUserCache(key1)),
                message1);
        Assert.isTrue(Objects.isNull(cacheDao.getUserCache(key2)),
                message2);

        cacheDao.set(key1, val1);
        cacheDao.set(key2, val2, 2L, TimeUnit.SECONDS);
        Assert.isTrue(val1.equals(cacheDao.getUserCache(key1)),
                message1);
        Assert.isTrue(val2.equals(cacheDao.getUserCache(key2)),
                message2);

        timeConsuming(2);
        Assert.isTrue(val1.equals(cacheDao.getUserCache(key1)),
                message1);
        Assert.isTrue(Objects.isNull(cacheDao.getUserCache(key2)),
                message2);

        cacheDao.deleteUserCache(key1);
        Assert.isTrue(Objects.isNull(cacheDao.getUserCache(key1)),
                message1);
    }


}
