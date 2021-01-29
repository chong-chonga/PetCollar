package com.example.controller.reactive;

import com.example.pojo.AccountVerificationLevel;
import com.example.requrest.LoginRegisterRequest;
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
    public static class LoginRegisterController {
        final
        UserService userService;

        public LoginRegisterController(UserService userService) {
            this.userService = userService;
        }


        @PostMapping("/login")
        public ReactiveResponse login(LoginRegisterRequest request) {
            return userService.getAccountVerificationResponse(request,
                    AccountVerificationLevel.LOGIN);
        }

        @PostMapping("/register")
        public ReactiveResponse register(LoginRegisterRequest request) {
            return userService.getAccountVerificationResponse(request,
                    AccountVerificationLevel.REGISTER);
        }

        @RequestMapping("change_password")
        public ReactiveResponse updatePassword() {
            return null;
        }
    }
}
