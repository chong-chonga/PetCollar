package com.example.controller.user;

import com.example.request.user.UserSettingsRequestDTO;
import com.example.request.user.UserSettingsRequestDTO.*;
import com.example.response.ReactiveResponse;
import com.example.response.data.user.UserSettingsRequestData;
import com.example.service.user.UserSettingsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Lexin Huang
 */
@Api(tags = "用户个人信息设置模块")
@RestController
@RequestMapping("/settings")
public class UserSettingsController {

    private final
    UserSettingsService userSettingsService;

    public UserSettingsController(UserSettingsService userSettingsService) {
        this.userSettingsService = userSettingsService;
    }

    @PatchMapping("/security")
    @ApiOperation(value = "修改密码接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "用户令牌", required = true,
                    dataTypeClass = String.class, paramType = "header")
    })
    public ReactiveResponse<UserSettingsRequestData> changePassword(@RequestHeader("token") String token,
                                                                    @Validated(value = {ChangePasswordGroup.class})
                                                                    @RequestBody UserSettingsRequestDTO request) {
        return userSettingsService.getChangePasswordResponse(token, request.getOldPassword(), request.getNewPassword());
    }

    @PatchMapping("/profile")
    @ApiOperation(value = "修改个人信息接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "用户令牌", required = true,
                    dataTypeClass = String.class, paramType = "header"),
    })
    public ReactiveResponse<UserSettingsRequestData> modifyProfile(@RequestHeader("token") String token,
                                                                   @Validated(value = {ModifyProfileGroup.class})
                                                                   @RequestBody UserSettingsRequestDTO requestDTO) {
        return userSettingsService.getModifyProfileResponse(token, requestDTO);
    }

    @PatchMapping("/avatar")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "用户令牌", required = true,
                    dataTypeClass = String.class, paramType = "header"),
            @ApiImplicitParam(value = "图片文件", required = true,
                    dataTypeClass = MultipartFile.class, paramType = "form")
    })
    public ReactiveResponse<UserSettingsRequestData> uploadAvatar(@RequestHeader("token") String token,
                                                                  @RequestParam("image") MultipartFile image) {
        return userSettingsService.getUploadAvatarResponse(token, image);
    }


}
