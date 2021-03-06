package com.example.controller.pet;

import com.example.controller.AbstractParamsCheckController;
import com.example.exception.PetNameFormatException;
import com.example.exception.PetSpeciesFormatException;
import com.example.exception.TooLongTextException;
import com.example.pojo.Pet;
import com.example.exception.UsernameFormatException;
import org.apache.logging.log4j.util.Strings;

import java.util.Objects;

/**
 * @author Lexin Huang
 */
public class PetParamsCheckController extends AbstractParamsCheckController<Pet> {

    private static final int INTRODUCTION_MAX_LEN = 255;

    protected void petNameFormatCheck(String petName) throws PetNameFormatException {
        if (!petNameFormatCorrect(petName)){
            throw new UsernameFormatException("名称: " + petName + " 的宠物名称格式有误!");
        }
    }

    protected void petIntroductionFormatCheck(String petIntroduction) throws TooLongTextException {
        if (!Objects.isNull(petIntroduction) && INTRODUCTION_MAX_LEN < petIntroduction.length()) {
            throw new UsernameFormatException("宠物介绍不能超过 " + INTRODUCTION_MAX_LEN + " 个字符!");
        }
    }

    /**
     * 正则表达式验证昵称
     */
    private boolean petNameFormatCorrect(String testName) {
        if (null == testName) {
            return false;
        }
        // 昵称格式：限4-16个字符，支持中英文、数字
        String regStr = "^[\\u4e00-\\u9fa5a-zA-Z0-9]{4,16}$";
        return testName.matches(regStr);
    }

    @Override
    protected void entityFormatCheck(Pet objToCheck) {
        if (Objects.isNull(objToCheck)) {
            throw new NullPointerException("校验参数不能为空!");
        }
        petNameFormatCheck(objToCheck.getPetName());
        petSpeciesFormatCheck(objToCheck.getPetSpecies());
        petIntroductionFormatCheck(objToCheck.getPetIntroduction());
        if (Objects.isNull(objToCheck)) {

        }
    }

    private void petSpeciesFormatCheck(String petSpecies) {
        if (Strings.isEmpty(petSpecies)) {
            throw new PetSpeciesFormatException("宠物品种不能为空!");
        }
    }
}
