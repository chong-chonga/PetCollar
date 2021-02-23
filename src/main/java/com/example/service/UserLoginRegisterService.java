package com.example.service;

import com.example.request.UserLoginRegisterRequest;
import com.example.response.ReactiveResponse;

/**
 * @author Lexin Huang
 */
public interface UserLoginRegisterService {
    ReactiveResponse getLoginRegisterResponse(UserLoginRegisterRequest request);

}
