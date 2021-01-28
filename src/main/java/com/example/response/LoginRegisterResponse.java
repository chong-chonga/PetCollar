package com.example.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Lexin Huang
 */
@ToString
public class LoginRegisterResponse extends ReactiveResponse {


    public LoginRegisterResponse() {
        super.data = new LoginRegisterData();
    }


    public void setData(String token) {
        LoginRegisterData data = (LoginRegisterData) super.data;
        data.setToken(token);
    }

    @Data
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LoginRegisterData{
        private String token;

    }

}
