package com.example.util;


import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.UUID;

/**
 * @author Lexin Huang
 */
public class StringUtil {

    private static final String CHARS =
            "123456789abcdefghijklmnopqrstxyzABCDEFGHIJKLMNOPQRSTXYZ";

    private static final SimpleDateFormat NAME_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");

    private static final FieldPosition FIELD_POSITION = new FieldPosition(SimpleDateFormat.TIMEZONE_FIELD);


    public static String getCodeString(int digits) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < digits; i++) {
            stringBuilder.append(CHARS.charAt(RandomNumberUtil.generateNum(CHARS.length())));
        }
        return stringBuilder.toString();
    }

    public static String getUniqueName() {
        return UUID.randomUUID().toString().replace("-","");
    }

}
