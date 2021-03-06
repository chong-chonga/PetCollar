package com.example.exception;


import com.example.request.RequestException;

/**
 * @author Lexin Huang
 */
public class UsernameFormatException extends RequestException {
    public UsernameFormatException(String msg) {
        super(msg);
    }
}
