package com.example.util;

/**
 * @author Lexin Huang
 */
public class NoSuchAccountVerificationTypeException extends Exception{
    public NoSuchAccountVerificationTypeException() {
        super();
    }

    public NoSuchAccountVerificationTypeException(String message) {
        super(message);
    }
}
