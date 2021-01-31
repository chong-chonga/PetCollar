package com.example.util;

import com.example.pojo.Account;
import com.example.pojo.AccountVerificationLevel;
import com.example.requrest.AccountRequest;
import com.example.response.ReactiveResponse.StatusCode;
import lombok.Data;
import lombok.ToString;
import org.apache.logging.log4j.util.Strings;


/**
 * @author Lexin Huang
 */
@Data
@ToString
public class AccountRequestFormat {

    private Integer formatVal;

    private AccountRequestFormat(){
        this.formatVal = StatusCode.CORRECT;
    }

    public boolean isCorrect() {
        return this.formatVal == StatusCode.CORRECT;
    }


    public Integer getStatusCode() {
        return formatVal;
    }

    public static AccountRequestFormat solveAccountRequestFormat(AccountRequest request) {
        AccountRequestFormat accountRequestFormat = new AccountRequestFormat();
        if(AccountVerificationLevel.RETRIEVE_PASSWORD != request.getLevel()
                && usernameFormatWrong(request.getUsername())){
            accountRequestFormat.formatVal = StatusCode.USERNAME_FORMAT_WRONG;
        }
        else if(AccountVerificationLevel.RETRIEVE_PASSWORD != request.getLevel()
                && passwordFormatWrong(request.getPassword())){
            accountRequestFormat.formatVal = StatusCode.PASSWORD_FORMAT_WRONG;
        }
        else if(AccountVerificationLevel.MODIFY_INFORMATION == request.getLevel()){
            if(passwordFormatWrong(request.getNewPassword())){
               accountRequestFormat.formatVal = StatusCode.NEW_PASSWORD_FORMAT_WRONG;
            }
            else if(notSupportFormatOf(request.getEmailAddress())){
                accountRequestFormat.formatVal = StatusCode.EMAIL_ADDRESS_NOT_SUPPORTED;
            }
        }
        return accountRequestFormat;
    }


    /**
     * 正则表达式验证昵称
     */
    private static boolean usernameFormatWrong(String testName) {
        if (null == testName) {
            return true;
        }
        // 昵称格式：限16个字符，支持中英文、数字
        String regStr = "^[\\u4e00-\\u9fa5a-zA-Z0-9]{4,16}$";
        return !testName.matches(regStr);
    }

    /**
     * 正则表达式验证密码
     */
    private static boolean passwordFormatWrong(String testPassword) {
        if (null == testPassword) {
            return true;
        }
        // 6-18 位，字母、数字、字符
        String regStr = "^([A-Z]|[a-z]|[0-9]|[`~!@#$%^&*()+=|{}':;,\\\\.<>/?！￥…（）—【】‘；：”“’。，、？])" +
                "{6,18}$";
        return !testPassword.matches(regStr);
    }

    private static boolean notSupportFormatOf(String emailAddress){
        return Strings.isEmpty(emailAddress) ||
                        (!emailAddress.endsWith("@126.com") &&
                        !emailAddress.endsWith("@163.com") &&
                        !emailAddress.endsWith("@qq.com"));
    }

}
