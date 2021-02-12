package com.example.service.impl.user;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.UserMapper;
import com.example.pojo.User;
import com.example.request.AccountVerificationRequest;
import com.example.response.AccountVerificationRequestData;
import com.example.response.ReactiveResponse;
import com.example.response.ReactiveResponse.StatusCode;
import com.example.dao.CacheDao;
import com.example.service.MailService;
import com.example.service.UserService;
import com.example.util.RequestInfoFormat;
import com.example.util.StringUtil;
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
public class UserLoginRegisterService extends ServiceImpl<UserMapper, User> implements UserService {

    private final static String verificationCodeCachePrefix = "vfCode";


    private final MailService mailService;

    private final CacheDao cacheDao;

    private final UserMapper userMapper;

    private final Object obj = new Object();


    public UserLoginRegisterService(@Qualifier("simpleMailService") MailService mailService,
                                    @Qualifier("redisCacheDao") CacheDao cacheDao,
                                    UserMapper userMapper) {
        this.mailService = mailService;
        this.cacheDao = cacheDao;
        this.userMapper = userMapper;
    }



    @Override
    public ReactiveResponse getAccountVerificationResponse(AccountVerificationRequest request) {
        RequestInfoFormat requestInfoFormat = RequestInfoFormat.solveRequestInfoFormat(request);
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
                                             AccountVerificationRequestData data) {
        try {
            switch (request.getRequestType()) {
                case NORMAL_LOGIN:
                    doNormalLogin(request, response, data);
                    break;
                case TOKEN_LOGIN:
                    doTokenLogin(request, response, data);
                    break;
                case REGISTER:
                    doRegister(request, response, data);
                    break;
                case SEND_EMAIL:
                    doSendEmail(request, response, data);
                    break;
                case SUBMIT_RESET:
                    doSubmitReset(request, response);
                    break;
                default:
                    throw new IllegalArgumentException("错误的账号验证请求");
            }
        } catch (IllegalArgumentException | MessagingException e) {
            log.error(e.getMessage());
            response.setContent(StatusCode.Server_ERROR, data);
        }
    }


    private void doNormalLogin(AccountVerificationRequest request,
                               ReactiveResponse response,
                               AccountVerificationRequestData data) {

        User user = getUserByUsername(request.getUsername(), request.getPassword());
        if (!Objects.isNull(user)) {
            configureLoginRegisterData(data, user);
            response.setContent(StatusCode.CORRECT, data);
        } else {
            response.setContent(StatusCode.MISMATCH, data);
        }
    }


    private User getUserByUsername(String username, String password) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(true, "user_username", username)
                .eq(true, "user_password", password);
        return userMapper.selectOne(queryWrapper);
    }


    /**
     * 登录注册相关的数据 data, 将在这层进行统一配置(包括 user 属性, token 属性)
     * 通过用户名查询对应的 token 是否已经存在, 如果存在, 将会继续沿用; 如果不存在, 将会生成新的 token
     * 并将刷新 K:username -> V:token, K:token -> V:user 在缓存中的时间
     * 同时此方法还将设置 {@link AccountVerificationRequestData} 中的 token 属性
     * @param data 登录注册响应数据
     * @param user                           提供的用户对象
     */
    private void configureLoginRegisterData(AccountVerificationRequestData data,
                                            User user) {
        data.setUser(user);
        String username = user.getUsername();
        String token = getToken(username);

        if (Strings.isEmpty(token)) {
            token = UUID.randomUUID().toString();
        }
        refreshTokenTime(token, user);
        data.setToken(token);
    }

    private String getToken(String username){
        return cacheDao.getStringCache(username);
    }



    private void doTokenLogin(AccountVerificationRequest request,
                              ReactiveResponse response,
                              AccountVerificationRequestData data) {
        String token = request.getToken();
        User user = getUserIfExists(token);
        if(!Objects.isNull(user)){
            configureLoginRegisterData(data, user);
            response.setContent(StatusCode.CORRECT, data);
        } else{
            response.setContent(StatusCode.TOKEN_NOT_EXISTS, data);
        }
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

    private void doRegister(AccountVerificationRequest request,
                            ReactiveResponse response,
                            AccountVerificationRequestData data) {
        User user = getUserByUsername(request.getUsername());
        if (Objects.isNull(user)) {
            user = register(request.createUserToRegister());
            configureLoginRegisterData(data, user);
            response.setContent(StatusCode.CORRECT, data);
        } else {
            response.setContent(StatusCode.NAME_HAS_REGISTERED, data);
        }
    }


    private User register(User user) {
        userMapper.insert(user);
        return getUserByUsername(user.getUsername(), user.getPassword());
    }



    private void doSendEmail(AccountVerificationRequest request,
                             ReactiveResponse response,
                             AccountVerificationRequestData data) throws MessagingException {
        User user = getUserByUsername(request.getUsername());
        if (!Objects.isNull(user)) {
            String verificationCode = StringUtil.getCodeString(8);
            cacheDao.setStringCache(verificationCodeCachePrefix +request.getUsername(), verificationCode, 5L, TimeUnit.MINUTES);
            mailService.sendVerificationCodeMail(user, verificationCode, 5L);
            data.setVerificationCode(verificationCode);
            response.setContent(StatusCode.CORRECT, data);
        } else {
            response.setContent(StatusCode.USER_NOT_EXISTS, data);
        }
    }


    private User getUserByUsername(String username){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_username", username);
        return userMapper.selectOne(queryWrapper);
    }


//  对于多线程, 将只会放行第一个正确的验证码请求, 然后使验证码失效
    private void doSubmitReset(AccountVerificationRequest request,
                               ReactiveResponse response) {
        String verificationCode;
        String k = verificationCodeCachePrefix + request.getUsername();
        synchronized (obj){
            verificationCode = getVerificationCodeCache(k);
            if(!Objects.isNull(verificationCode) && verificationCode.equals(request.getVerificationCode())){
                cacheDao.deleteStringCache(k);
            }
        }
        if(!Strings.isEmpty(verificationCode)){
            updatePassword(request.getUsername(), request.getPassword());
            deleteToken(request.getUsername());
            response.setContent(StatusCode.CORRECT, null);
        }else{
            response.setContent(StatusCode.VERIFICATION_CODE_ERROR, null);
        }
    }

    private String getVerificationCodeCache(String k){
        return cacheDao.getStringCache(k);
    }

    private void deleteToken(String username){
        cacheDao.deleteStringCache(username);
    }

}
