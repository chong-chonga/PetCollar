package com.example.exception;

import com.example.request.RequestException;

/**
 * @author Lexin Huang
 */
public class IllegalTextException extends RequestException {
    public IllegalTextException(String msg) {
        super(msg);
    }
}
