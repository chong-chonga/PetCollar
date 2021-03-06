package com.example.config;

import com.example.authc.InvalidTokenException;
import com.example.response.ReactiveResponse;
import com.example.response.Status;
import com.example.response.data.user.UserLoginRegisterRequestData;
import com.example.exception.EmailFormatException;
import com.example.exception.PasswordFormatException;
import com.example.exception.UsernameFormatException;
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

    @ResponseBody
    @ExceptionHandler(UsernameFormatException.class)
    public ReactiveResponse<UserLoginRegisterRequestData> handleUsernameException(Exception e){
        log.info(e.getMessage());
        ReactiveResponse<UserLoginRegisterRequestData> response = new ReactiveResponse<>();
        response.setError(Status.USERNAME_FORMAT_WRONG);
        return response;
    }

    @ResponseBody
    @ExceptionHandler(PasswordFormatException.class)
    public ReactiveResponse<UserLoginRegisterRequestData> handlePasswordException(Exception e){
        log.info(e.getMessage());
        ReactiveResponse<UserLoginRegisterRequestData> response = new ReactiveResponse<>();
        response.setError(Status.PASSWORD_FORMAT_WRONG);
        return response;
    }

    @ResponseBody
    @ExceptionHandler(EmailFormatException.class)
    public ReactiveResponse<UserLoginRegisterRequestData> handleEmailException(Exception e){
        log.info(e.getMessage());
        ReactiveResponse<UserLoginRegisterRequestData> response = new ReactiveResponse<>();
        response.setError(Status.EMAIL_FORMAT_WRONG);
        return response;
    }


    @ExceptionHandler(value = BindException.class)
    @ResponseBody
    public ReactiveResponse<UserLoginRegisterRequestData> violationException(BindException exception) {
        // 不带任何参数访问接口,会抛出 BindException
        ReactiveResponse<UserLoginRegisterRequestData> response = new ReactiveResponse<>();
        String message = exception.getAllErrors().get(0).getDefaultMessage();
        response.setFormatError(message);
        return response;
    }

}
