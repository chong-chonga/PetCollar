package com.example.exception;

import com.example.request.RequestException;

/**
 * @author Lexin Huang
 */
public class FormatException extends RequestException {
    public FormatException(String msg) {
        super(msg);
    }
}
