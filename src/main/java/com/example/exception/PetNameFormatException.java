package com.example.exception;

import com.example.request.RequestException;

/**
 * @author Lexin Huang
 */
public class PetNameFormatException extends RequestException {
    public PetNameFormatException(String msg) {
        super(msg);
    }
}
