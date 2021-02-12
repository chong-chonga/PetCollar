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
    public void setStringCache(String k, String v) {
        if(null == k){
            throw new NullPointerException("k 参数不能为 null!");
        }
        stringRedisTemplate.opsForValue().set(k, v);
    }


    @Override
    public void setStringCache(String k, String v, Long timeOut, TimeUnit timeUnit) {
        if(null == k){
            throw new NullPointerException("k 参数不能为 null!");
        }
        stringRedisTemplate.opsForValue().set(k, v, timeOut, timeUnit);
    }


    @Override
    public String getStringCache(Object k) {
        if(null == k){
            throw new NullPointerException("k 参数不能为 null!");
        }
        return stringRedisTemplate.opsForValue().get(k);
    }


    @Override
    public void deleteStringCache(String k) {
        stringRedisTemplate.delete(k);
    }


    public void setUserCache(String k, User v) {
        if(null == k){
            throw new NullPointerException("k 参数不能为 null!");
        }
        userRedisTemplate.opsForValue().set(k, v);
    }

    @Override
    public void setUserCache(String k, User v, Long timeOut, TimeUnit timeUnit) {
        if(null == k){
            throw new NullPointerException("k 参数不能为 null!");
        }
        userRedisTemplate.opsForValue().set(k, v, timeOut, timeUnit);
    }

    @Override
    public User getUserCache(String k) {
        if(null == k){
            throw new NullPointerException("k 参数不能为 null!");
        }
        return userRedisTemplate.opsForValue().get(k);
    }


    @Override
    public void deleteUserCache(String k) {
        userRedisTemplate.delete(k);
    }


}
