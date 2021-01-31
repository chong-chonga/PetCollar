package com.example.request;

import com.example.pojo.AccountInfo;
import lombok.*;


/**
 * @author Lexin Huang
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AccountVerificationRequest{
    protected String username;

    protected String password;

    protected String emailAddress;

    private AccountInfo accountInfo;

    private AccountInfoType infoType;

    public AccountInfo getAccountInfo() {
        if (null == this.accountInfo) {
            accountInfo = new AccountInfo();
            accountInfo.setUsername(username);
            accountInfo.setPassword(password);
            accountInfo.setEmailAddress(emailAddress);
            accountInfo.setInfoType(infoType);
        }
        return accountInfo;
    }
}
