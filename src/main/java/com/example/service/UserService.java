package com.example.service;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.pojo.User;
import com.example.request.AccountVerificationRequest;
import com.example.request.AccountSettingsRequest;
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

    default ReactiveResponse getSettingsResponse(AccountSettingsRequest request){
        ReactiveResponse response = new ReactiveResponse();
        response.setContent(ReactiveResponse.StatusCode.Server_ERROR, new ReactiveData());
        return response;
    }

    default void updatePassword(String username, String newPassword){
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("user_username", username);
        updateWrapper.set("user_password", newPassword);
        update(updateWrapper);
    }


}
