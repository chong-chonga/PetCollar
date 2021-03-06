package com.example.exception;

import com.example.request.RequestException;

/**
 * @author Lexin Huang
 */
public class TooLongTextException extends RequestException {
    public TooLongTextException(String msg) {
        super(msg);
    }
}
