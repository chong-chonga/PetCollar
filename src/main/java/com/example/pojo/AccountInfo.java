package com.example.pojo;

import com.example.request.AccountInfoType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Lexin Huang
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AccountInfo {

    private String username;
    private String password;
    private String newPassword;
    private String emailAddress;
    private AccountInfoType infoType;
}
