package com.example.service.user;

import com.example.exception.InvalidTokenException;
import com.example.pojo.User;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Lexin Huang
 */
public interface TokenCheckService {

    /**
     * 根据 token 调用 {@link TokenCheckService#getUserCache(String token)} 方法获取对应的用户;
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

    /**
     * 刷新用户的 token 和用户信息的缓存有效时间 (默认7天)
     * @param token 用户令牌
     * @param user 用户对象
     * @throws NullPointerException 当 token 为 null 或 username 为 null 时
     */
    default void refreshTokenTime(String token, User user) throws NullPointerException{
        if (Objects.isNull(token)) {
            throw new NullPointerException("token 不能为 null!");
        }
        if (Objects.isNull(user) || Objects.isNull(user.getUsername())) {
            throw new NullPointerException("username 不能为 null!");
        }
        long timeOut = 7L;
        putUserCache(token, user, timeOut, TimeUnit.DAYS);

        putStringCache(user.getUsername(), token, timeOut, TimeUnit.DAYS);
    }

    void putUserCache(String k, User v, Long timeOut, TimeUnit timeUnit);

    void putStringCache(String k, String v, Long timeOut, TimeUnit timeUnit);


}
