package com.example.request.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Pattern;

/**
 * @author Lexin Huang
 */
@Data
@ApiModel(value = "用户个人设置请求信息")
public class UserSettingsRequestDTO {

    @Pattern(regexp = "^[\\u4e00-\\u9fa5a-zA-Z0-9]{4,16}$",
            message = "用户名必须在 4-16个字符以内，支持中英文、数字", groups = {ModifyProfileGroup.class})
    @ApiModelProperty(value = "用户名")
    private String username;

    @Pattern(regexp = "^([A-Z]|[a-z]|[0-9]|[~!@#$%^&*()+=|{}':;,\\\\.<>/\\-_?]){6,18}$",
            message = "密码必须在 6-18 位，支持字母、数字、特殊字符",
            groups = {ChangePasswordGroup.class})
    @ApiModelProperty(value = "密码")
    private String oldPassword;

    @Pattern(regexp = "^([A-Z]|[a-z]|[0-9]|[~!@#$%^&*()+=|{}':;,\\\\.<>/\\-_?]){6,18}$",
            message = "密码必须在 6-18 位，支持字母、数字、特殊字符",
            groups = {ChangePasswordGroup.class})
    @ApiModelProperty(value = "密码")
    private String newPassword;

    @Length(max = 255, message = "个人介绍必须在 255 个字符以内!", groups = {ModifyProfileGroup.class})
    @ApiModelProperty(value = "用户个人介绍; 可以为空字符串, 不可以为 null !")
    private String userIntroduction;

    @Pattern(regexp = "[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$", message = "请输入正确的邮箱!"
            , groups = {ModifyProfileGroup.class})
    @ApiModelProperty(value = "用户邮箱")
    private String emailAddress;

    public interface ChangePasswordGroup{

    }
    public interface ModifyProfileGroup{

    }

}
