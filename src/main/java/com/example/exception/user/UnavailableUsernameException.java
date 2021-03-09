package com.example.exception.user;

import com.example.exception.UnavailableNameException;

/**
 * @author Lexin Huang
 */
public class UnavailableUsernameException extends UnavailableNameException {
    public UnavailableUsernameException(String msg) {
        super(msg);
    }
}
