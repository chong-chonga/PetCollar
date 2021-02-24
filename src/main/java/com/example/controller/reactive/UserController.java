package com.example.controller.reactive;

import com.example.request.UserLoginRegisterRequest;
import com.example.request.UserLoginRegisterRequestType;
import com.example.request.UserSettingsRequest;
import com.example.request.UserSettingsRequestType;
import com.example.response.ReactiveResponse;
import com.example.service.UserLoginRegisterService;
import com.example.service.UserSettingsService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

/**
 * @author Lexin Huang
 */
@RestController
public class UserController {

    private final
    UserLoginRegisterService userLoginRegisterService;

    private final
    UserSettingsService userSettingsService;


    public UserController( UserLoginRegisterService userLoginRegisterService,
                           UserSettingsService userSettingsService) {
        this.userLoginRegisterService = userLoginRegisterService;
        this.userSettingsService = userSettingsService;
    }


    @RestController
    @RequestMapping("/mobile_platform/user/account_request")
    public class AccountVerificationRequestController {

        @PostMapping("/login")
        public ReactiveResponse login(@RequestBody UserLoginRegisterRequest request) {
            request.setRequestType(UserLoginRegisterRequestType.NORMAL_LOGIN);
            return userLoginRegisterService.getLoginRegisterResponse(request);
        }


        @PostMapping("/token_login")
        public ReactiveResponse tokenLogin(@RequestBody UserLoginRegisterRequest request) {
            request.setRequestType(UserLoginRegisterRequestType.TOKEN_LOGIN);
            return userLoginRegisterService.getLoginRegisterResponse(request);
        }


        @PostMapping("/register")
        public ReactiveResponse register(@RequestBody UserLoginRegisterRequest request) {
            request.setRequestType(UserLoginRegisterRequestType.REGISTER);
            return userLoginRegisterService.getLoginRegisterResponse(request);
        }


        @PostMapping("/retrieve_password/email_check")
        public ReactiveResponse emailCheck(@RequestBody UserLoginRegisterRequest request) {
            request.setRequestType(UserLoginRegisterRequestType.RETRIEVE_PASSWORD);
            return userLoginRegisterService.getLoginRegisterResponse(request);
        }


        @PostMapping("/retrieve_password/submit_reset")
        public ReactiveResponse submitReset(@RequestBody UserLoginRegisterRequest request) {
            request.setRequestType(UserLoginRegisterRequestType.SUBMIT_RESET);
            return userLoginRegisterService.getLoginRegisterResponse(request);
        }

    }


    @RestController
    @RequestMapping("/users/settings")
    public class AccountSettingsController {

        @PatchMapping("/security")
        public ReactiveResponse modifyPassword(@RequestBody UserSettingsRequest request) {
            request.setAccountSettingsRequestType(UserSettingsRequestType.MODIFY_PASSWORD);
            return userSettingsService.getUserSettingsResponse(request);
        }

        @PatchMapping("/profile")
        public ReactiveResponse modifyProfile(@RequestBody UserSettingsRequest request) {
            request.setAccountSettingsRequestType(UserSettingsRequestType.MODIFY_PROFILE);
            return userSettingsService.getUserSettingsResponse(request);
        }

    }

}
