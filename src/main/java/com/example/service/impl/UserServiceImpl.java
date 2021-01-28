package com.example.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.UserMapper;
import com.example.pojo.Account;
import com.example.pojo.AccountFormat;
import com.example.pojo.AccountVerificationLevel;
import com.example.pojo.User;
import com.example.requrest.LoginRegisterRequest;
import com.example.response.LoginRegisterResponse;
import com.example.response.ReactiveResponse;
import com.example.response.ReactiveResponse.StatusCode;
import com.example.service.UserService;
import com.example.util.FormatTool;
import com.example.util.NoSuchAccountVerificationTypeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Lexin Huang
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    final
    StringRedisTemplate stringRedisTemplate;

    final
    UserMapper userMapper;

    public UserServiceImpl(StringRedisTemplate stringRedisTemplate, UserMapper userMapper) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.userMapper = userMapper;
    }


    @Override
    public ReactiveResponse getAccountVerificationResponse(LoginRegisterRequest request,
                                                           AccountVerificationLevel level) {
        AccountFormat accountFormat = FormatTool.solveAccountFormat(request.getAccount());
        LoginRegisterResponse response = new LoginRegisterResponse();
        if(accountFormat.correct()){
            dispatchVerificationRequest(request, response, level);
        }else{
            response.setStatus(accountFormat.getStatusCode());
        }
        return response;
    }



    private void dispatchVerificationRequest(LoginRegisterRequest request,
                                             LoginRegisterResponse response,
                                             AccountVerificationLevel level) {
        try{
            switch (level){
                case LOGIN:
                    doLogin(request, response);
                    break;
                case REGISTER:
                    doRegister(request, response);
                    break;
                default:
                    throw new NoSuchAccountVerificationTypeException("错误的账号验证请求");
            }
        }catch (NoSuchAccountVerificationTypeException e){
            log.error(e.getMessage());
            response.setStatus(ReactiveResponse.StatusCode.Server_ERROR);
        }
    }

    private void doLogin(LoginRegisterRequest request, LoginRegisterResponse response) {
        Account accountProvided = request.getAccount();
        if(exist(accountProvided)){
            setVerificationResponse(response, StatusCode.CORRECT);
        }else{
            setVerificationResponse(response, StatusCode.MISMATCH);
        }
    }

    private void setVerificationResponse(LoginRegisterResponse response, int code) {
        if(StatusCode.CORRECT == code){
            String token = UUID.randomUUID().toString();
            stringRedisTemplate.opsForValue().set(token, "", 1, TimeUnit.HOURS);
            response.setData(token);
        }
        response.setStatus(code);
    }


    private boolean exist(Account account){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(true,"user_account", account.getUsername())
                    .eq(true,"user_password", account.getPassword());
        return !Objects.isNull(userMapper.selectOne(queryWrapper));
    }


    private void doRegister(LoginRegisterRequest request, LoginRegisterResponse response) {
        String usernameProvided = request.getAccount().getUsername();
        if(exist(usernameProvided)){
            setVerificationResponse(response, StatusCode.USERNAME_HAS_REGISTERED);
        }else{
            save(request.getAccount());
            setVerificationResponse(response, StatusCode.CORRECT);
        }
    }


    private Boolean exist(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(true,"user_account", username);
        return !Objects.isNull(userMapper.selectOne(queryWrapper));
    }

    private void save(Account account){
        User user = new User();
        user.setAccount(account.getUsername());
        user.setPassword(account.getPassword());
        save(user);
    }

}
