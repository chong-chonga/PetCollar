package com.example.exception.user;

import com.example.request.ServiceException;

/**
 * @author Lexin Huang
 */
public class VerificationCodeMismatchException extends ServiceException {
    public VerificationCodeMismatchException(String msg) {
        super(msg);
    }
}
