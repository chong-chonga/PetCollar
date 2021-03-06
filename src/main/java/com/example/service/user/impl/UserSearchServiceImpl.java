package com.example.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.dao.CacheDao;
import com.example.mapper.UserMapper;
import com.example.pojo.User;
import com.example.response.ReactiveResponse;
import com.example.response.data.user.UserSearchRequestData;
import com.example.service.user.UserSearchService;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

import java.util.List;

/**
 * @author Lexin Huang
 */
@Service
public class UserSearchServiceImpl extends DefaultUserService<UserSearchRequestData>
                                        implements UserSearchService {

    private final UserMapper userMapper;

    protected UserSearchServiceImpl(CacheDao cacheDao, JavaMailSenderImpl javaMailSender, TemplateEngine templateEngine,
                                    UserMapper userMapper) {
        super(cacheDao, javaMailSender, templateEngine);
        this.userMapper = userMapper;
    }


    @Override
    public ReactiveResponse<UserSearchRequestData> getUsersLike(String name) {
        ReactiveResponse<UserSearchRequestData> response = new ReactiveResponse<>();
        UserSearchRequestData data = new UserSearchRequestData();
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.like("user_username", name);
        List<User> userGroup = userMapper.selectList(userQueryWrapper);
        for (User user : userGroup) {
            hidePrivateInfo(user);
        }
        data.setUsers(userGroup);
        response.setSuccess(data);
        return response;
    }

    @Override
    public ReactiveResponse<UserSearchRequestData> getUserProfile(String token) {
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
}
