package com.example.util;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Lexin Huang
 */
@SpringBootTest
public class StringUtilTest {

    @Test
    public void testGetUniquePictureName(){
        for(int i = 0; i < 1000; i++){
            System.out.println(StringUtil.getUniqueName());
        }
    }

}
