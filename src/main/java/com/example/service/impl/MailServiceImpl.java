package com.example.service.impl;

import com.example.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Lexin Huang
 * @since 2.0
 */
@Slf4j
@Service
public class MailServiceImpl implements MailService {
    @Override
    public void sendCheckCodeMail(String receiverAddress, String content) {

    }

    @Override
    public void sendTextMail(String receiverAddress, String subject, String content) {

    }

    @Override
    public void sendHtmlMail(String receiverAddress, String subject, String content) {

    }
}
