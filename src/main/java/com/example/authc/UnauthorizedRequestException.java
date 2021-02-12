package com.example.authc;

import org.apache.shiro.authc.AuthenticationException;

/**
 * @author Lexin Huang
 */
public class UnauthorizedRequestException extends AuthenticationException {

    public UnauthorizedRequestException(String msg){
        super(msg);
    }

}
