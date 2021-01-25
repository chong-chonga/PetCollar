package com.example.controller;

import com.example.exception.FormatErrorException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Lexin Huang
 */
@RestController
public class ErrorTriggerController {
    @GetMapping("/e1")
    public String error1(@RequestParam("a") Integer a){
        int i  = 1 / 0;
        return "/";
    }

    @GetMapping("/e2")
    public String error2() throws FormatErrorException {
        throw new FormatErrorException();
    }
}
