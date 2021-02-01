package com.example.request;

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

    private AccountVerificationRequestType requestType;

}
