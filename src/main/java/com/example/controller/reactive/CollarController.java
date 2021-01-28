package com.example.controller.reactive;

import com.example.requrest.CollarRequest;
import com.example.response.CollarResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Lexin Huang
 */
@RestController("/mobile_platform/user")
public class CollarController {

    @GetMapping("/user_collars")
    public CollarResponse userCollars(CollarRequest collarRequest){
        return null;
    }
}
