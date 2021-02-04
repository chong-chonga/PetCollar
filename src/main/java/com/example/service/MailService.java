package com.example.service;

import com.example.pojo.User;

import javax.mail.MessagingException;

/**
 * @author Lexin Huang
 * @since 2.0
 */
public interface MailService {

    void sendVerificationCodeMail(User user, String verificationCode, Long timeOut) throws MessagingException;

}
