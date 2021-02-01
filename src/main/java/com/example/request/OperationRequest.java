package com.example.request;

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
    private OperationRequestType operationRequestType;


}
