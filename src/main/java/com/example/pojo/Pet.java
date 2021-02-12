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
@TableName("pet")
public class Pet {

    @TableId(value = "pet_id", type = IdType.AUTO)
    private
    Integer petId;

    @TableField(value = "pet_portrait_path")
    private
    String petPortraitPath;

    @TableField(value = "pet_name")
    private
    String petName;

    @TableField(value = "pet_species")
    private
    String petSpecies;

    @TableField(value = "pet_introduction")
    private
    String petIntroduction;

    @TableField(value = "is_pet_overt")
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
