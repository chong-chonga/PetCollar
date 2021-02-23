package com.example.service.impl.user;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.authc.InvalidTokenException;
import com.example.dao.CacheDao;
import com.example.mapper.UserMapper;
import com.example.pojo.User;
import com.example.service.BasicRequestInfoFormatService;
import com.example.service.BasicUserCacheService;
import com.example.service.UserMailService;
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
public class DefaultUserService extends ServiceImpl<UserMapper, User> implements BasicUserCacheService,
        UserMailService, BasicRequestInfoFormatService {

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
    public void refreshTokenTime(String token, User user) {
        if (Objects.isNull(token)) {
            throw new NullPointerException("token 不能为 null!");
        }
        if (Objects.isNull(user.getUsername())) {
            throw new NullPointerException("username 不能为 null!");
        }
        long timeOut = 7L;
        putUserCache(token, user, timeOut);
        putStringCache(user.getUsername(), token, timeOut, TimeUnit.DAYS);
    }

    private void putUserCache(String k, User user, Long timeOut) {
        cacheDao.set(k, user, timeOut, TimeUnit.DAYS);
    }

    @Override
    public void putStringCache(String k, String v, Long timeOut, TimeUnit timeUnit) {
        if (Objects.isNull(k)) {
            throw new NullPointerException("k 不能为 null !");
        }
        cacheDao.set(k, v, timeOut, timeUnit);
    }

    @Override
    public User getUserByToken(String token) throws InvalidTokenException {
        if (Objects.isNull(token)) {
            throw new InvalidTokenException("token 不能为 null !");
        }
        User user = cacheDao.getUserCache(token);
        if (Objects.isNull(user)) {
            throw new InvalidTokenException("值为 " + token + " 的token 不存在!");
        }
        return user;
    }

    @Override
    public String getStringCache(String k) {
        if (Objects.isNull(k)) {
            throw new NullPointerException("k 不能为 null !");
        }
        return cacheDao.getStringCache(k);
    }

    @Override
    public void removeToken(String username) {
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


    protected void updatePassword(String targetUsername, String password) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(true, "user_username", targetUsername)
                .set("user_password", password);
        update(updateWrapper);
    }


    @Override
    public void sendVerificationCodeMail(User user, String verificationCode, Long timeOut) throws MessagingException, MailException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "utf-8");
        mimeMessage.addRecipients(MimeMessage.RecipientType.CC, InternetAddress.parse(senderMailAddress));
        mimeMessageHelper.setSubject("宠物项圈科技账号验证");
        mimeMessageHelper.setFrom(senderMailAddress);
        String emailPage = getVerificationMailPage(user.getUsername(), verificationCode, timeOut);
        mimeMessageHelper.setText(emailPage, true);
        mimeMessageHelper.setTo(user.getEmailAddress());
        javaMailSender.send(mimeMessage);
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
    public Boolean isNameFormatCorrect(String testName) {
        if (null == testName) {
            return false;
        }
        // 昵称格式：限4-16个字符，支持中英文、数字
        String regStr = "^[\\u4e00-\\u9fa5a-zA-Z0-9]{4,16}$";
        return testName.matches(regStr);
    }

    @Override
    public Boolean isTextFormatCorrect(String testText) {
        return null != testText && 255 >= testText.length();
    }

    public boolean passwordFormatCorrect(String testPassword){
        if (null == testPassword) {
            return false;
        }
        //密码格式: 限6-18 位，字母、数字、~!@#$%^&*()+=|{}':;,\\.<>/?等特殊字符字符
        String regStr = "^([A-Z]|[a-z]|[0-9]|[~!@#$%^&*()+=|{}':;,\\\\.<>/\\-_?]){6,18}$";
        return testPassword.matches(regStr);
    }

}
