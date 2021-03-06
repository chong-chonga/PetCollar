package com.example.service.user;

import com.example.pojo.User;
import com.example.request.UserLoginRegisterRequest;
import com.example.request.dto.LoginReqDTO;
import com.example.request.dto.RegisterReqDTO;
import com.example.request.dto.ResetPasswordReqDTO;
import com.example.request.dto.RetrievePasswordReqDTO;
import com.example.response.ReactiveResponse;
import com.example.response.data.user.UserLoginRegisterRequestData;

import javax.mail.MessagingException;

/**
 * @author Lexin Huang
 */
public interface UserLoginRegisterService {

    @Deprecated
    ReactiveResponse<UserLoginRegisterRequestData>  getNormalLoginResponse(UserLoginRegisterRequest request) ;

    ReactiveResponse<UserLoginRegisterRequestData>  getNormalLoginResponse(LoginReqDTO request) ;

    ReactiveResponse<UserLoginRegisterRequestData>  getTokenLoginResponse(String token);

    @Deprecated
    ReactiveResponse<UserLoginRegisterRequestData>  getRegisterResponse(User user) ;

    ReactiveResponse<UserLoginRegisterRequestData>  getRegisterResponse(RegisterReqDTO request) ;

    @Deprecated
    ReactiveResponse<UserLoginRegisterRequestData> getEmailCheckResponse(UserLoginRegisterRequest request);

    ReactiveResponse<UserLoginRegisterRequestData> getEmailCheckResponse(RetrievePasswordReqDTO request);

    @Deprecated
    ReactiveResponse<UserLoginRegisterRequestData>  getResetPasswordResponse(UserLoginRegisterRequest request);

    ReactiveResponse<UserLoginRegisterRequestData>  getResetPasswordResponse(ResetPasswordReqDTO request);

    ReactiveResponse<UserLoginRegisterRequestData> getLogoutResponse(String token);
}
