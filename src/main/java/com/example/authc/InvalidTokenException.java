package com.example.authc;


import com.example.request.RequestException;

/**
 * @author Lexin Huang
 */
public class InvalidTokenException extends RequestException {

    public InvalidTokenException(String msg) {
        super(msg);
    }

}
