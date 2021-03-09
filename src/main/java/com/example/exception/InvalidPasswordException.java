package com.example.exception;

import com.example.request.ServiceException;

/**
 * @author Lexin Huang
 */
public class InvalidPasswordException extends ServiceException {
    public InvalidPasswordException(String msg) {
        super(msg);
    }
}
