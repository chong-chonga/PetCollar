package com.example.service.user.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.exception.InvalidTokenException;
import com.example.dao.CacheDao;
import com.example.exception.user.LoginMismatchException;
import com.example.exception.user.VerificationCodeMismatchException;
import com.example.pojo.User;
import com.example.response.ReactiveResponse;
import com.example.response.Status;
import com.example.response.data.user.UserAccountRequestData;
import com.example.service.ServiceExceptionHandler;
import com.example.service.user.UserAccountService;
import com.example.util.StringUtil;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

import javax.mail.MessagingException;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


/**
 * @author Lexin Huang, Yu Wang
 */
@Slf4j
@Service
public class UserAccountServiceImpl extends BasicUserService
                                    implements UserAccountService, ServiceExceptionHandler<UserAccountRequestData> {

    private final static String VERIFICATION_CODE_CACHE_PREFIX = "vfCode#";

    private final static String DEFAULT_AVATAR_URL = "http://www.petcollat.top:8083/image/avatar/user/default.png";

    private final static String DEFAULT_INTRODUCTION = "这个人还没有介绍哦~";


    protected UserAccountServiceImpl(CacheDao cacheDao, JavaMailSenderImpl javaMailSender, TemplateEngine templateEngine) {
        super(cacheDao, javaMailSender, templateEngine);
    }



    @Override
    public ReactiveResponse<UserAccountRequestData> getNormalLoginResponse(String username, String password) {
        ReactiveResponse<UserAccountRequestData>  response = new ReactiveResponse<> ();
        try {
            User user = login(username, password);
            configureLoginRegisterResponse(response, user);
        } catch (Exception e) {
            handle(e, response);
        }
        return response;
    }

    private User login(String username, String password) throws LoginMismatchException{
        User user = getUser(username, password);
        if (Objects.isNull(user)) {
            throw new LoginMismatchException("提供的用户名密码组合查询不到用户!");
        }
        return user;
    }

    private User getUser(String username, String password) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_username", username);
        queryWrapper.eq("user_password", password);
        return getOne(queryWrapper);
    }


    @Override
    public ReactiveResponse<UserAccountRequestData>  getTokenLoginResponse(String token) {
        ReactiveResponse<UserAccountRequestData>  response = new ReactiveResponse<> ();
        try{
            User user = getUserByToken(token);
            configureLoginRegisterResponse(response, user);
        } catch (Exception e){
            handle(e, response);
        }
        return response;
    }


    @Override
    public ReactiveResponse<UserAccountRequestData> getRegisterResponse(String username, String password, String emailAddress) {
        ReactiveResponse<UserAccountRequestData>  response = new ReactiveResponse<> ();
        User user = createUserToRegister(username, password, emailAddress);
        try {
            checkIfNameIsAvailable(username);
            save(user);
            configureLoginRegisterResponse(response, user);
        } catch (Exception e) {
            handle(e, response);
        }
        return response;
    }

    /**
     * 用于创建注册时, 用于插入表的 User 对象
     * 此方法会默认将会指定用户的默认头像, 参见 resources 目录
     * @return User 对象, 参见{@link User}
     */
    public User createUserToRegister(String username, String password, String emailAddress) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmailAddress(emailAddress);
        user.setUserIntroduction(DEFAULT_INTRODUCTION);
        user.setUserPortraitPath(DEFAULT_AVATAR_URL);
        return user;
    }


    /**
     * 登录注册相关的数据 data, 将在这层进行统一配置(包括 user 属性, token 属性)
     * 通过用户名查询对应的 token 是否已经存在, 如果存在, 将会继续沿用; 如果不存在, 将会生成新的 token
     * 并将刷新 K:username -> V:token, K:token -> V:user 在缓存中的时间
     * 同时此方法还将设置 {@link UserAccountRequestData} 中的 token 属性
     *
     * @param response 响应内容
     * @param user     提供的用户对象
     */
    private void configureLoginRegisterResponse(ReactiveResponse<UserAccountRequestData>  response, User user) {
        String username = user.getUsername();
        String token = getStringCache(username);

        if (Strings.isEmpty(token)) {
            token = StringUtil.getUniqueName();
        }
        refreshTokenTime(token, user);
        hidePrivateInfo(user);
        configureSuccessData(response, user, token, null);
    }

    private void configureSuccessData(ReactiveResponse<UserAccountRequestData> response,
                                      User user,
                                      String token,
                                      String verificationCode) {
        UserAccountRequestData data = new UserAccountRequestData();
        data.setUser(user);
        data.setToken(token);
        data.setVerificationCode(verificationCode);
        response.setSuccess(data);
    }


    @Override
    public ReactiveResponse<UserAccountRequestData> getEmailCheckResponse(String username) {
        ReactiveResponse<UserAccountRequestData> response = new ReactiveResponse<>();
        try {
            doEmailCheck(username, response);
        } catch (Exception e){
            handle(e, response);
        }
        return response;
    }



    private void doEmailCheck(String username, ReactiveResponse<UserAccountRequestData> response) throws MessagingException, MailException {
        String verificationCode;
        User user = getByUniqueName(username);
        if (!Objects.isNull(user)) {
            verificationCode = StringUtil.getCodeString(8);
            String k = getVerificationCodeCacheKey(username);
            putStringCache(k, verificationCode, 5L, TimeUnit.MINUTES);
            sendVerificationCodeMail(user, verificationCode, 5L);
        } else {
            verificationCode = "";
        }
        configureSuccessData(response, null, null, verificationCode);
    }


    private String getVerificationCodeCacheKey(String username) {
        return VERIFICATION_CODE_CACHE_PREFIX + username;
    }


    @Override
    public ReactiveResponse<UserAccountRequestData> getResetPasswordResponse(String username, String password, String verificationCode) {
        ReactiveResponse<UserAccountRequestData>  response = new ReactiveResponse<> ();
        try {
            checkIfVerificationCodeMatch(getVerificationCodeCacheKey(username), verificationCode);
            updatePassword(username, password);
            removeTokenByUsername(username);
            response.setSuccess(null);
        } catch (Exception e){
            handle(e, response);
        }
        return response;
    }

    private @Synchronized void checkIfVerificationCodeMatch(String k, String verificationCode) {
        String target = getStringCache(k);
        boolean match = (!Objects.isNull(target) && target.equals(verificationCode));
        if (!match) {
            throw new VerificationCodeMismatchException("验证码不匹配!");
        }
        removeStringCache(k);
    }

    private void updatePassword(String targetUsername, String password) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("user_username", targetUsername).set("user_password", password);
        update(updateWrapper);
    }

    @Override
    public ReactiveResponse<UserAccountRequestData> getLogoutResponse(String token) {
        ReactiveResponse<UserAccountRequestData> response = new ReactiveResponse<>();
        try {
            User user = getUserByToken(token);
            removeTokenByUsername(user.getUsername());
            response.setSuccess(null);
        } catch (InvalidTokenException e){
            handle(e, response);
        }
        return response;
    }

    @Override
    public void handle(Exception e, ReactiveResponse<UserAccountRequestData> response) {
        log.info(e.getMessage());
        if (e instanceof LoginMismatchException) {
            response.setError(Status.MISMATCH);
        } else if (e instanceof InvalidTokenException) {
            response.setError(Status.INVALID_TOKEN);
        } else if (e instanceof MessagingException || e instanceof MailException) {
            response.setError(Status.MAIL_SERVICE_NOT_AVAILABLE);
        }  else if (e instanceof DuplicateKeyException) {
            response.setError(Status.USERNAME_NOT_AVAILABLE);
        }  else if (e instanceof VerificationCodeMismatchException) {
            response.setError(Status.VERIFICATION_CODE_WRONG);
        } else {
            log.error(Arrays.toString(e.getStackTrace()));
            response.setError(Status.SERVER_ERROR);
        }
    }
}
