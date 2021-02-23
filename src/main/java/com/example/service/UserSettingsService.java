package com.example.service;

import com.example.request.UserSettingsRequest;
import com.example.response.ReactiveResponse;

public interface UserSettingsService {

    ReactiveResponse getUserSettingsResponse(UserSettingsRequest request);
}
