package com.example.service;

/**
 * @author Lexin Huang
 */
public interface MailService {
    sendTextMail(String receiverAddress, String content);

    sendTextMail(String receiverAddress, String subject, String content);

    void sendHtmlMail(String receiverAddress, String content);

}
