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
public class UserLoginRegisterRequest {

    private String username;

    private String password;

    private String emailAddress;

    private String verificationCode;

    private String token;

    private UserLoginRegisterRequestType requestType;



    /**
     * 用于创建注册时, 用于插入表的 User 对象
     * 此方法会默认将会指定用户的默认头像, 参见 resources 目录
     * @return User 对象, 参见{@link User}
     */
    public User createUserToRegister() {
        return new User(null, "http://www.petcollar.top:8082/image/headPortrait/user/default.png", this.username,
                this.password, this.emailAddress, "您还没有介绍哦~");
    }


}
