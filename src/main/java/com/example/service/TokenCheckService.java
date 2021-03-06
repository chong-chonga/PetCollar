package com.example.service;

import com.example.authc.InvalidTokenException;
import com.example.pojo.User;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public interface TokenCheckService {

    /**
     * 根据 token 调用 {@link TokenCheckService#getUserCache(String)} 方法获取对应的用户; 特别的, 当 token 无效时,
     * 将会抛出{@link InvalidTokenException}异常
     * @param token 用户令牌
     * @return 用户对象, 如果 token 存在的话
     * @throws InvalidTokenException 当 token 无效时
     */
    default User getUserByToken(String token) throws InvalidTokenException {
        if (Objects.isNull(token)) {
            throw new InvalidTokenException("token 不能为 null !");
        }
        User user = getUserCache(token);
        if (Objects.isNull(user)) {
            throw new InvalidTokenException("值为 " + token + " 的token 不存在!");
        }
        return user;
    }

    User getUserCache(String k);

    default void refreshTokenTime(String token, User user) {
        if (Objects.isNull(token)) {
            throw new NullPointerException("token 不能为 null!");
        }
        if (Objects.isNull(user.getUsername())) {
            throw new NullPointerException("username 不能为 null!");
        }
        long timeOut = 7L;
        putUserCache(token, user, timeOut, TimeUnit.DAYS);
        putStringCache(user.getUsername(), token, timeOut, TimeUnit.DAYS);
    }

    void putUserCache(String k, User v, Long timeOut, TimeUnit timeUnit);

    void putStringCache(String k, String v, Long timeOut, TimeUnit timeUnit);


}
