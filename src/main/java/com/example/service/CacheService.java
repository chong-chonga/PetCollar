package com.example.service;

import java.util.concurrent.TimeUnit;

public interface CacheService {
    boolean exist(Object k);

    String getStringCache(Object k);

    Object getObjectCache(Object k);

    void saveStringCache(Object k, Object v);

    void saveStringCache(Object k, Object v, Long timeOut, TimeUnit timeUnit);

    void saveStringCache(String k, String v, Long timeOut, TimeUnit timeUnit);
}
