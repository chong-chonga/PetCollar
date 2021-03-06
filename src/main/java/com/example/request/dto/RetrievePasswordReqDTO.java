package com.example.request.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Pattern;

/**
 * @author Lexin Huang
 */
@Data
@ApiModel(value = "找回密码请求参数")
public class RetrievePasswordReqDTO {
    @Pattern(regexp = "^[\\u4e00-\\u9fa5a-zA-Z0-9]{4,16}$",
            message = "用户名必须在 4-16个字符以内，支持中英文、数字")
    @ApiModelProperty(value = "用户名")
    private String username;
}
