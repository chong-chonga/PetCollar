package com.example.exception.pet;


import com.example.exception.UnavailableNameException;

/**
 * @author Lexin Huang
 */
public class UnavailablePetNameException extends UnavailableNameException {
    public UnavailablePetNameException(String msg) {
        super(msg);
    }
}
