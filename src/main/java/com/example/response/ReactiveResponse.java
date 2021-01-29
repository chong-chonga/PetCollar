package com.example.response;

import lombok.ToString;

import static com.example.response.ReactiveResponse.StatusCode.*;


/**
 * @author Lexin Huang
 * @since 2.0
 * 响应式消息的基类, 对于响应状态 code 和 msg 做出相应的调整
 * 对于 code 的设置, 将会自动匹配对应的 msg 且不允许手动设置 msg 的值
 * 只保留 data 的自定义能力, 且限制类型为 ReactiveData 如{@link LoginRegisterData}
 * 将data设置为保护类型, 使得子类可以直接赋值, 而不允许外界调用
 */
@ToString
public class ReactiveResponse {

    private Integer code;
    private String msg;
    protected Object data;

    public ReactiveResponse() {
        this.data = new Object();
    }

    public static class StatusCode {
        public static final int CORRECT = 200;

        public static final int USERNAME_NOT_REGISTERED = 101;

        public static final int MISMATCH = 102;

        @Deprecated
        public static final int USERNAME_HAS_REGISTERED = 103;

        public static final int USERNAME_FORMAT_WRONG = 104;

        public static final int PASSWORD_FORMAT_WRONG = 105;

        public static final int NOT_THE_ORIGINAL_PASSWORD = 106;

        public static final int TOKEN_NOT_EXISTS = 151;

        public static final int UNSUPPORTED_MAILBOX_TYPE = 410;

        public static final int Server_ERROR = 500;

        public static final int EMAIL_FUNCTION_NOT_AVAILABLE = 501;

        /**
         * @param statusCode 设置的状态码
         * @return 状态码对应的消息
         */
        protected static String getStatusMsg(Integer statusCode){
            switch (statusCode) {
                case CORRECT:
                    return "请求成功!";
                case USERNAME_NOT_REGISTERED:
                    return "该用户名尚未注册!";
                case MISMATCH:
                    return "用户名或密码错误!";
                case USERNAME_HAS_REGISTERED:
                    return "用户名已被注册!";
                case USERNAME_FORMAT_WRONG:
                    return "用户名格式错误!";
                case PASSWORD_FORMAT_WRONG:
                    return "密码格式错误!";
                case Server_ERROR:
                    return "服务器控制器错误!";
                case NOT_THE_ORIGINAL_PASSWORD:
                    return "输入密码与原密码不一致";
                case TOKEN_NOT_EXISTS:
                    return "无效令牌!";
                case UNSUPPORTED_MAILBOX_TYPE:
                    return "不支持的邮箱验证类型";
                case EMAIL_FUNCTION_NOT_AVAILABLE:
                    return "邮箱功能暂时不可用!";
                default:
                    return "NONE";
            }
        }
    }

    public String getMsg() {
        return msg;
    }

    public Integer getCode() {
        return code;
    }

    public Object getData() {
        return data;
    }

    public void setContent(Integer code, ReactiveData<?> reactiveData){
        setStatus(code);
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

}
