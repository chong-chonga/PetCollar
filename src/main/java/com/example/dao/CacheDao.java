package com.example.dao;

import com.example.pojo.User;

import java.util.concurrent.TimeUnit;

public interface CacheDao {

    void setStringCache(String k, String v);


    void setStringCache(String k, String v, Long timeOut, TimeUnit timeUnit);


    String getStringCache(Object k);


    void deleteStringCache(String k);


    void setUserCache(String k, User v);


    void setUserCache(String k, User v, Long timeOut, TimeUnit timeUnit);


    User getUserCache(String k);


    void deleteUserCache(String k);


}
