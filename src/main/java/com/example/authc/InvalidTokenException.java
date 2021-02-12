package com.example.authc;

import org.apache.shiro.authc.AuthenticationException;

/**
 * @author Lexin Huang
 */
public class InvalidTokenException extends AuthenticationException {

    public InvalidTokenException(String msg){
        super(msg);
    }

}
