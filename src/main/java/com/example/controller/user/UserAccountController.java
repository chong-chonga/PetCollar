package com.example.controller.user;

import com.example.request.user.UserAccountRequestDTO;
import com.example.request.user.UserAccountRequestDTO.*;
import com.example.response.ReactiveResponse;
import com.example.response.data.user.UserAccountRequestData;
import com.example.service.user.UserAccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author Lexin Huang
 */
@Api(tags = "用户账号模块")
@RestController
public class UserAccountController {

    private final
    UserAccountService userAccountService;

    public UserAccountController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @PostMapping("/login")
    @ApiOperation("登录接口")
    public ReactiveResponse<UserAccountRequestData> login(@Validated(value = {NormalLoginGroup.class})
                                                          @RequestBody UserAccountRequestDTO request) {
        return userAccountService.getNormalLoginResponse(request.getUsername(), request.getPassword());
    }

    @PostMapping("/register")
    @ApiOperation("注册接口")
    public ReactiveResponse<UserAccountRequestData> register(@Validated(value = {RegisterGroup.class})
                                                             @RequestBody UserAccountRequestDTO request) {
        return userAccountService.getRegisterResponse(request.getUsername(), request.getPassword(), request.getEmailAddress());
    }


    @PostMapping("/email_check")
    @ApiOperation("找回密码-发送邮件接口")
    public ReactiveResponse<UserAccountRequestData> emailCheck(@Validated(value = {EmailCheckGroup.class})
                                                               @RequestBody UserAccountRequestDTO request) {
        return userAccountService.getEmailCheckResponse(request.getUsername());
    }


    @PostMapping("/password_reset")
    @ApiOperation("找回密码-提交重置接口")
    public ReactiveResponse<UserAccountRequestData> resetPassword(@Validated(value = {ResetPasswordGroup.class})
                                                                  @RequestBody UserAccountRequestDTO request) {
        return userAccountService.getResetPasswordResponse(request.getUsername(), request.getPassword(), request.getVerificationCode());
    }

    @PostMapping("/token_login")
    @ApiOperation("免密登录接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "用户令牌", required = true,
                    dataTypeClass = String.class, paramType = "header")
    })
    public ReactiveResponse<UserAccountRequestData> tokenLogin(@RequestHeader("token") String token) {
        return userAccountService.getTokenLoginResponse(token);
    }

    @PostMapping("/logout")
    @ApiOperation("注销账号接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "用户令牌", required = true,
                    dataTypeClass = String.class, paramType = "header")
    })
    public ReactiveResponse<UserAccountRequestData> logout(@RequestHeader("token") String token){
        return userAccountService.getLogoutResponse(token);
    }


}
