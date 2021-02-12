package com.example.service.impl.user;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.UserMapper;
import com.example.pojo.User;
import com.example.request.AccountSettingsRequest;
import com.example.response.AccountSettingsRequestData;
import com.example.response.ReactiveResponse;
import com.example.response.ReactiveResponse.StatusCode;
import com.example.dao.CacheDao;
import com.example.service.UserService;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;


/**
 * @author Lexin Huang
 */
@Slf4j
@Service("userSettingsService")
public class UserSettingsService extends ServiceImpl<UserMapper, User> implements UserService {

    private final
    CacheDao cacheDao;

    private final
    UserMapper userMapper;


    public UserSettingsService(@Qualifier("redisCacheDao") CacheDao cacheDao,
                               UserMapper userMapper) {
        this.cacheDao = cacheDao;
        this.userMapper = userMapper;
    }


    /**
     * 先将存在于缓存中的值取出, 避免出现以下情况: 判断前key存在, 判断后key失效
     * @param request 用户操作请求
     * @return 响应式回复, 参见 {@link ReactiveResponse}
     */
    @Override
    public ReactiveResponse getSettingsResponse(AccountSettingsRequest request) {
        ReactiveResponse response = new ReactiveResponse();
        AccountSettingsRequestData accountSettingsRequestData = new AccountSettingsRequestData();
        String token = request.getToken();
        User user = getUserIfExists(token);
        if(!Objects.isNull(user)){
            refreshTokenTime(token, user);
            accountSettingsRequestData.setToken(token);
            doOperation(request, response, accountSettingsRequestData, user);
        }else{
            response.setContent(StatusCode.TOKEN_NOT_EXISTS, accountSettingsRequestData);
        }
        return response;
    }

    private User getUserIfExists(String token) {
        if (Objects.isNull(token)) {
            return null;
        } else {
            return cacheDao.getUserCache(token);
        }
    }

    /**
     * 刷新 token 的持续时间为 7 天
     * @param token 用户令牌
     * @param user 用户对象
     */
    @Deprecated
    private void refreshTokenTime(String token, User user) {
        cacheDao.setStringCache(user.getUsername(), token, 7L, TimeUnit.DAYS);
        cacheDao.setUserCache(token, user, 7L, TimeUnit.DAYS);
    }


    private void doOperation(AccountSettingsRequest request,
                             ReactiveResponse response,
                             AccountSettingsRequestData accountSettingsRequestData,
                             User user) {
        switch (request.getAccountSettingsRequestType()){
            case MODIFY_PASSWORD:
                doModifyPassword(request, response, accountSettingsRequestData, user);
                break;
            default:
                break;
        }
    }

    @Synchronized
    private void doModifyPassword(AccountSettingsRequest request,
                                  ReactiveResponse response,
                                  AccountSettingsRequestData accountSettingsRequestData,
                                  User user) {
            if(user.getPassword().equals(request.getOldPassword())){
                updatePassword(user.getUsername(), request.getNewPassword());
                user.setPassword(request.getNewPassword());
                refreshTokenTime(request.getToken(), user);
                accountSettingsRequestData.setUser(user);
                response.setContent(StatusCode.CORRECT, accountSettingsRequestData);
            }else{
                response.setContent(StatusCode.PASSWORD_WRONG, accountSettingsRequestData);
            }
    }


}
