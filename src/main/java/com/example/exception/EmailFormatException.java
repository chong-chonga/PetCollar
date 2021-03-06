package com.example.exception;

import com.example.request.RequestException;

/**
 * @author Lexin Huang
 */
public class EmailFormatException extends RequestException {
    public EmailFormatException(String msg) {
        super(msg);
    }
}
