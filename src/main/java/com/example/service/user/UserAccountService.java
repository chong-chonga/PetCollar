package com.example.service.user;

import com.example.response.ReactiveResponse;
import com.example.response.data.user.UserAccountRequestData;

/**
 * @author Lexin Huang
 */
public interface UserAccountService {

    ReactiveResponse<UserAccountRequestData>  getNormalLoginResponse(String username, String password) ;



    ReactiveResponse<UserAccountRequestData>  getRegisterResponse(String username,
                                                                  String password,
                                                                  String emailAddress) ;

    ReactiveResponse<UserAccountRequestData> getEmailCheckResponse(String username);


    ReactiveResponse<UserAccountRequestData>  getResetPasswordResponse(String username,
                                                                       String password,
                                                                       String verificationCode);

    ReactiveResponse<UserAccountRequestData>  getTokenLoginResponse(String token);

    ReactiveResponse<UserAccountRequestData> getLogoutResponse(String token);
}
