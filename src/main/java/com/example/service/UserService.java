package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.pojo.User;
import com.example.request.AccountVerificationRequest;
import com.example.request.OperationRequest;
import com.example.response.ReactiveResponse;

/**
 * @author 悠一木碧
 */

public interface UserService extends IService<User>{

    ReactiveResponse getAccountVerificationResponse(AccountVerificationRequest request);

    ReactiveResponse getOperationResponse(OperationRequest request);
}
