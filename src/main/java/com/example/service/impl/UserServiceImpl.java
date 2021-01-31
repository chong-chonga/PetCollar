package com.example.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.UserMapper;
import com.example.pojo.Account;
import com.example.pojo.AccountFormat;
import com.example.pojo.AccountVerificationLevel;
import com.example.pojo.User;
import com.example.requrest.AccountRequest;
import com.example.response.AccountRequestData;
import com.example.response.ReactiveResponse;
import com.example.response.ReactiveResponse.StatusCode;
import com.example.service.CacheService;
import com.example.service.MailService;
import com.example.service.UserService;
import com.example.util.FormatUtil;
import com.example.util.NoSuchAccountVerificationTypeException;
import com.example.util.VerificationCodeGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
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
    private final StringRedisTemplate stringRedisTemplate;

    private final UserMapper userMapper;

    private final MailService mailService;

    private final CacheService cacheService;


    public UserServiceImpl(StringRedisTemplate stringRedisTemplate,
                           UserMapper userMapper,
                           @Qualifier("simpleMailService")MailService mailService,
                           @Qualifier("redisCacheService")CacheService cacheService) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.userMapper = userMapper;
        this.mailService = mailService;
        this.cacheService = cacheService;
    }


    @Override
    public ReactiveResponse getAccountVerificationResponse(AccountRequest request,
                                                           AccountVerificationLevel level) {
        AccountFormat accountFormat = FormatUtil.solveAccountFormat(request.getAccount());
        ReactiveResponse response = new ReactiveResponse();
        AccountRequestData accountRequestData = new AccountRequestData();
        if(accountFormat.isCorrect() || level == AccountVerificationLevel.RETRIEVE_PASSWORD){
            dispatchVerificationRequest(request, response, accountRequestData, level);
        }else{
            response.setContent(accountFormat.getStatusCode(), accountRequestData);
        }
        return response;
    }


    /**
     * 跨层交互时, 确保上一层传入的参数符合 当前的期望,
     * 对于 controller 传入的参数进行必要的检查, 如不符合规定, 则即时抛出异常, 利于进行断言测试
     * @param request 登录注册请求
     * @param response 响应内容
     * @param accountRequestData 登录注册响应数据
     * @param level 账号验证层级, 详情见 {@link AccountVerificationLevel}
     */
    private void dispatchVerificationRequest(AccountRequest request,
                                             ReactiveResponse response,
                                             AccountRequestData accountRequestData,
                                             AccountVerificationLevel level) {
        try{
            switch (level){
                case LOGIN:
                    doLogin(request, response, accountRequestData);
                    break;
                case REGISTER:
                    doRegister(request, response, accountRequestData);
                    break;
                case RETRIEVE_PASSWORD:
                    doRetrievePassword(request, response, accountRequestData);
                    break;
                default:
                    throw new NoSuchAccountVerificationTypeException("错误的账号验证请求");
            }
        }catch (NoSuchAccountVerificationTypeException e){
            log.error(e.getMessage());
            response.setContent(StatusCode.Server_ERROR, accountRequestData);
        }
    }


    private void doLogin(AccountRequest request,
                         ReactiveResponse response,
                         AccountRequestData accountRequestData) {
        Account accountProvided = request.getAccount();
        if(exist(accountProvided)){
            configureVerificationData(accountRequestData, accountProvided);
            response.setContent(StatusCode.CORRECT, accountRequestData);
        }else{
            response.setContent(StatusCode.MISMATCH, accountRequestData);
        }

    }


    /**
     * 此方法完成了 token 的生成, 并将其存储在 redis中, 有效时间为 7 天
     * 同一账号在7天内登录, 只会获得同一个token
     * @param accountRequestData 登录注册响应数据
     * @param accountProvided 提供的账号信息
     */
    private void configureVerificationData(AccountRequestData accountRequestData, Account accountProvided) {
        String username = accountProvided.getUsername();
        if (stringRedisTemplate.opsForValue().get(username)!=null){
            String token = stringRedisTemplate.opsForValue().get(username);
            accountRequestData.setToken(token);
            assert token != null;
            //延长有效期
            saveInRedis(token, accountProvided.getUsername(), 7L, TimeUnit.DAYS);
            saveInRedis(accountProvided.getUsername(),token, 7L, TimeUnit.DAYS);
        }else {
            String token = UUID.randomUUID().toString();
            saveInRedis(token, accountProvided.getUsername(), 7L, TimeUnit.DAYS);
            saveInRedis(accountProvided.getUsername(),token, 7L, TimeUnit.DAYS);
            accountRequestData.setToken(token);
        }
    }


    /**
     * 用于查询对应 account 是否存在
     * @param account 需要判断是否存在的账号
     * @return 账号存在的布尔值
     */
    private boolean exist(Account account){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(true,"user_account", account.getUsername())
                    .eq(true,"user_password", account.getPassword());
        return !Objects.isNull(userMapper.selectOne(queryWrapper));
    }


    private void doRegister(AccountRequest request,
                            ReactiveResponse response,
                            AccountRequestData accountRequestData) {
        String usernameProvided = request.getAccount().getUsername();

        if(exist(usernameProvided)){
            response.setContent(StatusCode.USERNAME_HAS_REGISTERED, accountRequestData);
        }else{
            save(request.getAccount());
            configureVerificationData(accountRequestData, request.getAccount());
            response.setContent(StatusCode.CORRECT, accountRequestData);
        }
    }


    private boolean exist(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(true,"user_account", username);
        System.out.println(username);
        return !Objects.isNull(userMapper.selectOne(queryWrapper));
    }


    private void save(Account account){
        User user = new User();
        user.setAccount(account.getUsername());
        user.setPassword(account.getPassword());
        user.setEmailAddress(account.getEmailAddress());
        save(user);
    }


    private void doRetrievePassword(AccountRequest request,
                                    ReactiveResponse response,
                                    AccountRequestData accountRequestData) {
        if(exist(request.getUsername())){
            String verificationCode = VerificationCodeGenerator.generate(8);
            saveInRedis(request.getUsername(), verificationCode, 5, TimeUnit.MINUTES);
            try {
                sendEmail(request.getAccount(), verificationCode);
                accountRequestData.setVerificationCode(verificationCode);
                response.setContent(StatusCode.CORRECT, accountRequestData);
            } catch (MessagingException e) {
                log.error(e.getMessage());
                response.setContent(StatusCode.Server_ERROR, accountRequestData);
            }

        }else{
            response.setContent(StatusCode.USERNAME_NOT_REGISTERED, accountRequestData);
        }
    }

    private void saveInRedis(String K, String V, Long timeout, TimeUnit timeUnit) {
        stringRedisTemplate.opsForValue().set(K, V, timeout, timeUnit);
    }

    private void sendEmail(Account accountInfo, String verificationCode) throws MessagingException {
        mailService.sendVerificationCodeMail(accountInfo, verificationCode, 5, TimeUnit.MINUTES);
    }

}
