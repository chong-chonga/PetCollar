package com.example.controller.reactive;

import com.example.request.AccountVerificationRequest;
import com.example.request.AccountVerificationRequestType;
import com.example.request.OperationRequest;
import com.example.request.OperationRequestType;
import com.example.response.ReactiveResponse;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
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

    private final
    UserService userAccountVerificationService;

    private final
    UserService userOperationService;


    public UserController(@Qualifier("userAccountVerificationService") UserService userAccountVerificationService,
                          @Qualifier("userOperationService")UserService userOperationService) {
        this.userAccountVerificationService = userAccountVerificationService;
        this.userOperationService = userOperationService;
    }



    @RestController
    @RequestMapping("/mobile_platform/user/account_request")
    public class AccountVerificationRequestController {

        @PostMapping("/login")
        public ReactiveResponse login(@RequestBody AccountVerificationRequest request) {
            request.setRequestType(AccountVerificationRequestType.LOGIN);
            return userAccountVerificationService.getAccountVerificationResponse(request);
        }


        @PostMapping("/register")
        public ReactiveResponse register(@RequestBody AccountVerificationRequest request) {
            request.setRequestType(AccountVerificationRequestType.REGISTER);
            return userAccountVerificationService.getAccountVerificationResponse(request);
        }


        @PostMapping("/retrieve_password/email_check")
        public ReactiveResponse emailCheck(@RequestBody AccountVerificationRequest request) {
            request.setRequestType(AccountVerificationRequestType.EMAIL_CHECK);
            return userAccountVerificationService.getAccountVerificationResponse(request);
        }


        @PostMapping("/retrieve_password/submit_reset")
        public ReactiveResponse submitReset(@RequestBody AccountVerificationRequest request){
            request.setRequestType(AccountVerificationRequestType.RESET_PASSWORD);
            return userAccountVerificationService.getAccountVerificationResponse(request);
        }

    }



    @RestController
    @RequestMapping("/mobile_platform/user/operation")
    public class OperationRequestController {

        @PostMapping("/edit/self_info")
        public ReactiveResponse modifyInfo(@RequestBody OperationRequest request){
            request.setOperationRequestType(OperationRequestType.MODIFY_INFORMATION);
            return userOperationService.getOperationResponse(request);
        }



    }

}
