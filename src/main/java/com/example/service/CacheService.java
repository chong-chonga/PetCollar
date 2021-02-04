package com.example.service;

import com.example.pojo.User;

import java.util.concurrent.TimeUnit;

public interface CacheService {

    void saveStringCache(String k, String v);


    void saveStringCache(String k, String v, Long timeOut, TimeUnit timeUnit);


    String getStringCache(Object k);


    void removeStringCache(String k);



    void saveUserCache(String k, User v);


    void saveUserCache(String k, User v, Long timeOut, TimeUnit timeUnit);


    User getUserCache(String k);

    User getUserIfExist(String token);

    void removeUserCache(String k);



    String getToken(String username);


    void refreshTokenTime(String token, User user);


    void refreshTokenTime(String token, User user, Long timeOut, TimeUnit timeUnit);


    void removeToken(String username);

}
