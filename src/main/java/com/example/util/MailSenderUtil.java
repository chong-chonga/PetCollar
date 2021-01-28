package com.example.util;


import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;


/**
 * @author Lexin Huang
 */
@Slf4j
@Component
public class MailSenderUtil {

    final
    JavaMailSenderImpl javaMailSender;


    public MailSenderUtil(JavaMailSenderImpl javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendEmail(String receiverAddress, String verificationCode) throws MessagingException {

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        mimeMessageHelper.setSubject("邮箱验证");
        mimeMessageHelper.setFrom("hlx_1612682622@163.com");

        String emailPage = getEmailPage(verificationCode, 5, "分钟");
        // 设置文件内容 第二个参数设置是否支持html
        mimeMessageHelper.setText(emailPage, true);
        mimeMessageHelper.setTo(receiverAddress);

        javaMailSender.send(mimeMessage);
    }

    private String getEmailPage(String verificationCode, Integer period, String timeUnit) {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <style>\n" +
                "        #checkCode{\n" +
                "            color: #1876b5;\n" +
                "            font-size: large;\n" +
                "            font-weight: bolder;\n" +
                "        }\n" +
                "        #warn{\n" +
                "            color: #767676;\n" +
                "        }\n" +
                "        #main{\n" +
                "            text-align: center;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div id=\"main\">\n" +
                "        <p>亲爱的会员： 您好！</p>\n" +
                "        <br/>\n" +
                "        <p>您本次操作的验证码为：<font id=\"checkCode\">"+verificationCode+"</font" +
                ">，请在返回输入框完成输入, 以完成操作。" + "(有效期:"+period+timeUnit+")</p>" + "        <br/><br/>\n" +
                "        <div id=\"warn\">\n" +
                "            <p>注意：此操作可能会修改您的密码、登录邮箱或绑定手机。如非本人操作，请及时登录并修改密码以保证帐户安全</p>\n" +
                "            <p>（工作人员不会向你索取此验证码，请勿泄漏！)</p>\n" +
                "            <br/>\n" +
                "            <p>2020-2021 &copy; from admin Lexin Huang 宠物科技集团</p>\n" +
                "        </div>\n" +
                "\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }


}
