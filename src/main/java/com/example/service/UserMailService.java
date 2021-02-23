package com.example.service;

import com.example.pojo.User;
import org.springframework.mail.MailException;

import javax.mail.MessagingException;

/**
 * @author Lexin Huang
 * @since 3.0
 */
public interface UserMailService {

    void sendVerificationCodeMail(User user, String verificationCode, Long timeOut) throws MessagingException, MailException;
}
