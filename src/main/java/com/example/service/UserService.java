package com.example.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.pojo.User;
import com.example.request.AccountVerificationRequest;
import com.example.request.OperationRequest;
import com.example.response.ReactiveData;
import com.example.response.ReactiveResponse;

/**
 * @author 悠一木碧
 */

public interface UserService extends IService<User>{

    default ReactiveResponse getAccountVerificationResponse(AccountVerificationRequest request){
        ReactiveResponse response = new ReactiveResponse();
        response.setContent(ReactiveResponse.StatusCode.Server_ERROR, new ReactiveData());
        return response;
    }

    default ReactiveResponse getOperationResponse(OperationRequest request){
        ReactiveResponse response = new ReactiveResponse();
        response.setContent(ReactiveResponse.StatusCode.Server_ERROR, new ReactiveData());
        return response;
    }

    default User getByUsername(String username){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_username", username);
        return getOne(queryWrapper);
    }

}
