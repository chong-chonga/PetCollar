package com.example.exception;

/**
 * @author Lexin Huang
 */
public class UnavailableUsernameException extends UnavailableNameException {
    public UnavailableUsernameException(String msg) {
        super(msg);
    }
}
