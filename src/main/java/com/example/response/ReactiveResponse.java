package com.example.response;

import lombok.ToString;

import static com.example.response.ReactiveResponse.Status.*;


/**
 * @author Lexin Huang
 * @since 2.0
 * 响应式消息的基类, 对于响应状态 code 和 msg 做出相应的调整
 * 对于 code 的设置, 将会自动匹配对应的 msg 且不允许手动设置 msg 的值
 * 只保留 data 的自定义能力, 且限制类型为 ReactiveData 如{@link UserLoginRegisterRequestData}
 * 将data设置为保护类型, 使得子类可以直接赋值, 而不允许外界调用
 */
@ToString
public class ReactiveResponse {

    private Integer code;
    private String msg;
    protected Object data;

    public ReactiveResponse() {
        setStatus(SERVER_ERROR);
        this.data = new Object();
    }

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
        INVALID_TOKEN(120001, "令牌无效, 请重新登录!"),
        UNAUTHORIZED(120002, "您没有访问这个资源的权限!"),
        INVALID_ITEM(120003, "您还没有这个物品! 请获取后再进行尝试!"),


//      ---PARAM_ERROR---
//        PET_MODULE
        PET_NAME_FORMAT_WRONG(400001, "宠物名称必须在 4-16 个字符内, 由中英文,数字组成!"),
        PET_NAME_NOT_AVAILABLE(400002, "该宠物名称已经被用了, 换一个试试~"),
        PET_INTRODUCTION_TOO_LONG(400003, "宠物介绍不能超过 255 个字符!"),

//        USER_MODULE
        USER_PASSWORD_WRONG(410001, "密码错误! 请检查是否为原密码!"),


//       ---SYS_ERROR---
        MAIL_SERVICE_NOT_AVAILABLE(900001, "邮箱服务暂时不可用!"),
        SERVER_ERROR(999999,  "服务器出错了! 请联系管理员以获得帮助");


        private final int value;

        private final String msg;

        Status(int value, String msg) {
            this.value = value;
            this.msg = msg;
        }

        public int value(){
            return this.value;
        }
    }


    public Object getData(){
        return this.data;
    }


    public void setContent(Status status, ReactiveData reactiveData){
        setStatus(status);
        this.data = reactiveData;
    }

    public void setContent(Status status, String msg, ReactiveData reactiveData){
        this.code = status.value;
        this.msg = msg;
        this.data = reactiveData;
    }

    /**
     * 这个方法会同时设置 状态码 code 和 状态信息 msg
     * @param status 响应状态码
     */
    private void setStatus(Status status) {
        this.code = status.value;
        this.msg = status.msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
