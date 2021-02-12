package com.example.util;

/**
 * @author Lexin Huang
 */
public class InvalidRequestException extends IllegalArgumentException{

    public InvalidRequestException(String msg){
        super(msg);
    }

}
