package com.example.request.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Pattern;

/**
 * @author Lexin Huang
 */
@Data
@ApiModel("重置密码请求参数")
public class ResetPasswordReqDTO {
    @Pattern(regexp = "^[\\u4e00-\\u9fa5a-zA-Z0-9]{4,16}$",
            message = "用户名必须在 4-16个字符以内，支持中英文、数字")
    @ApiModelProperty(value = "用户名")
    private String username;

    @Pattern(regexp = "^([A-Z]|[a-z]|[0-9]|[~!@#$%^&*()+=|{}':;,\\\\.<>/\\-_?]){6,18}$",
            message = "密码必须在 6-18 位，支持字母、数字、特殊字符")
    @ApiModelProperty(value = "密码")
    private String password;

    @Length(min = 4, max = 8, message = "验证码格式有误!")
    @ApiModelProperty(value = "邮箱验证码")
    private String verificationCode;
}
