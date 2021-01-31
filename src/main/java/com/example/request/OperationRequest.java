package com.example.request;

import com.example.pojo.AccountInfo;
import com.example.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Lexin Huang
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class OperationRequest {
    private String token;
    private String username;
    private String oldPassword;
    private String newPassword;
    private String emailAddress;
    private AccountInfo accountInfo;
    OperationType operationType;

    public AccountInfo getAccount() {
        if (null == this.accountInfo) {
            accountInfo = new AccountInfo(this.username, this.oldPassword,
                    this.newPassword, this.emailAddress, AccountInfoType.MODIFY_INFORMATION);
        }
        return accountInfo;
    }

}
