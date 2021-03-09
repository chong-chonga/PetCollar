package com.example.pojo;

import lombok.Data;
import lombok.ToString;

/**
 * @author Lexin Huang
 */
@Data
@ToString
public class UserUpdateInfo {
    private String username;
    private String emailAddress;
    private String userIntroduction;
}
