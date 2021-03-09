package com.example.exception;

import com.example.request.ServiceException;

/**
 * @author Lexin Huang
 */
public class UnavailableNameException extends ServiceException {
    public UnavailableNameException(String msg) {
        super(msg);
    }
}
