package com.example.response.data.user;

import com.example.pojo.User;
import com.example.response.ReactiveResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Lexin Huang
 * @since 2.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@ToString
public class UserAccountRequestData extends ReactiveResponse.ApiData {
    private String token;
    private User user;
    private String verificationCode;

}
