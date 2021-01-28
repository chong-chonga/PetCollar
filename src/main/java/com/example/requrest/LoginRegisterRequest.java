package com.example.requrest;

import com.example.pojo.Account;
import lombok.*;

import java.util.Objects;

/**
 * @author Lexin Huang
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class LoginRegisterRequest extends Account {
    private Account account;

    public Account getAccount(){
        if(Objects.isNull(account)){
            account = new Account(this.getUsername(), this.getPassword());
        }
        return account;
    }
}
