package com.example.controller.user;

import com.example.pojo.User;
import com.example.request.UserSettingsRequest;
import com.example.response.ReactiveResponse;
import com.example.response.data.user.UserSettingsRequestData;
import com.example.service.user.UserSettingsService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Lexin Huang
 */
@Api(tags = "用户个人信息设置模块")
@RestController
@RequestMapping("/settings")
public class UserSettingsController extends UserParamsCheckController {
    private final
    UserSettingsService userSettingsService;

    public UserSettingsController(UserSettingsService userSettingsService) {
        this.userSettingsService = userSettingsService;
    }

    @PatchMapping("/security")
    public ReactiveResponse<UserSettingsRequestData> changePassword(@RequestHeader("token") String token,
                                                                    @RequestBody UserSettingsRequest request) {
        passwordFormatCheck(request.getOldPassword());
        passwordFormatCheck(request.getNewPassword());
        return userSettingsService.getChangePasswordResponse(token, request.getOldPassword(), request.getNewPassword());
    }

    @PatchMapping("/profile")
    public ReactiveResponse<UserSettingsRequestData> modifyProfile(@RequestHeader("token") String token,
                                                                   @RequestBody User newProfile) {
        usernameFormatCheck(newProfile.getUsername());
        emailFormatCheck(newProfile.getEmailAddress());
        introductionFormatCheck(newProfile.getUserIntroduction());
        return userSettingsService.getModifyProfileResponse(token, newProfile);
    }

    @PatchMapping("/avatar")
    public ReactiveResponse<UserSettingsRequestData> uploadAvatar(@RequestHeader("token") String token,
                                                                  @RequestParam("image") MultipartFile image) {
        return userSettingsService.getUploadAvatarResponse(token, image);
    }


}
