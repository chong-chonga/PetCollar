package com.example.exception;


import com.example.request.ServiceException;

/**
 * @author Lexin Huang
 */
public class InvalidTokenException extends ServiceException {

    public InvalidTokenException(String msg) {
        super(msg);
    }

}
