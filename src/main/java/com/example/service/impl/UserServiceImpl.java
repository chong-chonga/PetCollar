package com.example.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.UserMapper;
import com.example.pojo.Account;
import com.example.pojo.AccountFormat;
import com.example.pojo.AccountVerificationLevel;
import com.example.pojo.User;
import com.example.requrest.LoginRegisterRequest;
import com.example.response.LoginRegisterData;
import com.example.response.ReactiveResponse;
import com.example.response.ReactiveResponse.StatusCode;
import com.example.service.UserService;
import com.example.util.FormatTool;
import com.example.util.NoSuchAccountVerificationTypeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
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
        ReactiveResponse response = new ReactiveResponse();
        LoginRegisterData loginRegisterData = new LoginRegisterData();
        if(accountFormat.isCorrect()){
            dispatchVerificationRequest(request, response, loginRegisterData, level);
        }else{
            response.setContent(accountFormat.getStatusCode(), loginRegisterData);
        }
        return response;
    }


    /**
     * 跨层交互时, 确保上一层传入的参数符合 当前的期望,
     * 对于 controller 传入的参数进行必要的检查, 如不符合规定, 则即时抛出异常, 利于进行断言测试
     * @param request 登录注册请求
     * @param response 响应内容
     * @param loginRegisterData 登录注册响应数据
     * @param level 账号验证层级, 详情见 {@link AccountVerificationLevel}
     */
    private void dispatchVerificationRequest(LoginRegisterRequest request,
                                             ReactiveResponse response,
                                             LoginRegisterData loginRegisterData,
                                             AccountVerificationLevel level) {
        try{
            switch (level){
                case LOGIN:
                    doLogin(request, response, loginRegisterData);
                    break;
                case REGISTER:
                    doRegister(request, response, loginRegisterData);
                    break;
                default:
                    throw new NoSuchAccountVerificationTypeException("错误的账号验证请求");
            }
        }catch (NoSuchAccountVerificationTypeException e){
            log.error(e.getMessage());
            response.setContent(StatusCode.Server_ERROR, loginRegisterData);
        }
    }


    private void doLogin(LoginRegisterRequest request,
                         ReactiveResponse response,
                         LoginRegisterData loginRegisterData) {
        Account accountProvided = request.getAccount();
        if(exist(accountProvided)){
            configureVerificationData(loginRegisterData, accountProvided);
            response.setContent(StatusCode.CORRECT, loginRegisterData);
        }else{
            response.setContent(StatusCode.MISMATCH, loginRegisterData);
        }

    }


    /**
     * 此方法完成了 token 的生成, 并将其存储在 redis中, 有效时间为 1 小时
     * 已知, 此方法如果用户多次使用同一账号多次登录, 会产生大量不必要的 token 在redis中
     * 下一版本中, 修改该方法使得同一账号在一定时间内登录, 只会获得同一个token
     * 并将其实现逻辑封装在 RedisService 类中
     * @param loginRegisterData 登录注册响应数据
     * @param accountProvided 提供的账号信息
     */
    @Deprecated
    private void configureVerificationData(LoginRegisterData loginRegisterData, Account accountProvided) {
        String token = UUID.randomUUID().toString();
        stringRedisTemplate.opsForValue().set(token, accountProvided.getUsername(), 1, TimeUnit.HOURS);
        loginRegisterData.setVal(token);
    }

    /**
     * 此方法已经过时, 由于此方法渗透到了 mapper 层的配置, 进行了耦合性的跨层使用
     * 一旦此方法出现问题时, 将难以排查, 需要进行修改
     * @param account 需要判断是否存在的账号
     * @return 账号存在的布尔值
     */
    @Deprecated
    private boolean exist(Account account){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(true,"user_account", account.getUsername())
                    .eq(true,"user_password", account.getPassword());
        return !Objects.isNull(userMapper.selectOne(queryWrapper));
    }


    private void doRegister(LoginRegisterRequest request,
                            ReactiveResponse response,
                            LoginRegisterData loginRegisterData) {
        String usernameProvided = request.getAccount().getUsername();

        if(exist(usernameProvided)){
            response.setContent(StatusCode.USERNAME_NOT_REGISTERED, loginRegisterData);
        }else{
            save(request.getAccount());
            configureVerificationData(loginRegisterData, request.getAccount());
            response.setContent(StatusCode.CORRECT, loginRegisterData);
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
