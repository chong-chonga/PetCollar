package com.example.response;

import com.example.pojo.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Lexin Huang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@ToString
public class AccountSettingsRequestData extends ReactiveData{
    private User user;
    private String token;
}
