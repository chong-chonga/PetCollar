package com.example.service.impl;

import com.example.service.CacheService;
import lombok.extern.slf4j.Slf4j;
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

    private final RedisTemplate<Object, Object> objectRedisTemplate;

    public RedisCacheService(StringRedisTemplate stringRedisTemplate, RedisTemplate<Object, Object> objectRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectRedisTemplate = objectRedisTemplate;
    }

    @Override
    public boolean exist(Object k) {
        return null != getStringCache(k);
    }

    @Override
    public String getStringCache(Object k) {
        return stringRedisTemplate.opsForValue().get(k);
    }

    public Object getObjectCache(Object k){
        return objectRedisTemplate.opsForValue().get(k);
    }

    @Override
    public void saveStringCache(Object k, Object v) {
        objectRedisTemplate.opsForValue().set(k, v);
    }

    @Override
    public void saveStringCache(Object k, Object v, Long timeOut, TimeUnit timeUnit) {
        objectRedisTemplate.opsForValue().set(k, v, timeOut, timeUnit);
    }

    @Override
    public void saveStringCache(String k, String v, Long timeOut, TimeUnit timeUnit) {
        stringRedisTemplate.opsForValue().set(k, v, timeOut, timeUnit);
    }
}
