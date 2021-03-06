package com.example.service.user.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.authc.InvalidTokenException;
import com.example.dao.CacheDao;
import com.example.pojo.User;
import com.example.request.UserLoginRegisterRequest;
import com.example.request.dto.LoginReqDTO;
import com.example.request.dto.RegisterReqDTO;
import com.example.request.dto.ResetPasswordReqDTO;
import com.example.request.dto.RetrievePasswordReqDTO;
import com.example.response.ReactiveResponse;
import com.example.response.Status;
import com.example.response.data.user.UserLoginRegisterRequestData;
import com.example.service.user.UserLoginRegisterService;
import com.example.util.StringUtil;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

import javax.mail.MessagingException;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


/**
 * @author Lexin Huang, Yu Wang
 */
@Slf4j
@Service
public class UserLoginRegisterServiceImpl extends DefaultUserService<UserLoginRegisterRequestData>
                                             implements UserLoginRegisterService {

    private final static String VERIFICATION_CODE_CACHE_PREFIX = "vfCode#";


    protected UserLoginRegisterServiceImpl(CacheDao cacheDao, JavaMailSenderImpl javaMailSender, TemplateEngine templateEngine) {
        super(cacheDao, javaMailSender, templateEngine);
    }

    @Deprecated
    @Override
    public ReactiveResponse<UserLoginRegisterRequestData>  getNormalLoginResponse(UserLoginRegisterRequest request) {
        ReactiveResponse<UserLoginRegisterRequestData>  response = new ReactiveResponse<> ();
        try {
            doNormalLogin(request.getUsername(), request.getPassword(), response);
        } catch (Exception e) {
            handle(e, response);
        }
        return response;
    }

    @Override
    public ReactiveResponse<UserLoginRegisterRequestData> getNormalLoginResponse(LoginReqDTO request) {
        ReactiveResponse<UserLoginRegisterRequestData>  response = new ReactiveResponse<> ();
        try {
            doNormalLogin(request.getUsername(), request.getPassword(), response);
        } catch (Exception e) {
            handle(e, response);
        }
        return response;
    }

    private void doNormalLogin(String username, String password, ReactiveResponse<UserLoginRegisterRequestData>  response) {
        User user = getLoginUser(username, password);
        if (!Objects.isNull(user)) {
            configureLoginRegisterResponse(response, user);
        } else {
            response.setError(Status.MISMATCH);
        }
    }

    private User getLoginUser(String username, String password) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_username", username);
        queryWrapper.eq("user_password", password);
        return getOne(queryWrapper);
    }


    @Override
    public ReactiveResponse<UserLoginRegisterRequestData>  getTokenLoginResponse(String token) {
        ReactiveResponse<UserLoginRegisterRequestData>  response = new ReactiveResponse<> ();
        try{
            User user = getUserByToken(token);
            configureLoginRegisterResponse(response, user);
        } catch (Exception e){
            handle(e, response);
        }
        return response;
    }

    @Deprecated
    @Override
    public ReactiveResponse<UserLoginRegisterRequestData>  getRegisterResponse(User user) {
        ReactiveResponse<UserLoginRegisterRequestData>  response = new ReactiveResponse<> ();
        try {
            doRegister(user, response);
        } catch (Exception e) {
            handle(e, response);
        }
        return response;
    }

    @Override
    public ReactiveResponse<UserLoginRegisterRequestData> getRegisterResponse(RegisterReqDTO request) {
        ReactiveResponse<UserLoginRegisterRequestData>  response = new ReactiveResponse<> ();
        try {
            doRegister(request.createUserToRegister(), response);
        } catch (Exception e) {
            handle(e, response);
        }
        return response;
    }

    private void doRegister(User userInfo,
                            ReactiveResponse<UserLoginRegisterRequestData>  response) {
        User user = getByUsername(userInfo.getUsername());
        if (Objects.isNull(user)) {
            user = register(userInfo);
            configureLoginRegisterResponse(response, user);
        } else {
            response.setError(Status.USERNAME_NOT_AVAILABLE);
        }
    }

    private User register(User userInfo) {
        save(userInfo);
        return getByUsername(userInfo.getUsername());
    }

    /**
     * 登录注册相关的数据 data, 将在这层进行统一配置(包括 user 属性, token 属性)
     * 通过用户名查询对应的 token 是否已经存在, 如果存在, 将会继续沿用; 如果不存在, 将会生成新的 token
     * 并将刷新 K:username -> V:token, K:token -> V:user 在缓存中的时间
     * 同时此方法还将设置 {@link UserLoginRegisterRequestData} 中的 token 属性
     *
     * @param response 响应内容
     * @param user     提供的用户对象
     */
    private void configureLoginRegisterResponse(ReactiveResponse<UserLoginRegisterRequestData>  response, User user) {
        String username = user.getUsername();
        String token = getStringCache(username);

        if (Strings.isEmpty(token)) {
            token = UUID.randomUUID().toString().replace("-", "");
        }
        refreshTokenTime(token, user);
        UserLoginRegisterRequestData data = new UserLoginRegisterRequestData();
        hidePrivateInfo(user);
        data.setUser(user);
        data.setToken(token);
        response.setSuccess(data);
    }


    @Override
    public ReactiveResponse<UserLoginRegisterRequestData> getEmailCheckResponse(UserLoginRegisterRequest request) {
        ReactiveResponse<UserLoginRegisterRequestData> response = new ReactiveResponse<>();
        try {
            doEmailCheck(request.getUsername(), response);
        } catch (Exception e){
            handle(e, response);
        }
        return response;
    }

    @Override
    public ReactiveResponse<UserLoginRegisterRequestData> getEmailCheckResponse(RetrievePasswordReqDTO request) {
        ReactiveResponse<UserLoginRegisterRequestData> response = new ReactiveResponse<>();
        try {
            doEmailCheck(request.getUsername(), response);
        } catch (Exception e){
            handle(e, response);
        }
        return response;
    }

    private void doEmailCheck(String username, ReactiveResponse<UserLoginRegisterRequestData> response) throws MessagingException {
        UserLoginRegisterRequestData data = new UserLoginRegisterRequestData();
        User user = getByUsername(username);
        if (!Objects.isNull(user)) {
            String verificationCode = StringUtil.getCodeString(8);
            String k = VERIFICATION_CODE_CACHE_PREFIX + username;
            putStringCache(k, verificationCode, 5L, TimeUnit.MINUTES);
            sendVerificationCodeMail(user, verificationCode, 5L);
            data.setVerificationCode(verificationCode);
        } else{
            data.setVerificationCode("");
        }
        response.setSuccess(data);
    }



    @Override
    public ReactiveResponse<UserLoginRegisterRequestData>  getResetPasswordResponse(UserLoginRegisterRequest request) {
        ReactiveResponse<UserLoginRegisterRequestData>  response = new ReactiveResponse<> ();
        try {
            doResetPassword(request, response);
        } catch (Exception e){
            handle(e, response);
        }
        return response;
    }

    @Override
    public ReactiveResponse<UserLoginRegisterRequestData> getResetPasswordResponse(ResetPasswordReqDTO request) {
        ReactiveResponse<UserLoginRegisterRequestData>  response = new ReactiveResponse<> ();
        try {
            doResetPassword(request, response);
        } catch (Exception e){
            handle(e, response);
        }
        return response;
    }


    private void doResetPassword(UserLoginRegisterRequest request, ReactiveResponse<UserLoginRegisterRequestData>  response) {
        String k = VERIFICATION_CODE_CACHE_PREFIX + request.getUsername();
        if (verificationCodeMatch(k, request.getVerificationCode())) {
            updatePassword(request.getUsername(), request.getPassword());
            removeTokenByUsername(request.getUsername());
            response.setSuccess(null);
        } else {
            response.setError(Status.VERIFICATION_CODE_WRONG);
        }
    }

    private void doResetPassword(ResetPasswordReqDTO request, ReactiveResponse<UserLoginRegisterRequestData>  response) {
        String k = VERIFICATION_CODE_CACHE_PREFIX + request.getUsername();
        if (verificationCodeMatch(k, request.getVerificationCode())) {
            updatePassword(request.getUsername(), request.getPassword());
            removeTokenByUsername(request.getUsername());
            response.setSuccess(null);
        } else {
            response.setError(Status.VERIFICATION_CODE_WRONG);
        }
    }

    private @Synchronized boolean verificationCodeMatch(String k, String verificationCode) {
        String target = getStringCache(k);
        if(!Objects.isNull(target) && target.equals(verificationCode)){
            removeStringCache(k);
            return true;
        }
        return false;
    }

    private void updatePassword(String username, String password) {
        User user = getByUsername(username);
        user.setPassword(password);
        updateById(user);
    }

    @Override
    public ReactiveResponse<UserLoginRegisterRequestData> getLogoutResponse(String token) {
        ReactiveResponse<UserLoginRegisterRequestData> response = new ReactiveResponse<>();
        try {
            User user = getUserByToken(token);
            removeTokenByUsername(user.getUsername());
            response.setSuccess(null);
        } catch (InvalidTokenException e){
            handle(e, response);
        }
        return response;
    }

}
