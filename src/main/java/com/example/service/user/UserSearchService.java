package com.example.service.user;

import com.example.response.ReactiveResponse;
import com.example.response.data.user.UserSearchRequestData;

/**
 * @author Lexin Huang
 */
public interface UserSearchService {
    ReactiveResponse<UserSearchRequestData> getSearchUsersResponse(String name);

    ReactiveResponse<UserSearchRequestData> getUserProfileResponse(String username);
}
