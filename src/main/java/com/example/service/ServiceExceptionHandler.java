package com.example.service;

import com.example.response.ReactiveResponse;

public interface ServiceExceptionHandler<T extends ReactiveResponse.ApiData> {
    void handle(Exception e, ReactiveResponse<T> response);
}
