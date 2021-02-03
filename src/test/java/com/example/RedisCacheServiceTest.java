package com.example;

import com.example.pojo.User;
import com.example.service.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Lexin Huang
 */
@Slf4j
@SpringBootTest
public class RedisCacheServiceTest {

    @Autowired
    private CacheService cacheService;

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


        Assert.isTrue(Objects.isNull(cacheService.getStringCache(key1)),
                message1);
        Assert.isTrue(Objects.isNull(cacheService.getStringCache(key2)),
                message2);

        cacheService.saveStringCache(key1, val1);
        cacheService.saveStringCache(key2, val2, 2L, TimeUnit.SECONDS);
        Assert.isTrue(val1.equals(cacheService.getStringCache(key1)),
                message1);
        Assert.isTrue(val2.equals(cacheService.getStringCache(key2)),
                message2);

        timeConsuming(2);
        Assert.isTrue(val1.equals(cacheService.getStringCache(key1)),
                message1);
        Assert.isTrue(Objects.isNull(cacheService.getStringCache(key2)),
                message2);

        cacheService.removeStringCache(key1);
        Assert.isTrue(Objects.isNull(cacheService.getStringCache(key1)),
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

        Assert.isTrue(Objects.isNull(cacheService.getUserCache(key1)),
                message1);
        Assert.isTrue(Objects.isNull(cacheService.getUserCache(key2)),
                message2);

        cacheService.saveUserCache(key1, val1);
        cacheService.saveUserCache(key2, val2, 2L, TimeUnit.SECONDS);
        Assert.isTrue(val1.equals(cacheService.getUserCache(key1)),
                message1);
        Assert.isTrue(val2.equals(cacheService.getUserCache(key2)),
                message2);

        timeConsuming(2);
        Assert.isTrue(val1.equals(cacheService.getUserCache(key1)),
                message1);
        Assert.isTrue(Objects.isNull(cacheService.getUserCache(key2)),
                message2);

        cacheService.removeUserCache(key1);
        Assert.isTrue(Objects.isNull(cacheService.getUserCache(key1)),
                message1);
    }


    @Test
    public void testTokenInterface() {
        String token = UUID.randomUUID().toString();
        User user = new User();
        user.setUserId(1);
        user.setUsername("nameX");
        user.setPassword("passwordX");
        String message1 = "测试TokenInterface-key1出错!";

        Assert.isTrue(Objects.isNull(cacheService.getUserCache(token)),
                message1);

        cacheService.refreshTokenTime(token, user, 2L, TimeUnit.SECONDS);
        Assert.isTrue(!Objects.isNull(cacheService.getUserCache(token)),
                message1);

        timeConsuming(2);
        Assert.isTrue(Objects.isNull(cacheService.getUserCache(token)),
                message1);
    }

}
