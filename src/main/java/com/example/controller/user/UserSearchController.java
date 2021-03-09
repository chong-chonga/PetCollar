package com.example.controller.user;

import com.example.response.ReactiveResponse;
import com.example.response.data.user.UserSearchRequestData;
import com.example.service.user.UserSearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

/**
 * @author Lexin Huang
 * 此控制器的所有方法, 不会进行参数的校验
 */
@Api(tags = "用户查询模块")
@RestController

public class UserSearchController {

    private final UserSearchService userSearchService;

    public UserSearchController(UserSearchService userSearchService) {
        this.userSearchService = userSearchService;
    }


    @GetMapping("/users")
    @ApiOperation(value = "查询相似昵称的用户")
    @ApiImplicitParam(value = "要查询的用户名", dataTypeClass = String.class, paramType = "query")
    public ReactiveResponse<UserSearchRequestData> searchUsersByUsername(@RequestParam("keyword") String username){
        return userSearchService.getSearchUsersResponse(username);
    }

    @GetMapping("/users/profile")
    @ApiOperation(value = "获取指定昵称用户的个人信息")
    @ApiImplicitParam(value = "用户令牌", dataTypeClass = String.class, paramType = "head")
    public ReactiveResponse<UserSearchRequestData> getUserProfile(@RequestHeader(value = "token", required = false) String token){
        return userSearchService.getUserProfileResponse(token);
    }

}
