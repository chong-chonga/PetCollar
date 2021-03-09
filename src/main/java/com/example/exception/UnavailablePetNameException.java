package com.example.exception;


/**
 * @author Lexin Huang
 */
public class UnavailablePetNameException extends UnavailableNameException {
    public UnavailablePetNameException(String msg) {
        super(msg);
    }
}
