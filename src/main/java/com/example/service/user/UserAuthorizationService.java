package com.example.service.user;

/**
 * @author 悠一木碧
 */

public interface UserAuthorizationService extends TokenCheckService {

    /**
     * 根据用户名删除对应的用户缓存 (包括 token, 以及用户信息的缓存)
     * @param username 用户名
     */
    void removeTokenByUsername(String username);

    /**
     * 根据 k 获取 v (都为 String 类型)
     * @param k 键
     * @return 值(如果存在) 否则 null
     */
    String getStringCache(String k);

    /**
     * 根据 k 移除 v (都为 String 类型)
     * @param k 键
     */
    void removeStringCache(String k);
}
