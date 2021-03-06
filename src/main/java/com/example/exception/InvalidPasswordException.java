package com.example.exception;

import com.example.request.RequestException;

/**
 * @author Lexin Huang
 */
public class InvalidPasswordException extends RequestException {
    public InvalidPasswordException(String msg) {
        super(msg);
    }
}
