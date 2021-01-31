package com.example.requrest;

import com.example.pojo.Account;
import com.example.pojo.AccountVerificationLevel;
import lombok.*;

import java.util.Objects;

/**
 * @author Lexin Huang
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AccountRequest {
    private String username;
    private String password;
    private String emailAddress;
    private AccountVerificationLevel level;
    private Account account;

    public Account getAccount(){
        if(Objects.isNull(account)){
            account = new Account(this.getUsername(), this.getPassword(), this.emailAddress);
        }
        return account;
    }
}
