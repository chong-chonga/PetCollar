package com.example.exception;

import com.example.request.ServiceException;

/**
 * @author 悠一木碧
 * @create 2021/3/8 17:34
 */
public class LoginMismatchException extends ServiceException {
    public LoginMismatchException(String msg) {
        super(msg);
    }
}
