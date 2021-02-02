package com.example.service.impl.user;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.UserMapper;
import com.example.pojo.User;
import com.example.request.OperationRequest;
import com.example.request.OperationRequestType;
import com.example.response.OperationRequestData;
import com.example.response.ReactiveResponse;
import com.example.response.ReactiveResponse.StatusCode;
import com.example.service.CacheService;
import com.example.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author Lexin Huang
 */
@Slf4j
@Service("userOperationService")
public class UserOperationService extends ServiceImpl<UserMapper, User> implements UserService {

    private final
    CacheService cacheService;

    private final
    UserMapper userMapper;


    public UserOperationService(@Qualifier("redisCacheService") CacheService cacheService,
                                UserMapper userMapper) {
        this.cacheService = cacheService;
        this.userMapper = userMapper;
    }



    @Override
    public ReactiveResponse getOperationResponse(OperationRequest request) {
        ReactiveResponse response = new ReactiveResponse();
        OperationRequestData operationRequestData = new OperationRequestData();
        String token = request.getToken();
        if (cacheService.exist(token)) {
            operationRequestData.setToken(token);
            User user = getByUsername(cacheService.getStringCache(token));
            cacheService.saveStringCache(token, user.getUsername(), 7L, TimeUnit.DAYS);
            cacheService.saveStringCache(user.getUsername(), token, 7L, TimeUnit.DAYS);
            doOperation(request, response, operationRequestData, user);
        } else {
            response.setContent(StatusCode.TOKEN_NOT_EXISTS, operationRequestData);
        }
        return response;
    }

    private void doOperation(OperationRequest request,
                             ReactiveResponse response,
                             OperationRequestData operationRequestData,
                             User user) {
        if (OperationRequestType.MODIFY_INFORMATION == request.getOperationRequestType()) {

            if(!user.getPassword().equals(request.getOldPassword())){
                response.setContent(StatusCode.PASSWORD_WRONG, operationRequestData);
            }else{
                user.setEmailAddress(request.getEmailAddress());
                user.setPassword(request.getNewPassword());
                UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("user_id", user.getUserId());
                update(user, updateWrapper);
                operationRequestData.setUser(user);
                response.setContent(StatusCode.CORRECT, operationRequestData);
            }

        }
    }
}
