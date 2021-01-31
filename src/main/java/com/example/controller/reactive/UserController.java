package com.example.controller.reactive;

import com.example.request.AccountVerificationRequest;
import com.example.request.AccountInfoType;
import com.example.request.OperationRequest;
import com.example.request.OperationType;
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
            request.setInfoType(AccountInfoType.LOGIN);
            return userService.getAccountVerificationResponse(request);
        }

        @PostMapping("/register")
        public ReactiveResponse register(@RequestBody AccountVerificationRequest request) {
            request.setInfoType(AccountInfoType.REGISTER);
            return userService.getAccountVerificationResponse(request);
        }

        @PostMapping("/retrieve_password")
        public ReactiveResponse retrievePassword(@RequestBody AccountVerificationRequest request) {
            request.setInfoType(AccountInfoType.RETRIEVE_PASSWORD);
            return userService.getAccountVerificationResponse(request);
        }

    }

    @RestController
    @RequestMapping("/mobile_platform/user/operation")
    public class OperationRequestController {

        @PostMapping("/modify_info")
        public ReactiveResponse modifyInfo(@RequestBody OperationRequest request){
            request.setOperationType(OperationType.MODIFY_INFOMATION);
            return userService.getOperationResponse(request);
        }
    }

}
