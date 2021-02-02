package com.example.util;

import com.example.request.AccountVerificationRequest;
import com.example.request.AccountVerificationRequestType;
import com.example.response.ReactiveResponse.StatusCode;
import lombok.Data;
import lombok.ToString;
import org.apache.logging.log4j.util.Strings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author Lexin Huang
 */
@Data
@ToString
public class AccountRequestInfoFormat {

    private Integer formatVal;

    private AccountRequestInfoFormat(){
        this.formatVal = StatusCode.CORRECT;
    }

    public boolean isCorrect() {
        return this.formatVal == StatusCode.CORRECT;
    }

    public Integer getStatusCode() {
        return formatVal;
    }


    public static AccountRequestInfoFormat solveRequestInfoFormat(AccountVerificationRequest request) {

        AccountRequestInfoFormat accountRequestInfoFormat = new AccountRequestInfoFormat();

        if(!usernameFormatCorrect(request.getUsername())){
            accountRequestInfoFormat.formatVal = StatusCode.USERNAME_FORMAT_WRONG;
        }
        else if(AccountVerificationRequestType.RESET_PASSWORD != request.getRequestType()
                && AccountVerificationRequestType.EMAIL_CHECK != request.getRequestType()
                && (!passwordFormatCorrect(request.getPassword())) ){
            accountRequestInfoFormat.formatVal = StatusCode.PASSWORD_FORMAT_WRONG;
        }
        else if(AccountVerificationRequestType.REGISTER == request.getRequestType()){
            if(!emailAddressFormatCorrect(request.getEmailAddress())){
                accountRequestInfoFormat.formatVal = StatusCode.EMAIL_ADDRESS_NOT_SUPPORTED;
            }
        }
        return accountRequestInfoFormat;
    }


    /**
     * 正则表达式验证昵称
     */
    public static boolean usernameFormatCorrect(String testName) {
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
