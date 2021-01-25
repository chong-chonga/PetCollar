package com.example.service;

import com.example.dao.UserDao;
import com.example.pojo.User;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 悠一木碧
 */
public class UserService {
    private UserDao userDao = new UserDao();
    public List<User> allUsers(){
        List<User> searchResult =userDao.selectAll();
        return searchResult;
    }
}
