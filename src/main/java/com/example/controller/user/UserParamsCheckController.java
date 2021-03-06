package com.example.controller.user;

import com.example.controller.AbstractParamsCheckController;
import com.example.exception.TooLongTextException;
import com.example.exception.EmailFormatException;
import com.example.exception.PasswordFormatException;
import com.example.exception.UsernameFormatException;
import com.example.pojo.User;
import org.apache.logging.log4j.util.Strings;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Lexin Huang
 * 抽象类的实现, 不仅实现了对名称的校验, 同时为子类提供了密码, 邮箱格式参数的校验
 */
public class UserParamsCheckController extends AbstractParamsCheckController<User> {

    private static final int PERSONAL_INTRODUCTION_MAX_LEN = 255;

    protected void usernameFormatCheck(String usernameToCheck) throws UsernameFormatException {
        if (!usernameFormatCorrect(usernameToCheck)){
            throw new UsernameFormatException("名称: " + usernameToCheck + " 的用户名格式有误!");
        }
    }

    protected void passwordFormatCheck(String passwordToCheck) throws PasswordFormatException {
        if (!passwordFormatCorrect(passwordToCheck)){
            throw new PasswordFormatException("密码: " + passwordToCheck + " 的用户名格式有误!");
        }
    }

    protected void emailFormatCheck(String emailAddressToCheck) throws EmailFormatException {
        if (!emailAddressFormatCorrect(emailAddressToCheck)){
            throw new EmailFormatException("邮箱: " + emailAddressToCheck + " 格式不合规范!");
        }
    }

    protected void introductionFormatCheck(String introduction) throws EmailFormatException {
        if (!Objects.isNull(introduction) && PERSONAL_INTRODUCTION_MAX_LEN < introduction.length()){
            throw new TooLongTextException("个人介绍不能超过 255 个字符!");
        }
    }

    /**
     * 正则表达式验证昵称
     */
    private boolean usernameFormatCorrect(String testName) {
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
    private boolean passwordFormatCorrect(String testPassword) {
        if (null == testPassword) {
            return false;
        }
        //密码格式: 限6-18 位，字母、数字、~!@#$%^&*()+=|{}':;,\\.<>/?等特殊字符字符
        String regStr = "^([A-Z]|[a-z]|[0-9]|[~!@#$%^&*()+=|{}':;,\\\\.<>/\\-_?]){6,18}$";
        return testPassword.matches(regStr);
    }


    private boolean emailAddressFormatCorrect(String emailAddress){
        if(!Strings.isEmpty(emailAddress)){
            Pattern pattern = Pattern.compile("[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$");
            Matcher matcher = pattern.matcher(emailAddress);
            return matcher.matches();
        } else{
            return false;
        }
    }

    @Override
    protected void entityFormatCheck(User objToCheck) {
        if (Objects.isNull(objToCheck)) {
            throw new NullPointerException("校验参数不能为空!");
        }
        usernameFormatCheck(objToCheck.getUsername());
        passwordFormatCheck(objToCheck.getPassword());
        emailFormatCheck(objToCheck.getEmailAddress());
    }
}
