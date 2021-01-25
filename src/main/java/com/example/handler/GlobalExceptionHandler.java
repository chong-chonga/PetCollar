package com.example.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author Lexin Huang
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({MissingServletRequestParameterException.class})
    public String handleParamsExceptions(Exception e){
        return "5xx";
    }

//    @ExceptionHandler({Exception.class})
//    public String handleException(Exception e){
//        log.error("异常原因是{}", e.getMessage());
//        return "redirect:/";
//    }
}
