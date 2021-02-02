package com.example.service;

import java.util.concurrent.TimeUnit;

public interface CacheService {


    String getStringCache(Object k);


    Object getObjectCache(Object k);


    void saveStringCache(String k, String v);


    void saveStringCache(String k, String v, Long timeOut, TimeUnit timeUnit);


    void saveObjectCache(Object k, Object v);


    void saveObjectCache(Object k, Object v, Long timeOut, TimeUnit timeUnit);


    void removeStringKey(String k);


    void removeObjectKey(Object o);

    void refreshTokenTime(String token, String username);
}
