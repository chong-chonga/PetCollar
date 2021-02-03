package com.example.service.impl;

import com.example.pojo.User;
import com.example.service.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author Lexin Huang
 */
@Slf4j
@Service("redisCacheService")
public class RedisCacheService implements CacheService {

    private final StringRedisTemplate stringRedisTemplate;

    private final RedisTemplate<String, User> userRedisTemplate;


    public RedisCacheService(StringRedisTemplate stringRedisTemplate,
                             RedisTemplate<String, User> userRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.userRedisTemplate = userRedisTemplate;
    }



    @Override
    public void saveStringCache(String k, String v) {
        stringRedisTemplate.opsForValue().set(k, v);
    }


    @Override
    public void saveStringCache(String k, String v, Long timeOut, TimeUnit timeUnit) {
        stringRedisTemplate.opsForValue().set(k, v, timeOut, timeUnit);
    }


    @Override
    public String getStringCache(Object k) {
        return stringRedisTemplate.opsForValue().get(k);
    }


    @Override
    public void removeStringCache(String k) {
        stringRedisTemplate.delete(k);
    }



    public void saveUserCache(String k, User v){
        userRedisTemplate.opsForValue().set(k, v);
    }

    @Override
    public void saveUserCache(String k, User v, Long timeOut, TimeUnit timeUnit) {
        userRedisTemplate.opsForValue().set(k, v, timeOut, timeUnit);
    }


    @Override
    public User getUserCache(String token) {
        return userRedisTemplate.opsForValue().get(token);
    }

    @Override
    public void removeUserCache(String k) {
        userRedisTemplate.delete(k);
    }


    @Override
    public String getToken(String username){
        return getStringCache(username);
    }

    @Override
    public void refreshTokenTime(String token, User user) {
        refreshTokenTime(token, user, 7L, TimeUnit.DAYS);
    }

    @Override
    public void refreshTokenTime(String token, User user, Long timeOut, TimeUnit timeUnit) {
        stringRedisTemplate.opsForValue().set(user.getUsername(), token, timeOut, timeUnit);
        userRedisTemplate.opsForValue().set(token, user, timeOut, timeUnit);
    }

    @Override
    public void removeToken(String username) {
        String token = getToken(username);
        stringRedisTemplate.delete(username);
        if(!Strings.isEmpty(token)){
            userRedisTemplate.delete(token);
        }
    }





}
