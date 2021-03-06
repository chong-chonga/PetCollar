package com.example.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.UserMapper;
import com.example.pojo.User;

import java.util.Objects;

/**
 * @author Lexin Huang
 */
public abstract class BasicUserService extends ServiceImpl<UserMapper, User> {

    protected User getByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_username", username);
        return getOne(queryWrapper);
    }

    protected void hidePrivateInfo(User user) {
        if (!Objects.isNull(user)) {
            user.setEmailAddress(null);
            user.setPassword(null);
        }
    }

}
