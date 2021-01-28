package com.example;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.mapper.UserMapper;
import com.example.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
@Slf4j
class Springboot03WebApplicationTests {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    private UserMapper userMapper;


    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    void contextLoads() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(true, "user_account", "111")
                    .eq(true, "user_password", "2222");
        User user = userMapper.selectOne(queryWrapper);
        System.out.println("查到的是" + user);
    }

    @Test
    void test1(){

    }

}
