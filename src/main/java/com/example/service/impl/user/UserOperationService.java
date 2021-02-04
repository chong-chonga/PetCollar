package com.example.service.impl.user;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.UserMapper;
import com.example.pojo.User;
import com.example.request.OperationRequest;
import com.example.response.OperationRequestData;
import com.example.response.ReactiveResponse;
import com.example.response.ReactiveResponse.StatusCode;
import com.example.service.CacheService;
import com.example.service.UserService;
import com.example.util.AccountRequestInfoFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Objects;


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
        User user = cacheService.getUserIfExist(token);
        if(!Objects.isNull(user)){
            cacheService.refreshTokenTime(token, user);
            operationRequestData.setToken(token);
            doOperation(request, response, operationRequestData, user);
        }else{
            response.setContent(StatusCode.TOKEN_NOT_EXISTS, operationRequestData);
        }
        return response;
    }


    private void doOperation(OperationRequest request,
                             ReactiveResponse response,
                             OperationRequestData operationRequestData,
                             User user) {
        switch (request.getOperationRequestType()){
            case MODIFY_INFORMATION:
                doModifyInformation(request, response, operationRequestData, user);
                break;
            default:
                break;
        }
    }


    private void doModifyInformation(OperationRequest request,
                                     ReactiveResponse response,
                                     OperationRequestData operationRequestData,
                                     User user) {
            if(user.getPassword().equals(request.getOldPassword())){
                if(!AccountRequestInfoFormat.passwordFormatCorrect(request.getNewPassword())){
                    response.setContent(StatusCode.PASSWORD_FORMAT_WRONG, "设置的新密码格式错误!", operationRequestData);
                } else if(!AccountRequestInfoFormat.emailAddressFormatCorrect(request.getEmailAddress())){
                    response.setContent(StatusCode.EMAIL_ADDRESS_NOT_SUPPORTED, operationRequestData);
                } else{
                    user.setPassword(request.getNewPassword());
                    user.setEmailAddress(request.getEmailAddress());
                    update(user);
                    operationRequestData.setUser(user);
                    response.setContent(StatusCode.CORRECT, operationRequestData);
                }
            }else{
                response.setContent(StatusCode.PASSWORD_WRONG, operationRequestData);
            }
    }


    private void update(User user){
        userMapper.updateById(user);
    }

}
