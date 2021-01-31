package com.example.service;

import java.util.concurrent.TimeUnit;

public interface CacheService {
    boolean exist(Object k);

    String getStringCache(Object k);

    Object getObjectCache(Object k);

    void saveCache(Object k, Object v);

    void saveCache(Object k, Object v, Long timeOut, TimeUnit timeUnit);

    void saveCache(String k, String v, Long timeOut, TimeUnit timeUnit);
}
