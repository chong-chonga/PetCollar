package com.example.response.data.user;

import com.example.pojo.User;
import com.example.response.ReactiveResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Lexin Huang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@ToString
public class UserSettingsRequestData extends ReactiveResponse.ApiData {
    private User user;


}
