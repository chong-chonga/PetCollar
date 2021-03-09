package com.example.service.user.impl;

import com.example.dao.CacheDao;
import com.example.pojo.User;
import com.example.service.user.*;
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
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Lexin Huang
 */
@Slf4j
public class BasicUserService extends EnhanceUserQueryServiceImpl
                                implements UserAuthorizationService, UserMailService {

    private final static String VERIFICATION_CODE_TIME_UNIT = "分钟";

    private final CacheDao cacheDao;

    private final JavaMailSenderImpl javaMailSender;

    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String senderMailAddress;

    protected BasicUserService(CacheDao cacheDao, JavaMailSenderImpl javaMailSender,
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


    @Override
    public void sendVerificationCodeMail(User user, String verificationCode, Long timeOut) throws
                                                                MessagingException, MailException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "utf-8");
        addCc(mimeMessage);
        configureBasicEmailInformation(mimeMessageHelper, senderMailAddress, user.getEmailAddress());
        configureEmailContent(mimeMessageHelper, user.getUsername(), verificationCode, timeOut);
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

    private void configureEmailContent(MimeMessageHelper mimeMessageHelper, String username,
                                       String verificationCode, Long timeOut) throws MessagingException {
        Context context = new Context();
        render(context, username, verificationCode, timeOut);
        String emailPageTemplateName = "verificationMailTemplate";
        String content = templateEngine.process(emailPageTemplateName, context);
        mimeMessageHelper.setText(content, true);
    }

    private void render(Context context, String username, String verificationCode, Long timeOut) {
        context.setVariable("username", username);
        context.setVariable("verificationCode", verificationCode);
        context.setVariable("timeOut", timeOut);
        context.setVariable("timeUnit", VERIFICATION_CODE_TIME_UNIT);
    }

    private void sendMail(MimeMessage message) throws MailException {
        javaMailSender.send(message);
    }

}
