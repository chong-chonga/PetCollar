package com.example.service.impl.user;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.authc.InvalidTokenException;
import com.example.mapper.UserMapper;
import com.example.pojo.User;
import com.example.request.UserLoginRegisterRequest;
import com.example.response.UserLoginRegisterRequestData;
import com.example.response.ReactiveResponse;
import com.example.response.ReactiveResponse.Status;
import com.example.dao.CacheDao;
import com.example.request.RequestInfoFormat;
import com.example.service.UserLoginRegisterService;
import com.example.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

import javax.mail.MessagingException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Lexin Huang, Yu Wang
 */
@Slf4j
@Service
public class DefaultUserLoginRegisterService extends DefaultUserService implements UserLoginRegisterService {

    private final static String verificationCodeCachePrefix = "vfCode";

    private final UserMapper userMapper;

    private final Object lock = new Object();

    protected DefaultUserLoginRegisterService(CacheDao cacheDao, UserMapper userMapper,
                                              JavaMailSenderImpl javaMailSender, TemplateEngine templateEngine) {
        super(cacheDao, javaMailSender, templateEngine);
        this.userMapper = userMapper;
    }


    @Override
    public ReactiveResponse getLoginRegisterResponse(UserLoginRegisterRequest request) {
        ReactiveResponse response = new ReactiveResponse();
        UserLoginRegisterRequestData data = new UserLoginRegisterRequestData();
        try {
            RequestInfoFormat.solveRequestInfoFormat(request);
            dispatchVerificationRequest(request, response, data);
        }catch (Exception e){
            handle(e, response, data);
        }
        return response;
    }

    private void handle(Exception e, ReactiveResponse response, UserLoginRegisterRequestData data) {
        log.info(e.getMessage());
        if (e instanceof RequestInfoFormat.NameFormatException){
            response.setContent(Status.USERNAME_FORMAT_WRONG, data);
        } else if (e instanceof RequestInfoFormat.PasswordFormatException){
            response.setContent(Status.PASSWORD_FORMAT_WRONG, data);
        } else if (e instanceof RequestInfoFormat.EmailFormatException){
            response.setContent(Status.EMAIL_FORMAT_WRONG, data);
        } else if (e instanceof InvalidTokenException) {
            response.setContent(Status.INVALID_TOKEN, data);
        }else if (e instanceof MessagingException || e instanceof  MailException) {
            response.setContent(Status.MAIL_SERVICE_NOT_AVAILABLE, data);
        }else {
            response.setContent(Status.SERVER_ERROR, data);
        }
    }


    //  调用的方法可能抛出的异常将在这层进行统一捕获
    private void dispatchVerificationRequest(UserLoginRegisterRequest request,
                                             ReactiveResponse response,
                                             UserLoginRegisterRequestData data) throws MessagingException {
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
            case RETRIEVE_PASSWORD:
                doRetrievePassword(request, response, data);
                break;
            case SUBMIT_RESET:
                doResetPassword(request, response, data);
                break;
            default:
                throw new IllegalArgumentException("无法处理 request 的请求类型!");
        }
    }


    private void doNormalLogin(UserLoginRegisterRequest request,
                               ReactiveResponse response,
                               UserLoginRegisterRequestData data) {

        User user = getByUsername(request.getUsername(), request.getPassword());
        if (!Objects.isNull(user)) {
            configureLoginRegisterData(data, user);
            response.setContent(Status.SUCCESS, data);
        } else {
            response.setContent(Status.MISMATCH, data);
        }
    }


    private User getByUsername(String username, String password) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(true, "user_username", username)
                .eq(true, "user_password", password);
        return userMapper.selectOne(queryWrapper);
    }


    /**
     * 登录注册相关的数据 data, 将在这层进行统一配置(包括 user 属性, token 属性)
     * 通过用户名查询对应的 token 是否已经存在, 如果存在, 将会继续沿用; 如果不存在, 将会生成新的 token
     * 并将刷新 K:username -> V:token, K:token -> V:user 在缓存中的时间
     * 同时此方法还将设置 {@link UserLoginRegisterRequestData} 中的 token 属性
     *
     * @param data 登录注册响应数据
     * @param user 提供的用户对象
     */
    private void configureLoginRegisterData(UserLoginRegisterRequestData data,
                                            User user) {
        data.setUser(user);
        String username = user.getUsername();
        String token = getStringCache(username);

        if (Strings.isEmpty(token)) {
            token = UUID.randomUUID().toString();
        }
        refreshTokenTime(token, user);
        data.setToken(token);
    }


    private void doTokenLogin(UserLoginRegisterRequest request,
                              ReactiveResponse response,
                              UserLoginRegisterRequestData data) {
        String token = request.getToken();
        User user = getUserByToken(token);
        configureLoginRegisterData(data, user);
        response.setContent(Status.SUCCESS, data);

    }


    private void doRegister(UserLoginRegisterRequest request,
                            ReactiveResponse response,
                            UserLoginRegisterRequestData data) {
        User user = getByUsername(request.getUsername());
        if (Objects.isNull(user)) {
            user = register(request.createUserToRegister());
            configureLoginRegisterData(data, user);
            response.setContent(Status.SUCCESS, data);
        } else {
            response.setContent(Status.USERNAME_NOT_AVAILABLE, "该名称已被使用过了, 请再挑个名称试试!", data);
        }
    }


    private User register(User user) {
        save(user);
        return getByUsername(user.getUsername(), user.getPassword());
    }


    private void doRetrievePassword(UserLoginRegisterRequest request,
                                    ReactiveResponse response,
                                    UserLoginRegisterRequestData data) throws MessagingException, MailException {
        User user = getByUsername(request.getUsername());
        if (!Objects.isNull(user)) {
            String verificationCode = StringUtil.getCodeString(8);
            putStringCache(verificationCodeCachePrefix + request.getUsername(), verificationCode, 5L, TimeUnit.MINUTES);
            sendVerificationCodeMail(user, verificationCode, 5L);
            data.setVerificationCode(verificationCode);
        }
        response.setContent(Status.SUCCESS, data);
    }


    private User getByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_username", username);
        return userMapper.selectOne(queryWrapper);
    }


    /**
     * 用户忘记密码时, 通过邮箱验证码重置密码的方法
     * 用户请求成功之后, 更新数据库的信息同时, 将会清除原有的用户缓存 (如果存在的话)
     * 此方法对于并发的请求, 将只会放行第一个正确的验证码, 后续验证码缓存将被清除
     * @param request  请求体封装对象
     * @param response 响应内容封装对象
     * @param data 响应数据
     * @since 3.0
     */
    private void doResetPassword(UserLoginRegisterRequest request,
                                 ReactiveResponse response, UserLoginRegisterRequestData data) {
        String verificationCode;
        String k = verificationCodeCachePrefix + request.getUsername();
        synchronized (lock) {
            verificationCode = request.getVerificationCode();
            if (!Objects.isNull(verificationCode) && verificationCode.equals(request.getVerificationCode())) {
                removeStringCache(k);
            }
        }
        if (!Strings.isEmpty(verificationCode)) {
            updatePassword(request.getUsername(), request.getPassword());
            removeToken(request.getUsername());
            response.setContent(Status.SUCCESS, data);
        } else {
            response.setContent(Status.VERIFICATION_CODE_WRONG, data);
        }
    }


}
