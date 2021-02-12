package com.example;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.mapper.UserMapper;
import com.example.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.util.Objects;

/**
 * @author Lexin Huang
 */
@Slf4j
@SpringBootTest
public class MybatisPlusWrapperTest {

    @Autowired
    private UserMapper userMapper;


    @Test
    public void testQueryWrapper(){
        User val1 = new User();
        val1.setUsername("nameX");
        val1.setPassword("passwordX");
        String message1 = "测试QueryWrapper出错!";

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_username", val1.getUsername())
                    .eq("user_password", val1.getPassword());
        Assert.isTrue(Objects.isNull(userMapper.selectOne(queryWrapper)),
                message1);

        userMapper.insert(val1);
        Assert.isTrue(!Objects.isNull(userMapper.selectOne(queryWrapper)),
                message1);

        userMapper.delete(queryWrapper);
        Assert.isTrue(Objects.isNull(userMapper.selectOne(queryWrapper)),
                message1);
    }
}
