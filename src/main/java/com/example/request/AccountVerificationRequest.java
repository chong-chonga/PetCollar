package com.example.request;

import com.example.pojo.User;
import lombok.*;


/**
 * @author Lexin Huang
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AccountVerificationRequest {

    private String username;

    private String password;

    private String emailAddress;

    private String newPassword;

    private String verificationCode;

    private AccountVerificationRequestType requestType;


    /**
     * 用于创建登录时用于查表的 User 对象
     * @return User 对象, 参见{@link User}
     */
    public User createUserToLogin() {
        return new User(null, null, this.username,
                this.password, null, null);
    }


    /**
     * 用于创建注册时, 用于插入表的 User 对象
     * @return User 对象, 参见{@link User}
     */
    public User createUserToRegister() {
        return new User(null, null, this.username,
                this.password, this.emailAddress, null);
    }


}
