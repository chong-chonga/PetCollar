package com.example.response;

import lombok.*;

/**
 * @author Lexin Huang
 * @since 2.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@ToString
public class AccountVerificationRequestData extends ReactiveData{
    private String token;
    private String verificationCode;

}
