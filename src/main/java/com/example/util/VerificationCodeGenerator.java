package com.example.util;

import java.util.Random;

/**
 * @author Lexin Huang
 */
public class VerificationCodeGenerator {
    private static final String CODE_STRING =
            "123456789abcdefghijklmnopqrstxyzABCDEFGHIJKLMNOPQRSTXYZ";

    private static final Random random = new Random();


    public static String generate(Integer digits){
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < digits; i++) {
            stringBuilder.append(CODE_STRING.charAt(random.nextInt(CODE_STRING.length())));
        }
        return stringBuilder.toString();
    }
}
