package com.example.response;

import lombok.ToString;

import static com.example.response.ReactiveResponse.StatusCode.*;


/**
 * @author Lexin Huang
 * @since 2.0
 * 响应式消息的基类, 对于响应状态 code 和 msg 做出相应的调整
 * 对于 code 的设置, 将会自动匹配对应的 msg 且不允许手动设置 msg 的值
 * 只保留 data 的自定义能力, 且限制类型为 ReactiveData 如{@link AccountVerificationRequestData}
 * 将data设置为保护类型, 使得子类可以直接赋值, 而不允许外界调用
 */
@ToString
public class ReactiveResponse {

    private Integer code;
    private String msg;
    protected Object data;

    public ReactiveResponse() {
        setStatus(Server_ERROR);
        this.data = new Object();
    }

    public static class StatusCode {

        public static final int CORRECT = 200;

        public static final int USERNAME_FORMAT_WRONG = 411;

        public static final int PASSWORD_FORMAT_WRONG = 412;

        public static final int MISMATCH = 413;

        public static final int USERNAME_HAS_REGISTERED = 414;

        public static final int USER_NOT_EXISTS = 415;

        public static final int TOKEN_NOT_EXISTS = 416;

        public static final int EMAIL_ADDRESS_NOT_SUPPORTED = 417;

        public static final int PASSWORD_WRONG = 418;

        public static final int VERIFICATION_CODE_HAS_EXPIRED = 419;

        public static final int Server_ERROR = 500;

    }
    public Object getData(){
        return this.data;
    }


        /**
         * @param statusCode 设置的状态码
         * @return 状态码对应的消息
         */
        protected static String getStatusMsg(Integer statusCode){
            switch (statusCode) {
                case CORRECT:
                    return "请求成功!";
                case USERNAME_FORMAT_WRONG:
                    return "用户名格式错误!";
                case PASSWORD_FORMAT_WRONG:
                    return "密码格式错误!";
                case MISMATCH:
                    return "用户名或密码错误!";
                case USERNAME_HAS_REGISTERED:
                    return "用户名已被注册!";
                case USER_NOT_EXISTS:
                    return "用户不存在!";
                case TOKEN_NOT_EXISTS:
                    return "登录已过期!";
                case EMAIL_ADDRESS_NOT_SUPPORTED:
                    return "不支持的邮箱类型!";
                case PASSWORD_WRONG:
                    return "密码错误!";
                case VERIFICATION_CODE_HAS_EXPIRED:
                    return "邮箱验证码已过期!";
                case Server_ERROR:
                    return "服务器出错了!";
                default:
                    return "NONE";
            }
        }


    public void setContent(Integer code, ReactiveData reactiveData){
        setStatus(code);
        this.data = reactiveData;
    }

    public void setContent(Integer code, String msg, ReactiveData reactiveData){
        this.code = code;
        this.msg = msg;
        this.data = reactiveData;
    }

    /**
     * 这个方法会同时设置 状态码 code 和 状态信息 msg
     * @param code 响应状态码
     */
    private void setStatus(Integer code) {
        this.code = code;
        this.msg = getStatusMsg(code);
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
