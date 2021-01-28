package com.example.util;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Lexin Huang
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "账号或密码格式错误")
@NoArgsConstructor
public class FormatErrorException extends Exception{
    public FormatErrorException(String msg){
        super(msg);
    }
}
