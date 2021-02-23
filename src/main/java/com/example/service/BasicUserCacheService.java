package com.example.service;

import com.example.authc.InvalidTokenException;
import com.example.pojo.User;

import java.util.concurrent.TimeUnit;

/**
 * @author 悠一木碧
 */

public interface BasicUserCacheService {
    
    void refreshTokenTime(String k, User v);

    User getUserByToken(String token) throws InvalidTokenException;

    void removeToken(String username);

    void putStringCache(String k, String v, Long timeout, TimeUnit timeUnit);

    String getStringCache(String k);

    void removeStringCache(String k);
}
