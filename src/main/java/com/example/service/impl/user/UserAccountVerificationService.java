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

    private final String verificationCodeCachePrefix = "vfCode";

    private final
    MailService mailService;

    private final
    CacheService cacheService;

    private final
    UserMapper userMapper;

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
                case EMAIL_CHECK:
                    doEmailCheck(request, response, accountVerificationRequestData);
                    break;
                case RESET_PASSWORD:
                    doSubmitCheckCode(request, response);
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
        User userInfo = request.createUserToLogin();
        if (exist(userInfo)) {
            accountVerificationRequestData.setUser(getByUsername(request.getUsername()));

            configureVerificationToken(accountVerificationRequestData, userInfo.getUsername());
            response.setContent(StatusCode.CORRECT, accountVerificationRequestData);
        } else {
            response.setContent(StatusCode.MISMATCH, accountVerificationRequestData);
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


    /**
     * 此方法完成了 token 的生成, 并将其存储在 redis中, 有效时间为 7 天
     * 同一账号在7天内登录, 只会获得同一个token
     * @param accountVerificationRequestData 登录注册响应数据
     * @param username                       提供的用户名称
     */
    private void configureVerificationToken(AccountVerificationRequestData accountVerificationRequestData, String username) {
        String token = cacheService.getStringCache(username);
        if (Strings.isEmpty(token)) {
            token = UUID.randomUUID().toString();
        }
        cacheService.saveStringCache(token, username, 7L, TimeUnit.DAYS);
        cacheService.saveStringCache(username, token, 7L, TimeUnit.DAYS);

        accountVerificationRequestData.setToken(token);
    }



    private void doRegister(AccountVerificationRequest request,
                            ReactiveResponse response,
                            AccountVerificationRequestData data) {
        String usernameProvided = request.getUsername();
        if (!exist(usernameProvided)) {
            User userInfo = request.createUserToRegister();
            int userId = userMapper.insert(userInfo);
            userInfo.setUserId(userId);
            data.setUser(userInfo);

            configureVerificationToken(data, usernameProvided);
            response.setContent(StatusCode.CORRECT, data);
        } else {
            response.setContent(StatusCode.USERNAME_HAS_REGISTERED, data);
        }
    }


    private boolean exist(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(true, "user_username", username);
        System.out.println(username);
        return !Objects.isNull(userMapper.selectOne(queryWrapper));
    }



    private void doEmailCheck(AccountVerificationRequest request,
                              ReactiveResponse response,
                              AccountVerificationRequestData data) {
        if (exist(request.getUsername())) {
            User user = getByUsername(request.getUsername());
            String verificationCode = VerificationCodeGenerator.generate(8);
            cacheService.saveStringCache(verificationCodeCachePrefix +request.getUsername(), verificationCode, 5L,
                    TimeUnit.MINUTES);
            try {
                data.setVerificationCode(verificationCode);
                response.setContent(StatusCode.CORRECT, data);
                mailService.sendVerificationCodeMail(user, verificationCode, 5L);
            } catch (MessagingException e) {
                log.error(e.getMessage());
                response.setContent(StatusCode.Server_ERROR, data);
            }
        } else {
            response.setContent(StatusCode.USER_NOT_EXISTS, data);
        }
    }



    private void doSubmitCheckCode(AccountVerificationRequest request,
                                   ReactiveResponse response) {
        String vfCode;
        synchronized (obj){
            String k = verificationCodeCachePrefix + request.getUsername();
            vfCode = cacheService.getStringCache(k);
            cacheService.removeStringKey(k);
        }
        if(!Strings.isEmpty(vfCode)){
            if(vfCode.equals(request.getVerificationCode())){
                UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("user_username", request.getUsername());
                updateWrapper.set("user_password", request.getNewPassword());
                update(updateWrapper);
                response.setContent(StatusCode.CORRECT, null);
            }else{
                response.setContent(StatusCode.MISMATCH, "验证码错误!", null);
            }
        }else{
            response.setContent(StatusCode.VERIFICATION_CODE_HAS_EXPIRED, null);
        }

    }

}
