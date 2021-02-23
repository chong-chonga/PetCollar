package com.example.service.impl.user;

import com.example.authc.InvalidTokenException;
import com.example.pojo.User;
import com.example.request.UserSettingsRequest;
import com.example.response.AccountSettingsRequestData;
import com.example.response.ReactiveResponse;
import com.example.response.ReactiveResponse.Status;
import com.example.dao.CacheDao;
import com.example.service.UserSettingsService;
import com.example.service.impl.TooLongIntroductionException;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

import java.util.Objects;


/**
 * @author Lexin Huang
 */
@Slf4j
@Service
public class DefaultUserSettingsService extends DefaultUserService implements UserSettingsService {


    protected DefaultUserSettingsService(CacheDao cacheDao,
                                         JavaMailSenderImpl javaMailSender, TemplateEngine templateEngine) {
        super(cacheDao, javaMailSender, templateEngine);
    }

    /**
     * 先将存在于缓存中的值取出, 避免出现以下情况: 判断前key存在, 判断后key失效
     * @param request 用户操作请求
     * @return 响应式回复, 参见 {@link ReactiveResponse}
     */
    @Override
    public ReactiveResponse getUserSettingsResponse(UserSettingsRequest request) {
        ReactiveResponse response = new ReactiveResponse();
        AccountSettingsRequestData accountSettingsRequestData = new AccountSettingsRequestData();
        String token = request.getToken();
        try {
            User user = getUserByToken(token);
            accountSettingsRequestData.setToken(token);
            doOperation(request, response, accountSettingsRequestData, user);
        } catch (InvalidTokenException e) {
            log.info(e.getMessage());
            response.setContent(Status.INVALID_TOKEN, "令牌无效或已过期!",
                    accountSettingsRequestData);
        }
        return response;
    }


    private void doOperation(UserSettingsRequest request,
                             ReactiveResponse response,
                             AccountSettingsRequestData data,
                             User user) {
        switch (request.getAccountSettingsRequestType()) {
            case MODIFY_PROFILE:
                doModifyProfile(request, response, user, data);
                break;
            case MODIFY_PASSWORD:
                doModifyPassword(request, response, user, data);
                break;
            default:
                break;
        }
        refreshTokenTime(request.getToken(), user);
    }

    private void doModifyProfile(UserSettingsRequest request,
                                 ReactiveResponse response,
                                 User user,
                                 AccountSettingsRequestData data) {
        String introduction = request.getIntroduction();
        if (!Objects.isNull(introduction) && 255 < introduction.length()) {
            throw new TooLongIntroductionException("个人介绍不能为超过255个字符!");
        }
        if(!Objects.isNull(request.getIntroduction())){
            user.setUserIntroduction(request.getIntroduction());
            updateById(user);
        }
        data.setUser(user);
        response.setContent(Status.SUCCESS, data);
    }

    @Synchronized
    private void doModifyPassword(UserSettingsRequest request,
                                  ReactiveResponse response,
                                  User user,
                                  AccountSettingsRequestData data) {
        if (user.getPassword().equals(request.getOldPassword())) {
            if (passwordFormatCorrect(request.getNewPassword())){
                user.setPassword(request.getNewPassword());
                updateById(user);
                user.setPassword(request.getNewPassword());
                data.setUser(user);
                response.setContent(Status.SUCCESS, data);
            } else{
                response.setContent(Status.PASSWORD_FORMAT_WRONG, data);
            }
        } else {
            response.setContent(Status.USER_PASSWORD_WRONG, data);
        }
    }


}
