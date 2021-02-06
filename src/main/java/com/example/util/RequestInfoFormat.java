package com.example.util;

import com.example.request.AccountVerificationRequest;
import com.example.request.AccountVerificationRequestType;
import com.example.response.ReactiveResponse.StatusCode;
import lombok.Data;
import lombok.ToString;
import org.apache.logging.log4j.util.Strings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.request.AccountVerificationRequestType.*;


/**
 * @author Lexin Huang
 */
@Data
@ToString
public class RequestInfoFormat {

    private Integer formatVal;

    private RequestInfoFormat(Integer val){
        this.formatVal = val;
    }

    public boolean isCorrect() {
        return this.formatVal == StatusCode.CORRECT;
    }

    public Integer getStatusCode() {
        return formatVal;
    }


    public static RequestInfoFormat solveRequestInfoFormat(AccountVerificationRequest request) {

        RequestInfoFormat requestInfoFormat = new RequestInfoFormat(StatusCode.CORRECT);
        AccountVerificationRequestType type = request.getRequestType();
//        免密登录不检查任何格式
        if(TOKEN_LOGIN == type){
            return requestInfoFormat;
        }
        String username = request.getUsername();
        String password = request.getPassword();
        String emailAddress = request.getEmailAddress();
//        提交重置密码时, 不检查用户名; 登录/注册/找回密码时检查
        if(SUBMIT_RESET != type){
            usernameCheck(username, requestInfoFormat);
        }
//        发送邮件时, 不检查密码和邮箱格式
        if(SEND_EMAIL != type){
            passwordCheck(password, requestInfoFormat);
//            只有注册时, 才会检查邮箱格式
            if(REGISTER == type){
                emailCheck(emailAddress, requestInfoFormat);
            }
        }
        return requestInfoFormat;
    }


    private static void passwordCheck(String password, RequestInfoFormat requestInfoFormat) {
        if(!passwordFormatCorrect(password)){
            requestInfoFormat.formatVal = StatusCode.PASSWORD_FORMAT_WRONG;
        }
    }


    private static void usernameCheck(String username, RequestInfoFormat requestInfoFormat) {
        if(!nameFormatCorrect(username)){
            requestInfoFormat.setFormatVal(StatusCode.NAME_FORMAT_WRONG);
        }
    }


    private static void emailCheck(String emailAddress,
                                   RequestInfoFormat requestInfoFormat){
        if(!emailAddressFormatCorrect(emailAddress)){
            requestInfoFormat.formatVal = StatusCode.EMAIL_ADDRESS_NOT_SUPPORTED;
        }
    }



    /**
     * 正则表达式验证昵称
     */
    public static boolean nameFormatCorrect(String testName) {
        if (null == testName) {
            return false;
        }
        // 昵称格式：限4-16个字符，支持中英文、数字
        String regStr = "^[\\u4e00-\\u9fa5a-zA-Z0-9]{4,16}$";
        return testName.matches(regStr);
    }

    /**
     * 正则表达式验证密码
     */
    public static boolean passwordFormatCorrect(String testPassword) {
        if (null == testPassword) {
            return false;
        }
        //密码格式: 6-18 位，字母、数字、字符
        String regStr = "^([A-Z]|[a-z]|[0-9]|[~!@#$%^&*()+=|{}':;,\\\\.<>/?])" +
                "{6,18}$";
        return testPassword.matches(regStr);
    }


    public static boolean emailAddressFormatCorrect(String emailAddress){
        if(!Strings.isEmpty(emailAddress) &&
           (emailAddress.endsWith("@qq.com") ||
            emailAddress.endsWith("@126.com") ||
            emailAddress.endsWith("@163.com")  )){
            Pattern pattern = Pattern.compile("[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$");
            Matcher matcher = pattern.matcher(emailAddress);
            return matcher.matches();
        } else{
            return false;
        }
    }

}
