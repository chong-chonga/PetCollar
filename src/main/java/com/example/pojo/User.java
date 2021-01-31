package com.example.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author 悠一木碧
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@TableName("user")
public class User {
    @TableId(value = "user_id", type = IdType.AUTO)
    private Integer userId;
    @TableField(value = "user_portrait_path")
    private String userPortraitPath;
    @TableField(value = "user_username")
    private String username;
    @TableField(value = "user_password")
    private String password;
    @TableField(value = "user_email_address")
    private String emailAddress;
    @TableField(value = "user_introduction")
    private String userIntroduction;

}
