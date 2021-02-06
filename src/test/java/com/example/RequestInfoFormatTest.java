package com.example;

import com.example.util.RequestInfoFormat;
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
        Assert.isTrue(!RequestInfoFormat.emailAddressFormatCorrect("123"),
                "123邮箱格式不可能支持");
        Assert.isTrue(!RequestInfoFormat.emailAddressFormatCorrect("io..@qq.com"),
                "io.@qq.com邮箱格式不可能支持");
        Assert.isTrue(!RequestInfoFormat.emailAddressFormatCorrect("1612682622@123.com"),
                "@123.com结尾的邮箱格式不可能支持");
        Assert.isTrue(!RequestInfoFormat.emailAddressFormatCorrect("hlx_1612682622@@163.com"),
                "hlx_1612682622@@163.com邮箱必定不支持");
        Assert.isTrue(!RequestInfoFormat.emailAddressFormatCorrect("wang123@..com"),
                "wang123@..com邮箱必定不支持");

//      true

        Assert.isTrue(RequestInfoFormat.emailAddressFormatCorrect("123@qq.com"),
                "123@qq.com邮箱格式必定支持");
        Assert.isTrue(RequestInfoFormat.emailAddressFormatCorrect("io@qq.com"),
                "io@qq.com邮箱必定支持");
        Assert.isTrue(RequestInfoFormat.emailAddressFormatCorrect("1612682622@qq.com"),
                "1612682622@qq.com 邮箱必定支持");
        Assert.isTrue(RequestInfoFormat.emailAddressFormatCorrect("hlx_1612682622@163.com"),
                "hlx_1612682622@163.com邮箱必定支持");
        Assert.isTrue(RequestInfoFormat.emailAddressFormatCorrect("wang123@126.com"),
                "wang123@126.com邮箱必定支持");
    }

    @Test
    public void testUsername(){
        //        false
        Assert.isTrue(!RequestInfoFormat.nameFormatCorrect("Ws6"),
                "Ws6"+"用户名格式不可能支持");
        Assert.isTrue(!RequestInfoFormat.nameFormatCorrect("Qu8["),
                "Qu8{"+"用户名格式不可能支持");
        Assert.isTrue(!RequestInfoFormat.nameFormatCorrect("去2s你的*"),
                "去2s你的*"+"用户名格式不可能支持");
        Assert.isTrue(!RequestInfoFormat.nameFormatCorrect("0123456789abcdEFG"),
                "0123456789abcdEFG"+"用户名格式不可能支持");

//      true
        Assert.isTrue(RequestInfoFormat.nameFormatCorrect("Ws6我"),
                "Ws6我"+"用户名格式不可能支持");
        Assert.isTrue(RequestInfoFormat.nameFormatCorrect("Qu8o小"),
                "Qu8o小"+"用户名格式不可能支持");
        Assert.isTrue(RequestInfoFormat.nameFormatCorrect("去2L你的SSS"),
                "去2L你的SSS"+"用户名格式不可能支持");
        Assert.isTrue(RequestInfoFormat.nameFormatCorrect("0123456789abcdEF"),
                "0123456789abcdEF"+"用户名格式不可能支持");
    }

    @Test
    public void testPassword(){
        //        false
        Assert.isTrue(!RequestInfoFormat.passwordFormatCorrect("1this"),
                "1this"+"密码格式不可能支持");
        Assert.isTrue(!RequestInfoFormat.passwordFormatCorrect("0123456789abcdEFGHI"),
                "0123456789abcdEFGHI"+"密码格式不可能支持");
        Assert.isTrue(!RequestInfoFormat.passwordFormatCorrect("Your123我的"),
                "Your123我的"+"密码格式不可能支持");
        Assert.isTrue(!RequestInfoFormat.passwordFormatCorrect("#@!45我["),
                "#@!45我["+"密码格式不可能支持");

//      true
        Assert.isTrue(RequestInfoFormat.passwordFormatCorrect("1thisI"),
                "1thisI"+"密码格式必定支持");
        Assert.isTrue(RequestInfoFormat.passwordFormatCorrect("0123456789abcdEFGH"),
                "0123456789abcdEFGH"+"密码格式必定支持");
        Assert.isTrue(RequestInfoFormat.passwordFormatCorrect("Your123"),
                "Your123"+"密码格式必定支持");
        Assert.isTrue(RequestInfoFormat.passwordFormatCorrect("That*@!#'\\"),
                "That*@!#'\\"+"密码格式必定支持");
    }

}
