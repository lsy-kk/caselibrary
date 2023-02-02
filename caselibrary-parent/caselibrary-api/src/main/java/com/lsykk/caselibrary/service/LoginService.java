package com.lsykk.caselibrary.service;

import com.lsykk.caselibrary.dao.pojo.User;
import com.lsykk.caselibrary.vo.ApiResult;
import com.lsykk.caselibrary.vo.params.LoginParam;

public interface LoginService {

    /**
     * 登录账号
     * @param loginParam
     * @return
     */
    ApiResult login(LoginParam loginParam);

    /**
     * 退出登录
     * @param token
     * @return
     */
    ApiResult logout(String token);

    /**
     * 注册账号
     * @param loginParam
     * @return
     */
    ApiResult register(LoginParam loginParam);

    /**
     * 加密密码
     * @param password
     * @return
     */
    String encryptedPassword(String password);

    /**
     * 根据token返回对应User
     * @param token
     * @return
     */
    User checkToken(String token);
}
