package com.example.service.user.impl;

import com.example.authc.InvalidTokenException;
import com.example.exception.TooLongTextException;
import com.example.dao.CacheDao;
import com.example.pojo.User;
import com.example.response.ReactiveResponse;
import com.example.response.Status;
import com.example.service.ServiceExceptionHandler;
import com.example.service.user.*;
import com.example.exception.InvalidPasswordException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Lexin Huang
 */
@Slf4j
public class DefaultUserService<T extends ReactiveResponse.ApiData> extends BasicUserService
                                implements UserAuthorizationService, UserMailService, ServiceExceptionHandler<T> {

    private final CacheDao cacheDao;

    private final JavaMailSenderImpl javaMailSender;

    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String senderMailAddress;

    protected DefaultUserService(CacheDao cacheDao, JavaMailSenderImpl javaMailSender,
                                 TemplateEngine templateEngine) {
        this.cacheDao = cacheDao;
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public void putUserCache(String k, User v, Long timeOut, TimeUnit timeUnit) {
        cacheDao.set(k, v, timeOut, timeUnit);
    }

    @Override
    public void putStringCache(String k, String v, Long timeOut, TimeUnit timeUnit) {
        if (Objects.isNull(k)) {
            throw new NullPointerException("k 不能为 null !");
        }
        cacheDao.set(k, v, timeOut, timeUnit);
    }

    @Override
    public User getUserCache(String k) {
        return cacheDao.getUserCache(k);
    }

    @Override
    public String getStringCache(String k) {
        if (Objects.isNull(k)) {
            throw new NullPointerException("k 不能为 null !");
        }
        return cacheDao.getStringCache(k);
    }

    @Override
    public void removeTokenByUsername(String username) {
        String token = null;
        if (!Objects.isNull(username)) {
            token = getStringCache(username);
            removeStringCache(username);
        }
        if (!Objects.isNull(token)) {
            removeUserCache(token);
        }
    }

    @Override
    public void removeStringCache(String k) {
        if (!Objects.isNull(k)) {
            cacheDao.deleteStringCache(k);
        }
    }

    private void removeUserCache(String token) {
        cacheDao.deleteUserCache(token);
    }


    /**
     * 执行用户信息的更新, 此方法会同时更新缓存/持久化中的用户信息
     * @param token 用户令牌
     * @param user  需要更新的用户对象, 应当包含完整的更新后的数据
     */
    protected void executeUpdate(String token, User user) {
        refreshTokenTime(token, user);
        updateById(user);
    }


    @Override
    public void sendVerificationCodeMail(User user, String verificationCode, Long timeOut) throws
                                                                MessagingException, MailException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "utf-8");
        addCc(mimeMessage);
        configureBasicEmailInformation(mimeMessageHelper, senderMailAddress, user.getEmailAddress());
        renderEmailContent(mimeMessageHelper, user.getUsername(), verificationCode, timeOut);
        sendMail(mimeMessage);
    }

    private void addCc(MimeMessage mimeMessage) throws MessagingException {
        mimeMessage.addRecipients(MimeMessage.RecipientType.CC, InternetAddress.parse(senderMailAddress));
    }

    private void configureBasicEmailInformation(MimeMessageHelper mimeMessageHelper,
                                                String senderMailAddress, String receiverAddress) throws MessagingException {
        mimeMessageHelper.setSubject("宠物项圈科技账号验证");
        mimeMessageHelper.setFrom(senderMailAddress);
        mimeMessageHelper.setTo(receiverAddress);
    }

    private void renderEmailContent(MimeMessageHelper mimeMessageHelper, String username,
                                    String verificationCode, Long timeOut) throws MessagingException {
        String emailPage = getVerificationMailPage(username, verificationCode, timeOut);
        mimeMessageHelper.setText(emailPage, true);
    }

    private void sendMail(MimeMessage message){
        javaMailSender.send(message);
    }

    private String getVerificationMailPage(String username, String verificationCode,
                                           Long timeOut) {
        Context context = new Context();
        context.setVariable("username", username);
        context.setVariable("verificationCode", verificationCode);
        context.setVariable("timeOut", timeOut);
        context.setVariable("timeUnit", "分钟");

        return templateEngine.process("verificationMailTemplate", context);
    }

    @Override
    public void handle(Exception e, ReactiveResponse<T> response){
        log.info(e.getMessage());
        if (e instanceof InvalidTokenException) {
            response.setError(Status.INVALID_TOKEN);
        }else if (e instanceof InvalidPasswordException){
            response.setError(Status.USER_PASSWORD_WRONG);
        } else if (e instanceof MessagingException || e instanceof MailException) {
            response.setError(Status.MAIL_SERVICE_NOT_AVAILABLE);
        } else if (e instanceof TooLongTextException){
            response.setError(Status.TEXT_FORMAT_WRONG);
        } else {
            log.error(Arrays.toString(e.getStackTrace()));
            response.setError(Status.SERVER_ERROR);
        }
    }

}
