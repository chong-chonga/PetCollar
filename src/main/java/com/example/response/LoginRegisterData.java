package com.example.response;

import lombok.*;

/**
 * @author Lexin Huang
 * @since 2.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LoginRegisterData extends ReactiveData<String> {

    public void setVal(String token){
        super.setVal(token);
    }

}
