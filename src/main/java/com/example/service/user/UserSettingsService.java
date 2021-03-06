package com.example.service.user;

import com.example.pojo.User;
import com.example.response.ReactiveResponse;
import com.example.response.data.user.UserSettingsRequestData;
import org.springframework.web.multipart.MultipartFile;

public interface UserSettingsService {

    ReactiveResponse<UserSettingsRequestData> getChangePasswordResponse(String token,
                                                                        String oldPassword, String newPassword);

    ReactiveResponse<UserSettingsRequestData> getUploadAvatarResponse(String token,
                                                                      MultipartFile image);

    ReactiveResponse<UserSettingsRequestData> getModifyProfileResponse(String token,
                                                                       User newProfile);
}
