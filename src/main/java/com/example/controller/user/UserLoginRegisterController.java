package com.example.controller.user;

import com.example.pojo.User;
import com.example.request.UserLoginRegisterRequest;
import com.example.response.ReactiveResponse;
import com.example.response.data.user.UserLoginRegisterRequestData;
import com.example.service.user.UserLoginRegisterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;

/**
 * @author Lexin Huang
 */
@Api(tags = "用户登录注册模块")
@RestController
public class UserLoginRegisterController extends UserParamsCheckController {

    private final
    UserLoginRegisterService userLoginRegisterService;

    public UserLoginRegisterController(UserLoginRegisterService userLoginRegisterService) {
        this.userLoginRegisterService = userLoginRegisterService;
    }

    @Deprecated
    @ApiOperation("登录接口")
    @PostMapping("/login")
    public ReactiveResponse<UserLoginRegisterRequestData> login(@RequestBody UserLoginRegisterRequest request) throws Exception {
        usernameFormatCheck(request.getUsername());
        passwordFormatCheck(request.getPassword());
        return userLoginRegisterService.getNormalLoginResponse(request);
    }


    @ApiOperation("免密登录接口")
    @PostMapping("/token_login")
    public ReactiveResponse<UserLoginRegisterRequestData> tokenLogin(@RequestHeader("token") String token) {
        return userLoginRegisterService.getTokenLoginResponse(token);
    }

    @ApiOperation("注册接口")
    @PostMapping("/register")
    public ReactiveResponse<UserLoginRegisterRequestData> register(@RequestBody User user) throws Exception {
        entityFormatCheck(user);
        return userLoginRegisterService.getRegisterResponse(user);
    }

    @ApiOperation("找回密码-发送邮件接口")
    @PostMapping("/email_check")
    public ReactiveResponse<UserLoginRegisterRequestData> emailCheck(@RequestBody UserLoginRegisterRequest request) throws MessagingException {
        usernameFormatCheck(request.getUsername());
        return userLoginRegisterService.getEmailCheckResponse(request);
    }

    @ApiOperation("找回密码-提交重置接口")
    @PostMapping("/password_reset")
    public ReactiveResponse<UserLoginRegisterRequestData> resetPassword(@RequestBody UserLoginRegisterRequest request) {
        usernameFormatCheck(request.getUsername());
        return userLoginRegisterService.getResetPasswordResponse(request);
    }

    @ApiOperation("注销账号接口")
    @PostMapping("/logout")
    public ReactiveResponse<UserLoginRegisterRequestData> logout(@RequestHeader("token") String token){
        return userLoginRegisterService.getLogoutResponse(token);
    }

}
