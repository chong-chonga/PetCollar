package com.example.controller.reactive;

import com.example.response.ReactiveResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Lexin Huang
 */
@RestController("/mobile_platform/collar")
public class CollarController {


    @GetMapping("/user_collars")
    public ReactiveResponse userCollars(){
        return null;
    }

}
