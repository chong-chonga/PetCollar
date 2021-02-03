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
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


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


    /**
     * 先将存在于缓存中的值取出, 避免出现以下情况: 判断前key存在, 判断后key失效
     * @param request 用户操作请求
     * @return 响应式回复, 参见 {@link ReactiveResponse}
     */
    @Override
    public ReactiveResponse getOperationResponse(OperationRequest request) {
        ReactiveResponse response = new ReactiveResponse();
        OperationRequestData operationRequestData = new OperationRequestData();
        String token = request.getToken();
        User user = cacheService.getUserCache(token);
        if (!Strings.isEmpty(token)) {
            operationRequestData.setToken(token);
            cacheService.refreshTokenTime(token, user);
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
            if(user.getPassword().equals(request.getOldPassword())){
                user.setEmailAddress(request.getEmailAddress());
                user.setPassword(request.getNewPassword());
                UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("user_id", user.getUserId());
                update(user, updateWrapper);
                operationRequestData.setUser(user);
                response.setContent(StatusCode.CORRECT, operationRequestData);
            }else{
                response.setContent(StatusCode.PASSWORD_WRONG, operationRequestData);
            }
        }
    }
}
