package com.example.exception;

import com.example.request.RequestException;

/**
 * @author Lexin Huang
 */
public class PasswordFormatException extends RequestException {
    public PasswordFormatException(String msg) {
        super(msg);
    }
}
