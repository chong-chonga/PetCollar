package com.example.service.user;

import com.example.pojo.User;
import com.example.request.UserLoginRegisterRequest;
import com.example.response.ReactiveResponse;
import com.example.response.data.user.UserLoginRegisterRequestData;

import javax.mail.MessagingException;

/**
 * @author Lexin Huang
 */
public interface UserLoginRegisterService {

    ReactiveResponse<UserLoginRegisterRequestData>  getNormalLoginResponse(UserLoginRegisterRequest request) throws Exception;

    ReactiveResponse<UserLoginRegisterRequestData>  getTokenLoginResponse(String token);

    ReactiveResponse<UserLoginRegisterRequestData>  getRegisterResponse(User user) throws Exception;

    ReactiveResponse<UserLoginRegisterRequestData> getEmailCheckResponse(UserLoginRegisterRequest request) throws MessagingException;

    ReactiveResponse<UserLoginRegisterRequestData>  getResetPasswordResponse(UserLoginRegisterRequest request);

    ReactiveResponse<UserLoginRegisterRequestData> getLogoutResponse(String token);
}
