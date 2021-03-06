package com.example.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @author Lexin Huang
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@TableName("pet")
public class Pet {

    @TableId(value = "pet_id", type = IdType.AUTO)
    private
    Integer petId;

    @TableField(value = "pet_portrait_path")
    private
    String petPortraitPath;

    @TableField(value = "pet_name")
    @Pattern(regexp = "^[\\u4e00-\\u9fa5a-zA-Z0-9]{4,16}$", message = "宠物名称必须在 4-16个字符，支持中英文、数字")
    private
    String petName;

    @TableField(value = "pet_species")
    @Pattern(regexp = "^[\\u4e00-\\u9fa5]{2,10}$", message = "宠物品种必须在 2 - 10 个中文字符以内")
    private
    String petSpecies;

    @TableField(value = "pet_introduction")
    @Length(max = 255, message = "宠物介绍不能超过 255 个字符")
    private
    String petIntroduction;

    @TableField(value = "is_pet_overt")
    @NotNull(message = "请选择宠物是否公开!")
    private
    Boolean overt;

    @TableField(value = "pet_fence_longitude")
    private
    Double petLongitude;

    @TableField(value = "pet_fence_radius")
    private
    Double petLatitude;

    @TableField(value = "user_id")
    private
    Integer userId;

}
