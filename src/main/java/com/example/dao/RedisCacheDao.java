package com.example.dao;

import com.example.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

/**
 * @author Lexin Huang
 */
@Slf4j
@Repository
public class RedisCacheDao implements CacheDao {

    private final StringRedisTemplate stringRedisTemplate;

    private final RedisTemplate<String, User> userRedisTemplate;


    public RedisCacheDao(StringRedisTemplate stringRedisTemplate,
                         RedisTemplate<String, User> userRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.userRedisTemplate = userRedisTemplate;
    }


    @Override
    public void set(String k, String v) {
        stringRedisTemplate.opsForValue().set(k, v);
    }


    @Override
    public void set(String k, String v, Long timeOut, TimeUnit timeUnit) {
        stringRedisTemplate.opsForValue().set(k, v, timeOut, timeUnit);
    }


    @Override
    public String getStringCache(Object k) {
        return stringRedisTemplate.opsForValue().get(k);
    }


    @Override
    public void deleteStringCache(String k) {
        stringRedisTemplate.delete(k);
    }


    public void set(String k, User v) {
        userRedisTemplate.opsForValue().set(k, v);
    }

    @Override
    public void set(String k, User v, Long timeOut, TimeUnit timeUnit) {
        userRedisTemplate.opsForValue().set(k, v, timeOut, timeUnit);
    }

    @Override
    public User getUserCache(String k) {
        return userRedisTemplate.opsForValue().get(k);
    }


    @Override
    public void deleteUserCache(String k) {
        userRedisTemplate.delete(k);
    }


}
