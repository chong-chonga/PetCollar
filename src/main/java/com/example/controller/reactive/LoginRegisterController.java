package com.example.controller.reactive;

import com.example.pojo.AccountVerificationLevel;
import com.example.requrest.LoginRegisterRequest;
import com.example.response.ReactiveResponse;
import com.example.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Lexin Huang
 */
@RestController("mobileLoginRegisterController")
@RequestMapping("/mobile_platform/user")
public class LoginRegisterController {
    final
    UserService userService;

    public LoginRegisterController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/login")
    public ReactiveResponse login(@RequestBody LoginRegisterRequest request) {
        return userService.getAccountVerificationResponse(request,
                                                        AccountVerificationLevel.LOGIN);
    }

    @PostMapping("/register")
    public ReactiveResponse register(@RequestBody LoginRegisterRequest request) {
        return userService.getAccountVerificationResponse(request,
                                                    AccountVerificationLevel.REGISTER);
    }

    @RequestMapping("change_password")
    public ReactiveResponse updatePassword() {
        return null;
    }
}
