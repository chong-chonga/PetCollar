package com.example.response;

/**
 * @author Lexin Huang
 */
public enum Status {
    //      ---SUCCESS---
    SUCCESS(200, "请求成功"),


    //      ---WEB_ERROR---
    USERNAME_FORMAT_WRONG(100001, "用户名格式有误! 用户名限4-16个字符，支持中英文、数字"),
    PASSWORD_FORMAT_WRONG(100002, "密码格式有误! 密码限6-18 位，支持字母、数字,特殊字符"),
    EMAIL_FORMAT_WRONG(100003, "邮箱格式有误! 限QQ邮箱和网易邮箱, 且邮箱长度不得超过31个字符!"),
    USERNAME_NOT_AVAILABLE(100004, "该名称已被使用过了, 请再挑个名称试试!"),
    MISMATCH(100005, "用户名或密码错误!"),
    VERIFICATION_CODE_WRONG(100006, "验证码错误!"),


    //      ---SERVICE_ERROR---
    INVALID_TOKEN(200001, "令牌无效, 请重新登录!"),
    UNAUTHORIZED(200002, "您没有访问这个资源的权限!"),
    INVALID_ITEM(200003, "您还没有这个物品! 请获取后再进行尝试!"),

    //        USER_MODULE
    USER_PASSWORD_WRONG(300001, "密码错误! 请检查是否为原密码!"),
    TEXT_FORMAT_WRONG(300002, "个人介绍必须在 255 个字符内!"),

    //      ---PARAM_ERROR---
//        PET_MODULE
    PET_NAME_FORMAT_WRONG(400001, "宠物名称必须在 4-16 个字符内, 由中英文,数字组成!"),
    PET_NAME_NOT_AVAILABLE(400002, "该宠物名称已经被用了, 换一个试试~"),
    PET_INTRODUCTION_TOO_LONG(400003, "宠物介绍不能超过 255 个字符!"),


    //       ---SYS_ERROR---
    MAIL_SERVICE_NOT_AVAILABLE(900001, "邮箱服务暂时不可用!"),
    RESOURCE_NOT_FOUND(404, "找不到指定资源"),
    SERVER_ERROR(999999, "服务器出错了! 请联系管理员以获得帮助");


    final int value;

    final String msg;

    Status(int value, String msg) {
        this.value = value;
        this.msg = msg;
    }

}
