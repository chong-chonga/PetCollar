package com.example.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.UserMapper;
import com.example.pojo.User;
import com.example.request.AccountVerificationRequest;
import com.example.request.OperationRequest;
import com.example.request.OperationRequestType;
import com.example.response.AccountVerificationRequestData;
import com.example.response.OperationRequestData;
import com.example.response.ReactiveResponse;
import com.example.response.ReactiveResponse.StatusCode;
import com.example.service.CacheService;
import com.example.service.MailService;
import com.example.service.UserService;
import com.example.pojo.AccountRequestInfoFormat;
import com.example.util.NoSuchAccountVerificationTypeException;
import com.example.util.VerificationCodeGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Lexin Huang, Yu Wang
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    private final UserMapper userMapper;

    private final MailService mailService;

    private final CacheService cacheService;


    public UserServiceImpl(UserMapper userMapper,
                           @Qualifier("simpleMailService") MailService mailService,
                           @Qualifier("redisCacheService") CacheService cacheService) {
        this.userMapper = userMapper;
        this.mailService = mailService;
        this.cacheService = cacheService;
    }


    @Override
    public ReactiveResponse getAccountVerificationResponse(AccountVerificationRequest request) {
        AccountRequestInfoFormat requestInfoFormat = AccountRequestInfoFormat.solveRequestInfoFormat(request);
        ReactiveResponse response = new ReactiveResponse();
        AccountVerificationRequestData accountVerificationRequestData = new AccountVerificationRequestData();
        if (requestInfoFormat.isCorrect()) {
            dispatchVerificationRequest(request, response, accountVerificationRequestData);
        } else {
            response.setContent(requestInfoFormat.getStatusCode(), accountVerificationRequestData);
        }
        return response;
    }


    /**
     * 跨层交互时, 确保上一层传入的参数符合 当前的期望,
     * 对于 controller 传入的参数进行必要的检查, 如不符合规定, 则即时抛出异常, 利于进行断言测试
     * @param request                        登录注册请求
     * @param response                       响应内容
     * @param accountVerificationRequestData 登录注册响应数据
     */
    private void dispatchVerificationRequest(AccountVerificationRequest request,
                                             ReactiveResponse response,
                                             AccountVerificationRequestData accountVerificationRequestData) {
        try {
            switch (request.getRequestType()) {
                case LOGIN:
                    doLogin(request, response, accountVerificationRequestData);
                    break;
                case REGISTER:
                    doRegister(request, response, accountVerificationRequestData);
                    break;
                case RETRIEVE_PASSWORD:
                    doRetrievePassword(request, response, accountVerificationRequestData);
                    break;
                default:
                    throw new NoSuchAccountVerificationTypeException("错误的账号验证请求");
            }
        } catch (NoSuchAccountVerificationTypeException e) {
            log.error(e.getMessage());
            response.setContent(StatusCode.Server_ERROR, accountVerificationRequestData);
        }
    }


    private void doLogin(AccountVerificationRequest request,
                         ReactiveResponse response,
                         AccountVerificationRequestData accountVerificationRequestData) {
        User userInfo = new User();
        userInfo.setUsername(request.getUsername());
        userInfo.setPassword(request.getPassword());
        if (exist(userInfo)) {
            userInfo = getByUsername(request.getUsername());
            accountVerificationRequestData.setUser(userInfo);
            configureVerificationData(accountVerificationRequestData, userInfo.getUsername());
            response.setContent(StatusCode.CORRECT, accountVerificationRequestData);
        } else {
            response.setContent(StatusCode.MISMATCH, accountVerificationRequestData);
        }

    }


    /**
     * 此方法完成了 token 的生成, 并将其存储在 redis中, 有效时间为 7 天
     * 同一账号在7天内登录, 只会获得同一个token
     * @param accountVerificationRequestData 登录注册响应数据
     * @param username                       提供的用户名称
     */
    private void configureVerificationData(AccountVerificationRequestData accountVerificationRequestData, String username) {
        String token = cacheService.getStringCache(username);
        if (!Strings.isEmpty(token)) {
            accountVerificationRequestData.setToken(token);
            //延长有效期
            cacheService.saveCache(token, username, 7L, TimeUnit.DAYS);
            cacheService.saveCache(username, token, 7L, TimeUnit.DAYS);
        } else {
            token = UUID.randomUUID().toString();
            cacheService.saveCache(token, username, 7L, TimeUnit.DAYS);
            cacheService.saveCache(username, token, 7L, TimeUnit.DAYS);
            accountVerificationRequestData.setToken(token);
        }
    }


    /**
     * 用于查询对应 user 是否存在
     * @param user 需要判断是否存在的账号
     * @return user存在的布尔值
     */
    private boolean exist(User user) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(true, "user_username", user.getUsername())
                .eq(true, "user_password", user.getPassword());
        return !Objects.isNull(userMapper.selectOne(queryWrapper));
    }


    private void doRegister(AccountVerificationRequest request,
                            ReactiveResponse response,
                            AccountVerificationRequestData accountVerificationRequestData) {
        String usernameProvided = request.getUsername();
        if (!exist(usernameProvided)) {
            User userInfo = new User();
            userInfo.setUsername(usernameProvided);
            userInfo.setPassword(request.getPassword());
            userInfo.setEmailAddress(request.getEmailAddress());
            save(userInfo);
            configureVerificationData(accountVerificationRequestData, userInfo.getUsername());
            response.setContent(StatusCode.CORRECT, accountVerificationRequestData);
        } else {
            response.setContent(StatusCode.USERNAME_HAS_REGISTERED, accountVerificationRequestData);
        }
    }


    private boolean exist(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(true, "user_username", username);
        System.out.println(username);
        return !Objects.isNull(userMapper.selectOne(queryWrapper));
    }


    private User getByUsername(String username) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("user_username", username);
        return userMapper.selectOne(userQueryWrapper);
    }


    private void doRetrievePassword(AccountVerificationRequest request,
                                    ReactiveResponse response,
                                    AccountVerificationRequestData accountVerificationRequestData) {
        String prefix = "checkCode";
        if (exist(request.getUsername())) {
            User user = getByUsername(request.getUsername());
            String verificationCode = VerificationCodeGenerator.generate(8);
            cacheService.saveCache(prefix+request.getUsername(), verificationCode, 5L, TimeUnit.MINUTES);
            try {
                mailService.sendVerificationCodeMail(user, verificationCode, 5L);
                accountVerificationRequestData.setVerificationCode(verificationCode);
                response.setContent(StatusCode.CORRECT, accountVerificationRequestData);
            } catch (MessagingException e) {
                log.error(e.getMessage());
                response.setContent(StatusCode.Server_ERROR, accountVerificationRequestData);
            }
        } else {
            response.setContent(StatusCode.USER_NOT_EXISTS, accountVerificationRequestData);
        }
    }


    @Override
    public ReactiveResponse getOperationResponse(OperationRequest request) {
        ReactiveResponse response = new ReactiveResponse();
        OperationRequestData operationRequestData = new OperationRequestData();
        String token = request.getToken();
        if (cacheService.exist(token)) {
            operationRequestData.setToken(token);
            User user = getByUsername(cacheService.getStringCache(request.getToken()));
            cacheService.saveCache(token, user.getUsername(), 7L, TimeUnit.DAYS);
            cacheService.saveCache(user.getUsername(), token, 7L, TimeUnit.DAYS);
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
