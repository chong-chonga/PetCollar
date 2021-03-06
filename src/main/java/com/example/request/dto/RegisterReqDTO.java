package com.example.request.dto;

import com.example.pojo.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

/**
 * @author Lexin Huang
 */
@Data
@ApiModel(value = "注册请求参数")
public class RegisterReqDTO {

    private static final String USER_DEFAULT_AVATAR_PATH = "http://www.petcollar.top:8083/image/avatar/user/default.png";

    @Pattern(regexp = "^[\\u4e00-\\u9fa5a-zA-Z0-9]{4,16}$",
            message = "用户名必须在 4-16个字符以内，支持中英文、数字")
    @ApiModelProperty(value = "用户名")
    private String username;

    @Pattern(regexp = "^([A-Z]|[a-z]|[0-9]|[~!@#$%^&*()+=|{}':;,\\\\.<>/\\-_?]){6,18}$",
            message = "密码必须在 6-18 位，支持字母、数字、特殊字符")
    @ApiModelProperty(value = "密码")
    private String password;

    @Email(message = "请输入正确的邮箱!")
    private String emailAddress;

    public User createUserToRegister() {
        User user = new User();
        user.setUsername(this.username);
        user.setPassword(this.password);
        user.setEmailAddress(this.emailAddress);
        user.setUserPortraitPath(USER_DEFAULT_AVATAR_PATH);
        user.setUserIntroduction("这个人还没有介绍哦~");
        return user;
    }
}
