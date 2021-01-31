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
@Service
public class RedisCacheService implements CacheService {

    private final StringRedisTemplate stringRedisTemplate;

    private final RedisTemplate<Object, Object> objectRedisTemplate;

    public RedisCacheService(StringRedisTemplate stringRedisTemplate, RedisTemplate<Object, Object> objectRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectRedisTemplate = objectRedisTemplate;
    }

    @Override
    public boolean exist(String k) {
        return null != (stringRedisTemplate.opsForValue().get(k));
    }

    @Override
    public void saveCache(String k, String v, Long timeOut, TimeUnit timeUnit) {
        stringRedisTemplate.opsForValue().set(k, v, timeOut, timeUnit);
    }
}
