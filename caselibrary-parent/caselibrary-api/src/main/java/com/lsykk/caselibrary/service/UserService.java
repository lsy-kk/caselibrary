package com.lsykk.caselibrary.service;

import com.lsykk.caselibrary.dao.pojo.User;
import com.lsykk.caselibrary.vo.ApiResult;
import com.lsykk.caselibrary.vo.LoginVo;
import com.lsykk.caselibrary.vo.params.LoginParam;
import com.lsykk.caselibrary.vo.params.PageParams;

public interface UserService {

    /**
     * 根据token查找用户
     * @param token
     * @return
     */
    ApiResult findUserByToken(String token);

    /**
     * 分页获取用户列表
     * @param pageParams
     * @return
     */
    ApiResult getUserList(PageParams pageParams, User user);

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
     * 保存用户
     * @param user
     * @return
     */
    void saveUser(User user);

    /**
     * 新增用户
     * @param user
     * @return
     */
    ApiResult insertUser(User user);

    /**
     * 根据id更新用户密码（也可用于更新状态、权限）
     * @param user
     * @return
     */
    ApiResult updatePassword(User user);

    /**
     * 根据user_id更新用户信息（用户名、头像、状态、权限、邮箱）
     * 该接口不能用于更新密码
     * @param user
     * @return
     */
    ApiResult updateUser(User user);

    /**
     * 根据user_id删除用户
     * @param id
     * @return
     */
    void deleteUserById(Long id);

    LoginVo transUserToLoginVo(User user);
}
