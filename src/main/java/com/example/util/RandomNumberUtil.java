package com.example.util;

import java.util.Random;

/**
 * @author Lexin Huang
 */
public class RandomNumberUtil {

    private static final Random random = new Random();

    public static int generateNum(int bound) {
        return random.nextInt(bound);
    }

}
