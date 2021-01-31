package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.pojo.AccountVerificationLevel;
import com.example.pojo.User;
import com.example.requrest.AccountRequest;
import com.example.response.ReactiveResponse;

/**
 * @author 悠一木碧
 */

public interface UserService extends IService<User>{

    ReactiveResponse getAccountVerificationResponse(AccountRequest request);
}
