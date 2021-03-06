package com.example.response;

import lombok.Data;
import lombok.ToString;

import static com.example.response.Status.SERVER_ERROR;
import static com.example.response.Status.SUCCESS;


/**
 * @author Lexin Huang
 * @since 2.0
 */
@Data
@ToString
public class ReactiveResponse<T extends ReactiveResponse.ApiData> {

    private Integer code;
    private String msg;
    private T data;


    /**
     * 用于指定表示 data 指定类, 作用相当于标识, 防止 Object 直接通过赋值器传入
     */
    public static class ApiData {

    }

    public ReactiveResponse() {
        setStatus(SERVER_ERROR);
    }

    private void setData(T data) {
        this.data = data;
    }

    public void setError(Status status) {
        setStatus(status);
        setData(null);
    }

    public void setFormatError(String msg) {
        this.code = 400;
        this.msg = msg;
        this.data = null;
    }

    public void setSuccess(T data) {
        setStatus(SUCCESS);
        setData(data);
    }

    private void setStatus(Status status) {
        this.code = status.value;
        this.msg = status.msg;
    }

}
