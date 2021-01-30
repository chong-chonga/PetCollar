package com.example.util;

import com.example.pojo.Account;
import com.example.pojo.AccountFormat;

import java.util.Objects;


/**
 * @author Lexin Huang
 */
public class FormatUtil {


    public static AccountFormat solveAccountFormat(Account account){
        AccountFormat accountFormat = new AccountFormat();
        if(Objects.isNull(account)){
            throw new RuntimeException("解析账号格式出错!");
        } else{
            Integer val = solveValue(account);
            accountFormat.setFormatVal(val);
            return accountFormat;
        }
    }

    private static Integer solveValue(Account account){
        if(usernameFormatWrong(account.getUsername())){
            return AccountFormat.USERNAME_FORMAT_WRONG;
        } else if(passwordFormatWrong(account.getPassword())){
            return AccountFormat.PASSWORD_FORMAT_WRONG;
        } else{
            return AccountFormat.CORRECT;
        }
    }

    /**
     * 正则表达式验证昵称
     */
    private static boolean usernameFormatWrong(String testName) {
        if(null == testName){
            return true;
        }
        // 昵称格式：限20个字符，支持中英文、数字
        String regStr = "^[\\u4e00-\\u9fa5a-zA-Z0-9]{4,16}$";
        return !testName.matches(regStr);
    }

    /**
     * 正则表达式验证密码
     */
    private static boolean passwordFormatWrong(String testPassword) {
        if(null == testPassword){
            return true;
        }
        // 6-20 位，字母、数字、字符
        String regStr = "^([A-Z]|[a-z]|[0-9]|[`~!@#$%^&*()+=|{}':;,\\\\.<>/?！￥…（）—【】‘；：”“’。，、？])" +
                "{6,18}$";
        return !testPassword.matches(regStr);
    }

}
