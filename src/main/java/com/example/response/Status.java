package com.example.response;

/**
 * @author Lexin Huang
 */
public enum Status {
    //      ---SUCCESS---
    SUCCESS(200, "请求成功"),
    //      ---Not Found---
    RESOURCE_NOT_FOUND(404, "找不到指定资源"),


    //      ---WEB_ERROR---
    USERNAME_NOT_AVAILABLE(100001, "该名称已被使用过了, 请再挑个名称试试!"),
    MISMATCH(100002, "用户名或密码错误!"),
    VERIFICATION_CODE_WRONG(100003, "验证码错误!"),


    //      ---SERVICE_ERROR---
    INVALID_TOKEN(200001, "令牌无效, 请重新登录!"),
    UNAUTHORIZED(200002, "您没有访问这个资源的权限!"),

    //        USER_MODULE
    USER_PASSWORD_WRONG(300001, "密码错误! 请检查是否为原密码!"),

    //      ---PARAM_ERROR---
//        PET_MODULE
    PET_NAME_NOT_AVAILABLE(400001, "该宠物名称已经被用了, 换一个试试~"),


    //       ---SYS_ERROR---
    MAIL_SERVICE_NOT_AVAILABLE(900001, "邮箱服务暂时不可用!"),
    AVATAR_SERVICE_NOT_AVAILABLE(900002, "头像服务暂时不可用!"),
    SERVER_ERROR(999999, "服务器出错了! 请联系管理员以获得帮助");


    final int value;

    final String msg;

    Status(int value, String msg) {
        this.value = value;
        this.msg = msg;
    }

}
