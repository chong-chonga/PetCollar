package com.example;

import com.example.pojo.User;
import com.example.service.MailService;
import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.mail.MessagingException;


@Slf4j
@SpringBootTest
class MailServiceTest {
    @Autowired
    MailService mailService;


    @Test
    public void testSendVerificationCodeMail() throws MessagingException {
        User user = new User();
        user.setUsername("某名用户");
        user.setEmailAddress("1612682622@qq.com");
        mailService.sendVerificationCodeMail(user, "Sc92Xpew", 1L);
    }
}
