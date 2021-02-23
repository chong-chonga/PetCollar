package com.example;

import com.example.request.RequestInfoFormat;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

/**
 * 修改 AccountRequestInfoFormat类时, 应当能通过当前测试类的所有测试
 * @author Lexin Huang
 * @since 2.0
 */
@Slf4j
@SpringBootTest
public class RequestInfoFormatTest {
    @Test
    public void testEmailAddress(){
//        false
        Assert.isTrue(!RequestInfoFormat.isEmailAddressFormatCorrect("123"),
                "123邮箱格式不可能支持");
        Assert.isTrue(!RequestInfoFormat.isEmailAddressFormatCorrect("io..@qq.com"),
                "io.@qq.com邮箱格式不可能支持");
        Assert.isTrue(!RequestInfoFormat.isEmailAddressFormatCorrect("1612682622@123.com"),
                "@123.com结尾的邮箱格式不可能支持");
        Assert.isTrue(!RequestInfoFormat.isEmailAddressFormatCorrect("hlx_1612682622@@163.com"),
                "hlx_1612682622@@163.com邮箱必定不支持");
        Assert.isTrue(!RequestInfoFormat.isEmailAddressFormatCorrect("wang123@..com"),
                "wang123@..com邮箱必定不支持");

//      true

        Assert.isTrue(RequestInfoFormat.isEmailAddressFormatCorrect("123@qq.com"),
                "123@qq.com邮箱格式必定支持");
        Assert.isTrue(RequestInfoFormat.isEmailAddressFormatCorrect("io@qq.com"),
                "io@qq.com邮箱必定支持");
        Assert.isTrue(RequestInfoFormat.isEmailAddressFormatCorrect("1612682622@qq.com"),
                "1612682622@qq.com 邮箱必定支持");
        Assert.isTrue(RequestInfoFormat.isEmailAddressFormatCorrect("hlx_1612682622@163.com"),
                "hlx_1612682622@163.com邮箱必定支持");
        Assert.isTrue(RequestInfoFormat.isEmailAddressFormatCorrect("wang123@126.com"),
                "wang123@126.com邮箱必定支持");
    }

    @Test
    public void testUsername(){
//        false
        Assert.isTrue(!RequestInfoFormat.isNameFormatCorrect("Ws6"),
                "Ws6"+"用户名格式不可能支持");
        Assert.isTrue(!RequestInfoFormat.isNameFormatCorrect("Qu8["),
                "Qu8{"+"用户名格式不可能支持");
        Assert.isTrue(!RequestInfoFormat.isNameFormatCorrect("去2s你的*"),
                "去2s你的*"+"用户名格式不可能支持");
        Assert.isTrue(!RequestInfoFormat.isNameFormatCorrect("0123456789abcdEFG"),
                "0123456789abcdEFG"+"用户名格式不可能支持");

//      true
        Assert.isTrue(RequestInfoFormat.isNameFormatCorrect("Ws6我"),
                "Ws6我"+"用户名格式不可能支持");
        Assert.isTrue(RequestInfoFormat.isNameFormatCorrect("Qu8o小"),
                "Qu8o小"+"用户名格式不可能支持");
        Assert.isTrue(RequestInfoFormat.isNameFormatCorrect("去2L你的SSS"),
                "去2L你的SSS"+"用户名格式不可能支持");
        Assert.isTrue(RequestInfoFormat.isNameFormatCorrect("0123456789abcdEF"),
                "0123456789abcdEF"+"用户名格式不可能支持");
    }

    @Test
    public void testPassword(){
//        false
        Assert.isTrue(!RequestInfoFormat.isPasswordFormatCorrect("1this"),
                "1this"+"密码格式不可能支持");
        Assert.isTrue(!RequestInfoFormat.isPasswordFormatCorrect("0123456789abcdEFGHI"),
                "0123456789abcdEFGHI"+"密码格式不可能支持");
        Assert.isTrue(!RequestInfoFormat.isPasswordFormatCorrect("Your123我的"),
                "Your123我的"+"密码格式不可能支持");
        Assert.isTrue(!RequestInfoFormat.isPasswordFormatCorrect("#@!45我["),
                "#@!45我["+"密码格式不可能支持");

//      true
        Assert.isTrue(RequestInfoFormat.isPasswordFormatCorrect("1thisI"),
                "1thisI"+"密码格式必定支持");
        Assert.isTrue(RequestInfoFormat.isPasswordFormatCorrect("0123456789abcdEFGH"),
                "0123456789abcdEFGH"+"密码格式必定支持");
        Assert.isTrue(RequestInfoFormat.isPasswordFormatCorrect("Your123"),
                "Your123"+"密码格式必定支持");
        Assert.isTrue(RequestInfoFormat.isPasswordFormatCorrect("~!@#$%^&*()+=|{}"),
                "~!@#$%^&*()+=|{}"+"密码格式必定支持");
        Assert.isTrue(RequestInfoFormat.isPasswordFormatCorrect("':;,\\.<>/-_?"),
                "':;,\\.<>/-_?"+"密码格式必定支持");
    }

}
