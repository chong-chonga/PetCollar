package com.example.service;

/**
 * @author Lexin Huang
 * @since 2.0
 */
public interface MailService {
    void sendCheckCodeMail(String receiverAddress, String content);

    void sendTextMail(String receiverAddress, String subject, String content);

    void sendHtmlMail(String receiverAddress, String subject, String content);

}
