package com.example.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.dao.CacheDao;
import com.example.mapper.UserMapper;
import com.example.pojo.User;
import com.example.response.ReactiveResponse;
import com.example.response.Status;
import com.example.response.data.user.UserSearchRequestData;
import com.example.service.ServiceExceptionHandler;
import com.example.service.user.UserSearchService;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Lexin Huang
 */
@Service
public class UserSearchServiceImpl extends BasicUserService
                                   implements UserSearchService, ServiceExceptionHandler<UserSearchRequestData> {

    private final UserMapper userMapper;

    protected UserSearchServiceImpl(CacheDao cacheDao, JavaMailSenderImpl javaMailSender, TemplateEngine templateEngine,
                                    UserMapper userMapper) {
        super(cacheDao, javaMailSender, templateEngine);
        this.userMapper = userMapper;
    }


    @Override
    public ReactiveResponse<UserSearchRequestData> getSearchUsersResponse(String username) {
        ReactiveResponse<UserSearchRequestData> response = new ReactiveResponse<>();
        UserSearchRequestData data = new UserSearchRequestData();
        List<User> users = Objects.requireNonNullElse(getUsersLike(username), new ArrayList<>());
        for (User user : users) {
            hidePrivateInfo(user);
        }
        data.setUsers(users);
        response.setSuccess(data);
        return response;
    }

    private List<User> getUsersLike(String username) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.like("user_username", username);
        return userMapper.selectList(userQueryWrapper);
    }

    @Override
    public ReactiveResponse<UserSearchRequestData> getUserProfileResponse(String token) {
        ReactiveResponse<UserSearchRequestData> response = new ReactiveResponse<>();
        try {
            User user = getUserByToken(token);
            hidePrivateInfo(user);
            UserSearchRequestData data = new UserSearchRequestData();
            data.setUser(user);
        } catch (Exception e) {
            handle(e, response);
        }
        return response;
    }

    @Override
    public void handle(Exception e, ReactiveResponse<UserSearchRequestData> response) {
        response.setError(Status.SERVER_ERROR);
    }
}
