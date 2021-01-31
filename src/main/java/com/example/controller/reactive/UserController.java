package com.example.controller.reactive;

import com.example.pojo.AccountVerificationLevel;
import com.example.requrest.AccountRequest;
import com.example.response.ReactiveResponse;
import com.example.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Lexin Huang
 */
@RestController
@RequestMapping("/mobile_platform/user")
public class UserController {

    @RestController
    @RequestMapping("/mobile_platform/user/account_request")
    public static class AccountController {
        final
        UserService userService;

        public AccountController(UserService userService) {
            this.userService = userService;
        }


        @PostMapping("/login")
        public ReactiveResponse login(@RequestBody AccountRequest request) {
            request.setLevel(AccountVerificationLevel.LOGIN);
            return userService.getAccountVerificationResponse(request);
        }

        @PostMapping("/register")
        public ReactiveResponse register(@RequestBody AccountRequest request) {
            request.setLevel(AccountVerificationLevel.REGISTER);
            return userService.getAccountVerificationResponse(request,
                    AccountVerificationLevel.REGISTER);
        }

        @PostMapping("/retrieve_password")
        public ReactiveResponse retrievePassword(@RequestBody AccountRequest request) {
            request.setLevel(AccountVerificationLevel.RETRIEVE_PASSWORD);
            return userService.getAccountVerificationResponse(request,
                    AccountVerificationLevel.RETRIEVE_PASSWORD);
        }

        @PostMapping("/modify_info")
        public ReactiveResponse modifyInfo(@RequestBody AccountRequest request){
            request.setLevel(AccountVerificationLevel.MODIFY_INFORMATION);
            return userService.getAccountVerificationResponse(request);
        }
    }
}
