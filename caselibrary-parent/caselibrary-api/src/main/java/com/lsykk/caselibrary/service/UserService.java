package com.lsykk.caselibrary.service;

import com.lsykk.caselibrary.dao.pojo.User;
import com.lsykk.caselibrary.vo.ApiResult;
import com.lsykk.caselibrary.vo.UserVo;
import com.lsykk.caselibrary.vo.params.LoginParam;
import com.lsykk.caselibrary.vo.params.PageParams;

public interface UserService {

    /**
     * 根据token查找用户
     * @param token
     * @return
     */
    ApiResult findUserVoByToken(String token);

    /**
     * 分页获取用户列表
     * @param pageParams
     * @return
     */
    ApiResult getUserList(PageParams pageParams, User user);

    /**
     * 关键字搜索user
     * @param pageParams
     * @param keyWords
     * @return
     */
    ApiResult getSearchList(PageParams pageParams, String keyWords);

    /**
     * 更新userVoRepository的后门接口
     * @return
     */
    ApiResult userVoRepositoryReload();

    /**
     * 更新所有用户的caseNumber字段
     * @return
     */
    // ApiResult updateUserCaseNumber();

    /**
     * 根据user_id查找用户
     * @param id
     * @return
     */
    User findUserById(Long id);

    /**
     * 根据user_id查找用户vo
     * @param id
     * @return
     */
    UserVo findUserVoById(Long id);

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
     * 根据id更新用户密码
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
     * 更新用户
     * @param user
     * @return
     */
    void update(User user);

    /**
     * 根据user_id删除用户
     * @param id
     * @return
     */
    void deleteUserById(Long id);

}
