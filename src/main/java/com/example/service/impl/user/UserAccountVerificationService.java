package com.example.service.impl.user;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.UserMapper;
import com.example.pojo.User;
import com.example.request.AccountVerificationRequest;
import com.example.response.AccountVerificationRequestData;
import com.example.response.ReactiveResponse;
import com.example.response.ReactiveResponse.StatusCode;
import com.example.service.CacheService;
import com.example.service.MailService;
import com.example.service.UserService;
import com.example.util.AccountRequestInfoFormat;
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
@Service("userAccountVerificationService")
public class UserAccountVerificationService extends ServiceImpl<UserMapper, User> implements UserService {

    private final static String verificationCodeCachePrefix = "vfCode";


    private final MailService mailService;

    private final CacheService cacheService;

    private final UserMapper userMapper;

    private final Object obj = new Object();


    public UserAccountVerificationService(@Qualifier("simpleMailService") MailService mailService,
                                          @Qualifier("redisCacheService") CacheService cacheService,
                                          UserMapper userMapper) {
        this.mailService = mailService;
        this.cacheService = cacheService;
        this.userMapper = userMapper;
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


//  调用的方法可能抛出的异常将在这层进行统一捕获
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
                case EMAIL_CHECK:
                    doEmailCheck(request, response, accountVerificationRequestData);
                    break;
                case RESET_PASSWORD:
                    doResetPassword(request, response);
                    break;
                default:
                    throw new NoSuchAccountVerificationTypeException("错误的账号验证请求");
            }
        } catch (NoSuchAccountVerificationTypeException | MessagingException e) {
            log.error(e.getMessage());
            response.setContent(StatusCode.Server_ERROR, accountVerificationRequestData);
        }
    }


    private void doLogin(AccountVerificationRequest request,
                         ReactiveResponse response,
                         AccountVerificationRequestData accountVerificationRequestData) {

        User user = getUserBy(request.getUsername(), request.getPassword());
        if (!Objects.isNull(user)) {
            configureVerificationToken(accountVerificationRequestData, user);
            response.setContent(StatusCode.CORRECT, accountVerificationRequestData);
        } else {
            response.setContent(StatusCode.MISMATCH, accountVerificationRequestData);
        }
    }


    private User getUserBy(String username, String password) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(true, "user_username", username)
                .eq(true, "user_password", password);
        return userMapper.selectOne(queryWrapper);
    }


    /**
     * 此方法完成了 token 的生成, 并将其存储在 redis中, 有效时间为 7 天
     * 同一账号在7天内登录, 只会获得同一个token
     * 先将存在于缓存中的值取出, 避免了这样的情况---判断前key存在, 判断后key失效
     * 这样的思想同样应用于 {@link UserOperationService}方法中
     * @param accountVerificationRequestData 登录注册响应数据
     * @param user                           提供的用户对象
     */
    private void configureVerificationToken(AccountVerificationRequestData accountVerificationRequestData,
                                            User user) {
        String username = user.getUsername();
        String token = cacheService.getToken(username);

        if (Strings.isEmpty(token)) {
            token = UUID.randomUUID().toString();
        }
        cacheService.refreshTokenTime(token, user);
        accountVerificationRequestData.setToken(token);
    }



    private void doRegister(AccountVerificationRequest request,
                            ReactiveResponse response,
                            AccountVerificationRequestData data) {
        User user = getUserBy(request.getUsername());
        if (Objects.isNull(user)) {
            user = register(request.createUserToRegister());

            configureVerificationToken(data, user);
            response.setContent(StatusCode.CORRECT, data);
        } else {
            response.setContent(StatusCode.USERNAME_HAS_REGISTERED, data);
        }
    }


    private User register(User user) {
        int id = userMapper.insert(user);
        user.setUserId(id);
        return user;
    }



    private void doEmailCheck(AccountVerificationRequest request,
                              ReactiveResponse response,
                              AccountVerificationRequestData data) throws MessagingException {
        User user = getUserBy(request.getUsername());
        if (!Objects.isNull(user)) {
            String verificationCode = VerificationCodeGenerator.generate(8);
            cacheService.saveStringCache(verificationCodeCachePrefix +request.getUsername(), verificationCode, 5L, TimeUnit.MINUTES);
            mailService.sendVerificationCodeMail(user, verificationCode, 5L);
            data.setVerificationCode(verificationCode);
            response.setContent(StatusCode.CORRECT, data);
        } else {
            response.setContent(StatusCode.USER_NOT_EXISTS, data);
        }
    }


    private User getUserBy(String username){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_username", username);
        return userMapper.selectOne(queryWrapper);
    }



    private void doResetPassword(AccountVerificationRequest request,
                                 ReactiveResponse response) {
        String verificationCode;
        String k = verificationCodeCachePrefix + request.getUsername();
        synchronized (obj){
            verificationCode = cacheService.getStringCache(k);
            if(!Strings.isEmpty(verificationCode) && verificationCode.equals(request.getVerificationCode())){
                cacheService.removeStringCache(k);
            }
        }
        if(!Strings.isEmpty(verificationCode)){
            if(verificationCode.equals(request.getVerificationCode())){
                updatePassword(request.getUsername(), request.getNewPassword());
                cacheService.removeToken(request.getUsername());
                response.setContent(StatusCode.CORRECT, null);
            }else{
                response.setContent(StatusCode.MISMATCH, "验证码错误!", null);
            }
        }else{
            response.setContent(StatusCode.VERIFICATION_CODE_HAS_EXPIRED, null);
        }

    }

    private void updatePassword(String username, String newPassword){
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("user_username", username);
        updateWrapper.set("user_password", newPassword);
        userMapper.update(null, updateWrapper);
    }

}
