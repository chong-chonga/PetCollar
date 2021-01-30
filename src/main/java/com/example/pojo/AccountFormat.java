package com.example.pojo;

import com.example.response.ReactiveResponse.StatusCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Lexin Huang
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AccountFormat {
    public static final int CORRECT = 1;

    public static final int USERNAME_FORMAT_WRONG = 2;

    public static final int PASSWORD_FORMAT_WRONG = 3;

    private Integer formatVal;

    public boolean isCorrect(){
        return this.formatVal == CORRECT;
    }


    public Integer getStatusCode(){
        switch (formatVal){
            case CORRECT:
                return StatusCode.CORRECT;
            case USERNAME_FORMAT_WRONG:
                return StatusCode.USERNAME_FORMAT_WRONG;
            case PASSWORD_FORMAT_WRONG:
                return StatusCode.PASSWORD_FORMAT_WRONG;
            default:
                return null;
        }
    }

}
