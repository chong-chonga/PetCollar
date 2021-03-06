package com.example.response.data.user;

import com.example.pojo.User;
import com.example.response.ReactiveResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * @author Lexin Huang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@ToString
public class UserSearchRequestData extends ReactiveResponse.ApiData {

    private List<User> users;

    private User user;
}
