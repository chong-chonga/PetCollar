package com.example.service;

import com.example.response.ReactiveResponse;

/**
 * @author Lexin Huang
 * @param <T> 参见 {@link ReactiveResponse.ApiData}
 */
public interface ServiceExceptionHandler<T extends ReactiveResponse.ApiData> {
    /**
     * 用于统一处理 Service 层中可能产生的异常的方法, 可以是文件流等 IO 异常, 但不包括格式校验异常
     * @param e 异常
     * @param response 返回参数包装类
     */
    void handle(Exception e, ReactiveResponse<T> response);
}
