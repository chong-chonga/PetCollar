package com.example.service.user;

import com.example.service.TokenCheckService;

/**
 * @author 悠一木碧
 */

public interface UserAuthorizationService extends TokenCheckService {

    void removeTokenByUsername(String username);

    String getStringCache(String k);

    void removeStringCache(String k);
}
