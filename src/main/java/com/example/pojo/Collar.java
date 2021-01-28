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
 * @author Lexin Huang
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@TableName("collar")
public class Collar {
    @TableId(value = "collar_id", type = IdType.AUTO)
    private Integer collarId;
    @TableField("collar_introduction")
    private String collarIntroduction;
    @TableField("user_id")
    private Integer userId;
    @TableField("pet_id")
    private Integer petId;

}
