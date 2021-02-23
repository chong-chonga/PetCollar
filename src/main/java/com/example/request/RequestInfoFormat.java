package com.example.request;

import org.apache.logging.log4j.util.Strings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.request.UserLoginRegisterRequestType.*;


/**
 * @author Lexin Huang
 */
public class RequestInfoFormat {

    private RequestInfoFormat(){

    }

    public static class FormatException extends RequestException{

        public FormatException(String msg) {
            super(msg);
        }
    }

    public static class PasswordFormatException extends FormatException{

        public PasswordFormatException(String msg) {
            super(msg);
        }
    }

    public static class NameFormatException extends FormatException{

        public NameFormatException(String msg) {
            super(msg);
        }
    }

    public static class EmailFormatException extends FormatException{
        public EmailFormatException(String msg) {
            super(msg);
        }
    }

    public static void solveRequestInfoFormat(UserLoginRegisterRequest request) {

        UserLoginRegisterRequestType type = request.getRequestType();
//        免密登录不检查任何格式
        if(TOKEN_LOGIN == type){
            return;
        }
        String username = request.getUsername();
        String password = request.getPassword();
        String emailAddress = request.getEmailAddress();
//        提交重置密码时, 不检查用户名; 登录/注册/找回密码时检查
        if(SUBMIT_RESET != type){
            usernameCheck(username);
        }
//        发送邮件时, 不检查密码和邮箱格式
        if(RETRIEVE_PASSWORD != type){
            passwordCheck(password);
        }
//       只有注册时, 才会检查邮箱格式
        if(REGISTER == type){
            emailCheck(emailAddress);
        }
    }


    private static void passwordCheck(String password) {
        if(!isPasswordFormatCorrect(password)){
            throw new PasswordFormatException("密码: " + password + " 的用户名格式有误!");
        }
    }


    private static void usernameCheck(String username) {
        if(!isNameFormatCorrect(username)){
            throw new NameFormatException("名称: " + username + " 的用户名格式有误!");
        }
    }


    private static void emailCheck(String emailAddress){
        if(!isEmailAddressFormatCorrect(emailAddress)){
            throw new EmailFormatException("邮箱: " + emailAddress + " 格式不合规范!");
        }
    }



    /**
     * 正则表达式验证昵称
     */
    public static boolean isNameFormatCorrect(String testName) {
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
    public static boolean isPasswordFormatCorrect(String testPassword) {
        if (null == testPassword) {
            return false;
        }
        //密码格式: 限6-18 位，字母、数字、~!@#$%^&*()+=|{}':;,\\.<>/?等特殊字符字符
        String regStr = "^([A-Z]|[a-z]|[0-9]|[~!@#$%^&*()+=|{}':;,\\\\.<>/\\-_?]){6,18}$";
        return testPassword.matches(regStr);
    }


    public static boolean isEmailAddressFormatCorrect(String emailAddress){
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
