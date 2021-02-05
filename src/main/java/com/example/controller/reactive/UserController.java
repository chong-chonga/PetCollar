package com.example.controller.reactive;

import com.example.request.AccountSettingsRequest;
import com.example.request.AccountSettingsRequestType;
import com.example.request.AccountVerificationRequest;
import com.example.request.AccountVerificationRequestType;
import com.example.response.ReactiveResponse;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

/**
 * @author Lexin Huang
 */
@RestController
public class UserController {

    private final
    UserService userAccountVerificationService;

    private final
    UserService userSettingsService;


    public UserController(@Qualifier("userAccountVerificationService") UserService userAccountVerificationService,
                          @Qualifier("userSettingsService")           UserService userSettingsService) {
        this.userAccountVerificationService = userAccountVerificationService;
        this.userSettingsService = userSettingsService;
    }



    @RestController
    @RequestMapping("/mobile_platform/user/account_request")
    public class AccountVerificationRequestController {

        @PostMapping("/login")
        public ReactiveResponse login(@RequestBody AccountVerificationRequest request) {
            request.setRequestType(AccountVerificationRequestType.NORMAL_LOGIN);
            return userAccountVerificationService.getAccountVerificationResponse(request);
        }


        @PostMapping("/token_login")
        public ReactiveResponse tokenLogin(@RequestBody AccountVerificationRequest request){
            request.setRequestType(AccountVerificationRequestType.TOKEN_LOGIN);
            return userAccountVerificationService.getAccountVerificationResponse(request);
        }


        @PostMapping("/register")
        public ReactiveResponse register(@RequestBody AccountVerificationRequest request) {
            request.setRequestType(AccountVerificationRequestType.REGISTER);
            return userAccountVerificationService.getAccountVerificationResponse(request);
        }


        @PostMapping("/retrieve_password/email_check")
        public ReactiveResponse emailCheck(@RequestBody AccountVerificationRequest request) {
            request.setRequestType(AccountVerificationRequestType.SEND_EMAIL);
            return userAccountVerificationService.getAccountVerificationResponse(request);
        }


        @PostMapping("/retrieve_password/submit_reset")
        public ReactiveResponse submitReset(@RequestBody AccountVerificationRequest request){
            request.setRequestType(AccountVerificationRequestType.SUBMIT_RESET);
            return userAccountVerificationService.getAccountVerificationResponse(request);
        }

    }



    @RestController
    @RequestMapping("/users/settings")
    public class AccountSettingsController{

        @PatchMapping("/security")
        public ReactiveResponse modifyPassword(@RequestBody AccountSettingsRequest request){
            request.setAccountSettingsRequestType(AccountSettingsRequestType.MODIFY_PASSWORD);
            return userSettingsService.getSettingsResponse(request);
        }

    }



}
