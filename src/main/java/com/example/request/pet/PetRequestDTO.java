package com.example.request.pet;

import com.baomidou.mybatisplus.annotation.TableField;
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
@AllArgsConstructor
@NoArgsConstructor
public class PetRequestDTO {

    @TableField(value = "pet_name")
    @Pattern(regexp  = "^[\\u4e00-\\u9fa5a-zA-Z0-9]{4,16}$",
             message = "宠物名称必须在 4-16个字符，支持中英文、数字",
             groups  = {AddPetGroup.class, ModifyPetProfileGroup.class})
    private
    String petName;

    @TableField(value = "pet_species")
    @Pattern(regexp  = "^[\\u4e00-\\u9fa5]{2,10}$",
             message = "宠物品种必须在 2 - 10 个中文字符以内",
             groups  = {AddPetGroup.class, })
    private
    String petSpecies;

    @TableField(value = "pet_introduction")
    @Length(max = 255,
            message = "宠物介绍不能超过 255 个字符",
            groups = {AddPetGroup.class, ModifyPetProfileGroup.class})
    private
    String petIntroduction;

    @TableField(value = "is_pet_overt")
    @NotNull(message = "请选择宠物是否公开!",
             groups = {AddPetGroup.class, ModifyPetProfileGroup.class})
    private
    Boolean overt;

        public interface AddPetGroup {

        }
        public interface ModifyPetProfileGroup {

        }



}
