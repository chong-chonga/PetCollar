package com.example.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.exception.UnavailableNameException;
import com.example.exception.UnavailableUsernameException;
import com.example.mapper.UserMapper;
import com.example.pojo.User;
import com.example.service.UniqueNameQueryService;
import org.springframework.lang.Nullable;

import java.util.Objects;

/**
 * @author Lexin Huang
 */
public class EnhanceUserQueryServiceImpl extends ServiceImpl<UserMapper, User> implements UniqueNameQueryService<User> {

    @Override
    public User getByUniqueName(String name) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_username", name);
        return getOne(queryWrapper);
    }

    @Override
    public void checkIfNameIsAvailable(String name) throws UnavailableNameException {
        if (exist(name)) {
            throw new UnavailableUsernameException("用户名称为 " + name + " 的用户已存在!");
        }
    }


    /**
     * 隐藏用户私密信息
     * @param user 用户对象
     */
    protected void hidePrivateInfo(@Nullable User user) {
        if (!Objects.isNull(user)) {
            user.setPassword(null);
        }
    }

}
