package com.example.response;

import com.example.pojo.User;
import lombok.*;

/**
 * @author Lexin Huang
 * @since 2.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@ToString
public class UserLoginRegisterRequestData extends ReactiveData{
    private String token;
    private User user;
    private String verificationCode;

}
