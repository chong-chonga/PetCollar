package com.example.config;

import com.example.response.ReactiveResponse;
import com.example.response.Status;
import com.example.response.data.user.UserAccountRequestData;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Lexin Huang
 */
@Slf4j
@ControllerAdvice
public class ExceptionHandlerConfig {

    @ExceptionHandler(value = BindException.class)
    @ResponseBody
    public ReactiveResponse<UserAccountRequestData> violationException(BindException exception) {
        // 不带任何参数访问接口,会抛出 BindException
        ReactiveResponse<UserAccountRequestData> response = new ReactiveResponse<>();
        String message = exception.getAllErrors().get(0).getDefaultMessage();
        response.setFormatError(message);
        return response;
    }

}
