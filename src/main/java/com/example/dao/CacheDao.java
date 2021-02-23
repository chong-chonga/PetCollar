package com.example.dao;

import com.example.pojo.User;

import java.util.concurrent.TimeUnit;

public interface CacheDao {

    void set(String k, String v);


    void set(String k, String v, Long timeOut, TimeUnit timeUnit);


    String getStringCache(Object k);


    void deleteStringCache(String k);


    void set(String k, User v);


    void set(String k, User v, Long timeOut, TimeUnit timeUnit);


    User getUserCache(String k);


    void deleteUserCache(String k);


}
