package com.example.controller.reactive;

import com.example.pojo.AccountVerificationLevel;
import com.example.requrest.AccountRequest;
import com.example.response.ReactiveResponse;
import com.example.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Lexin Huang
 */
@RestController
@RequestMapping("/mobile_platform/user")
public class UserController {

    @RestController
    @RequestMapping("/mobile_platform/user")
    public static class AccountController {
        final
        UserService userService;

        public AccountController(UserService userService) {
            this.userService = userService;
        }


        @PostMapping("/login")
        public ReactiveResponse login(AccountRequest request) {
            return userService.getAccountVerificationResponse(request,
                    AccountVerificationLevel.LOGIN);
        }

        @PostMapping("/register")
        public ReactiveResponse register(AccountRequest request) {
            return userService.getAccountVerificationResponse(request,
                    AccountVerificationLevel.REGISTER);
        }

        @PostMapping("/retrieve_password")
        public ReactiveResponse retrievePassword(AccountRequest request) {
            return userService.getAccountVerificationResponse(request,
                    AccountVerificationLevel.RETRIEVE_PASSWORD);
        }
    }
}
