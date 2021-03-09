package com.example.service.user.impl;

import com.example.exception.InvalidTokenException;
import com.example.dao.CacheDao;
import com.example.exception.InvalidPasswordException;
import com.example.exception.UnavailableUsernameException;
import com.example.pojo.User;
import com.example.request.user.UserSettingsRequestDTO;
import com.example.response.ReactiveResponse;
import com.example.response.Status;
import com.example.response.data.user.UserSettingsRequestData;
import com.example.service.AvatarService;
import com.example.service.ServiceExceptionHandler;
import com.example.service.user.UserSettingsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;


/**
 * @author Lexin Huang
 */
@Slf4j
@Service
public class UserSettingsServiceImpl extends BasicUserService
                                     implements UserSettingsService, AvatarService, ServiceExceptionHandler<UserSettingsRequestData> {
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
            user.setPassword(newPassword);
            updateById(user);
            refreshTokenTime(token, user);
            response.setSuccess(null);
        } catch (Exception e){
            handle(e, response);
        }
        return response;
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

            String avatarUrl = uploadAvatar(image);
            user.setUserPortraitPath(avatarUrl);
            updateById(user);
            refreshTokenTime(token, user);
            User userData = new User();
            userData.setUserPortraitPath(avatarUrl);
            configureSuccessData(response, userData);
        } catch (Exception e){
            handle(e, response);
        }
        return response;
    }

    private void configureSuccessData(ReactiveResponse<UserSettingsRequestData> response, User user) {
        UserSettingsRequestData data = new UserSettingsRequestData();
        data.setUser(user);
        response.setSuccess(data);
    }


    @Override
    public ReactiveResponse<UserSettingsRequestData> getModifyProfileResponse(String token,
                                                                              UserSettingsRequestDTO requestDTO) {
        ReactiveResponse<UserSettingsRequestData> response = new ReactiveResponse<>();
        try{
            User user = getUserByToken(token);
            String originalName = user.getUsername();
            checkIfNameIsAvailable(requestDTO.getUsername());
            doModifyProfile(user, requestDTO);
            removeStringCache(originalName);
            refreshTokenTime(token, user);
            configureSuccessData(response, user);
        }catch (Exception e){
            handle(e, response);
        }
        return response;
    }

    private void doModifyProfile(User userToUpdate,
                                 UserSettingsRequestDTO requestDTO) {
        userToUpdate.setUserIntroduction(requestDTO.getUserIntroduction());
        userToUpdate.setUsername(requestDTO.getUsername());
        userToUpdate.setEmailAddress(requestDTO.getEmailAddress());
        updateById(userToUpdate);
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
    public String getAvatarSuffix() {
        return IMAGE_SUFFIX;
    }

    @Override
    public String getDefaultAvatarName() {
        return DEFAULT_IMAGE_NAME;
    }

    @Override
    public void handle(Exception e, ReactiveResponse<UserSettingsRequestData> response) {
        log.info(e.getMessage());
        if (e instanceof InvalidTokenException) {
            response.setError(Status.INVALID_TOKEN);
        } else if (e instanceof IOException) {
            response.setError(Status.AVATAR_SERVICE_NOT_AVAILABLE);
        } else if (e instanceof MessagingException || e instanceof MailException) {
            response.setError(Status.MAIL_SERVICE_NOT_AVAILABLE);
        } else if (e instanceof InvalidPasswordException) {
            response.setError(Status.USER_PASSWORD_WRONG);
        } else if (e instanceof UnavailableUsernameException || e instanceof DuplicateKeyException) {
            response.setError(Status.USERNAME_NOT_AVAILABLE);
        } else {
            log.error(Arrays.toString(e.getStackTrace()));
            response.setError(Status.SERVER_ERROR);
        }
    }
}
