package com.lsykk.caselibrary.service;

import com.lsykk.caselibrary.dao.pojo.User;
import com.lsykk.caselibrary.vo.ApiResult;
import com.lsykk.caselibrary.vo.params.LoginParam;

public interface UserService {

    /**
     * 登录账号
     * @param loginParam
     * @return
     */
    ApiResult login(LoginParam loginParam);
    /**
     * 注册账号
     * @param loginParam
     * @return
     */
    ApiResult register(LoginParam loginParam);

    /**
     * 根据token查找用户
     * @param token
     * @return
     */
    ApiResult findUserByToken(String token);

    /**
     * 根据user_id查找用户
     * @param id
     * @return
     */
    User findUserById(Long id);

    /**
     * 根据邮箱查找用户
     * @param email
     * @return
     */
    User findUserByEmail(String email);

    /**
     * 根据邮箱和密码查找用户
     * @param email
     * @param password
     * @return
     */
    User findUserByEmailAndPassword(String email, String password);

    /**
     * 保存用户（新增或更新）
     * @param user
     * @return
     */
    void saveUser(User user);

    /**
     * 根据user_id删除用户
     */
    void deleteUserById(Long id);
}
