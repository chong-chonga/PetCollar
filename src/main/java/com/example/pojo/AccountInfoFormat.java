package com.example.pojo;

import com.example.request.AccountInfoType;
import com.example.response.ReactiveResponse.StatusCode;
import lombok.Data;
import lombok.ToString;
import org.apache.logging.log4j.util.Strings;


/**
 * @author Lexin Huang
 */
@Data
@ToString
public class AccountInfoFormat {

    private Integer formatVal;

    private AccountInfoFormat(){
        this.formatVal = StatusCode.CORRECT;
    }

    public boolean isCorrect() {
        return this.formatVal == StatusCode.CORRECT;
    }

    public Integer getStatusCode() {
        return formatVal;
    }

    public static AccountInfoFormat solveAccountInfoFormat(AccountInfo accountInfo) {
        AccountInfoFormat accountInfoFormat = new AccountInfoFormat();
        if(AccountInfoType.MODIFY_INFORMATION != accountInfo.getInfoType()
                && usernameFormatWrong(accountInfo.getUsername())){
            accountInfoFormat.formatVal = StatusCode.USERNAME_FORMAT_WRONG;
        }
        else if(AccountInfoType.RETRIEVE_PASSWORD != accountInfo.getInfoType()
                && passwordFormatWrong(accountInfo.getPassword())){
            accountInfoFormat.formatVal = StatusCode.PASSWORD_FORMAT_WRONG;
        }
        else if(AccountInfoType.REGISTER == accountInfo.getInfoType()
                || AccountInfoType.MODIFY_INFORMATION == accountInfo.getInfoType()){
            if(emailAddressFormatWrong(accountInfo.getEmailAddress())){
                accountInfoFormat.formatVal = StatusCode.EMAIL_ADDRESS_NOT_SUPPORTED;
            }
            else if(AccountInfoType.MODIFY_INFORMATION == accountInfo.getInfoType()
                    && passwordFormatWrong(accountInfo.getNewPassword())){
               accountInfoFormat.formatVal = StatusCode.NEW_PASSWORD_FORMAT_WRONG;
            }

        }
        return accountInfoFormat;
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


    private static boolean emailAddressFormatWrong(String emailAddress){
        return Strings.isEmpty(emailAddress) ||
                        (!emailAddress.endsWith("@126.com") &&
                        !emailAddress.endsWith("@163.com") &&
                        !emailAddress.endsWith("@qq.com"));
    }

}
