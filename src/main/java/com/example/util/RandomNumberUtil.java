package com.example.util;

import java.util.Random;

/**
 * @author Lexin Huang
 */
public class RandomNumberUtil {

    private static final Random RANDOM = new Random();

    public static int generateNum(int bound) {
        return RANDOM.nextInt(bound);
    }

}
