package com.example.controller.reactive;

import com.example.request.AccountVerificationRequest;
import com.example.request.AccountVerificationRequestType;
import com.example.request.OperationRequest;
import com.example.request.OperationRequestType;
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
    final
    UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RestController
    @RequestMapping("/mobile_platform/user/account_request")
    public class AccountVerificationRequestController {

        @PostMapping("/login")
        public ReactiveResponse login(@RequestBody AccountVerificationRequest request) {
            request.setRequestType(AccountVerificationRequestType.LOGIN);
            return userService.getAccountVerificationResponse(request);
        }

        @PostMapping("/register")
        public ReactiveResponse register(@RequestBody AccountVerificationRequest request) {
            request.setRequestType(AccountVerificationRequestType.REGISTER);
            return userService.getAccountVerificationResponse(request);
        }

        @PostMapping("/retrieve_password/email_check")
        public ReactiveResponse emailCheck(@RequestBody AccountVerificationRequest request) {
            request.setRequestType(AccountVerificationRequestType.RETRIEVE_PASSWORD);
            return userService.getAccountVerificationResponse(request);
        }

        @PostMapping("/retrieve_password/submit_check_code")
        public ReactiveResponse submitCheckCode(@RequestBody AccountVerificationRequest request){
            return null;
        }

    }

    @RestController
    @RequestMapping("/mobile_platform/user/operation")
    public class OperationRequestController {

        @PostMapping("/edit/self_info")
        public ReactiveResponse modifyInfo(@RequestBody OperationRequest request){
            request.setOperationRequestType(OperationRequestType.MODIFY_INFORMATION);
            return userService.getOperationResponse(request);
        }
    }

}
