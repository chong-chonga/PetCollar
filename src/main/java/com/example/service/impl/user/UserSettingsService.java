package com.example.service.impl.user;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.UserMapper;
import com.example.pojo.User;
import com.example.request.AccountSettingsRequest;
import com.example.response.AccountSettingsRequestData;
import com.example.response.ReactiveResponse;
import com.example.response.ReactiveResponse.StatusCode;
import com.example.service.CacheService;
import com.example.service.UserService;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Objects;


/**
 * @author Lexin Huang
 */
@Slf4j
@Service("userSettingsService")
public class UserSettingsService extends ServiceImpl<UserMapper, User> implements UserService {

    private final
    CacheService cacheService;

    private final
    UserMapper userMapper;

    private final Object obj = new Object();


    public UserSettingsService(@Qualifier("redisCacheService") CacheService cacheService,
                               UserMapper userMapper) {
        this.cacheService = cacheService;
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
        User user = cacheService.getUserIfExist(token);
        if(!Objects.isNull(user)){
            cacheService.refreshTokenTime(token, user);
            accountSettingsRequestData.setToken(token);
            doOperation(request, response, accountSettingsRequestData, user);
        }else{
            response.setContent(StatusCode.TOKEN_NOT_EXISTS, accountSettingsRequestData);
        }
        return response;
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
                cacheService.refreshTokenTime(request.getToken(), user);
                accountSettingsRequestData.setUser(user);
                response.setContent(StatusCode.CORRECT, accountSettingsRequestData);
            }else{
                response.setContent(StatusCode.PASSWORD_WRONG, accountSettingsRequestData);
            }
    }


}
