package com.example.service;

import java.util.concurrent.TimeUnit;

public interface CacheService {
    boolean exist(String k);

    void saveCache(String k, String v, Long timeOut, TimeUnit timeUnit);
}
