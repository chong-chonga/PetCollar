package com.example.service.user.impl;

import com.example.dao.CacheDao;
import com.example.exception.IllegalTextException;
import com.example.exception.InvalidPasswordException;
import com.example.pojo.User;
import com.example.response.ReactiveResponse;
import com.example.response.data.user.UserSettingsRequestData;
import com.example.service.AvatarService;
import com.example.service.user.UserSettingsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;

import java.io.IOException;
import java.util.Objects;


/**
 * @author Lexin Huang
 */
@Slf4j
@Service
public class UserSettingsServiceImpl extends DefaultUserService<UserSettingsRequestData>
                                        implements UserSettingsService, AvatarService {
    private static final String AVATAR_RESOURCE_PATH_PREFIX = "/pet_collar/image/avatar/user/";

    private static final String IMAGE_SUFFIX = ".jpg";

    private static final String DEFAULT_IMAGE_NAME = "default.png";

    private static final String AVATAR_URL_PREFIX = "http://resource.petcollar.top:8082/image/avatar/user/";


    public UserSettingsServiceImpl(CacheDao cacheDao, JavaMailSenderImpl javaMailSender, TemplateEngine templateEngine) {
        super(cacheDao, javaMailSender, templateEngine);
    }


    @Override
    public ReactiveResponse<UserSettingsRequestData> getChangePasswordResponse(String token,
                                                                               String oldPassword, String newPassword) {
        ReactiveResponse<UserSettingsRequestData> response = new ReactiveResponse<>();
        try {
            User user = getUserByToken(token);
            verify(oldPassword, user.getPassword());
            doChangePassword(token, user, newPassword, response);
        } catch (Exception e){
            handle(e, response);
        }
        return response;
    }

    private void doChangePassword(String token, User user,
                                  String newPassword,
                                  ReactiveResponse<UserSettingsRequestData> response) {
        user.setPassword(newPassword);
        executeUpdate(token, user);
        response.setSuccess(null);
    }

    private void verify(String passwordToVerify, String targetPassword) {
        if (Objects.isNull(targetPassword)) {
            throw new NullPointerException("原密码不能为 null!");
        }
        if (Objects.isNull(passwordToVerify) || !(passwordToVerify.equals(targetPassword))){
            throw new InvalidPasswordException("与原密码不匹配!");
        }
    }


    @Override
    public ReactiveResponse<UserSettingsRequestData> getUploadAvatarResponse(String token, MultipartFile image) {
        ReactiveResponse<UserSettingsRequestData> response = new ReactiveResponse<>();
        try {
            User user = getUserByToken(token);
            deleteOriginalAvatarIfExists(user.getUserPortraitPath());
            doUploadAvatar(token, user, image, response);
        } catch (Exception e){
            handle(e, response);
        }
        return response;
    }

    private void doUploadAvatar(String token, User user, MultipartFile image,
                                ReactiveResponse<UserSettingsRequestData> response) throws IOException {
        String avatarUrl = createAvatarFile(image);
        user.setUserPortraitPath(avatarUrl);
        executeUpdate(token, user);

        UserSettingsRequestData data = new UserSettingsRequestData();
        hidePrivateInfo(user);
        data.setUser(user);
        response.setSuccess(data);
    }


    @Override
    public ReactiveResponse<UserSettingsRequestData> getModifyProfileResponse(String token,
                                                                              User newProfile) {
        ReactiveResponse<UserSettingsRequestData> response = new ReactiveResponse<>();
        try{
            User user = getUserByToken(token);
            doModifyProfile(token, user, newProfile, response);
        }catch (IllegalTextException e){
            handle(e, response);
        }
        return response;
    }

    private void doModifyProfile(String token, User userToUpdate,
                                 User newProfile,
                                 ReactiveResponse<UserSettingsRequestData> response) {
        userToUpdate.setUserIntroduction(newProfile.getUserIntroduction());
        userToUpdate.setUsername(newProfile.getUsername());
        userToUpdate.setEmailAddress(newProfile.getEmailAddress());
        executeUpdate(token, userToUpdate);
        response.setSuccess(null);
    }


    @Override
    public String getAvatarUrlPrefix() {
        return AVATAR_URL_PREFIX;
    }

    @Override
    public String getAvatarResourcePathPrefix() {
        return AVATAR_RESOURCE_PATH_PREFIX;
    }

    @Override
    public String getImageSuffix() {
        return IMAGE_SUFFIX;
    }

    @Override
    public String getDefaultImageName() {
        return DEFAULT_IMAGE_NAME;
    }
}
