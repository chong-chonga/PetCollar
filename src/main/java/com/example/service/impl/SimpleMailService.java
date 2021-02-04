package com.example.service.impl;

import com.example.pojo.User;
import com.example.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * @author Lexin Huang
 * @since 2.0
 */
@Slf4j
@Service("simpleMailService")
public class SimpleMailService implements MailService {
    private final JavaMailSenderImpl javaMailSender;

    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private  String senderMailAddress;

    public SimpleMailService(JavaMailSenderImpl javaMailSender, TemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }


    @Override
    public void sendVerificationCodeMail(User user, String verificationCode, Long timeOut) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "utf-8");
        mimeMessage.addRecipients(MimeMessage.RecipientType.CC, InternetAddress.parse(senderMailAddress));
        mimeMessageHelper.setSubject("宠物项圈科技账号验证");
        mimeMessageHelper.setFrom(senderMailAddress);
        String emailPage = getVerificationMailPage(user.getUsername(), verificationCode, timeOut, "分钟");
        mimeMessageHelper.setText(emailPage, true);
        mimeMessageHelper.setTo(user.getEmailAddress());
        javaMailSender.send(mimeMessage);
    }


    private String getVerificationMailPage(String username,       String verificationCode,
                                           Long timeOut, String timeUnit) {
        Context context = new Context();
        context.setVariable("username", username);
        context.setVariable("verificationCode", verificationCode);
        context.setVariable("timeOut", timeOut);
        context.setVariable("timeUnit", timeUnit);
        return templateEngine.process("verificationMailTemplate", context);
    }
}
