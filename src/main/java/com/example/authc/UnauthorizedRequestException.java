package com.example.authc;

import com.example.request.RequestException;

/**
 * @author Lexin Huang
 */
public class UnauthorizedRequestException extends RequestException {

    public UnauthorizedRequestException(String msg){
        super(msg);
    }

}
