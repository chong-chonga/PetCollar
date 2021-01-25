package com.example.dao;

import com.example.pojo.User;
import org.thymeleaf.expression.Lists;

import java.util.Arrays;
import java.util.List;

/**
 * @author 悠一木碧
 */
public class UserDao {
    public List<User> selectAll(){
        User user1 = new User(1, null, "xiaoxiao", "12341","没有介绍");
        User user2 = new User(2, null, "98173812", "12sdsa123", "是小鸟");
        return Arrays.asList(user1, user2);
    }
}
