package com.example.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.thymeleaf.util.StringUtils;

/**
 * @author 悠一木碧
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User {
    private Integer userId;
    private String userPortraitPath;
    private String account;
    private String password;
    private String userIntroduction;

}
