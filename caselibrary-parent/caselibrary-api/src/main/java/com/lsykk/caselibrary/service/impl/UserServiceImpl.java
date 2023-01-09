package com.lsykk.caselibrary.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lsykk.caselibrary.dao.mapper.UserMapper;
import com.lsykk.caselibrary.dao.pojo.User;
import com.lsykk.caselibrary.service.UserService;
import com.lsykk.caselibrary.utils.JWTUtils;
import com.lsykk.caselibrary.vo.ApiResult;
import com.lsykk.caselibrary.vo.ErrorCode;
import com.lsykk.caselibrary.vo.params.LoginParam;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    private static final String slat = "kkysl!?226";

    @Override
    public ApiResult login(LoginParam loginParam) {
        /* 检查邮箱与密码是否为空 */
        if (StringUtils.isBlank(loginParam.getEmail()) || StringUtils.isBlank(loginParam.getPassword())){
            return ApiResult.fail(ErrorCode.PARAMS_ERROR.getCode(),ErrorCode.PARAMS_ERROR.getMsg());
        }
        /* 加密密码 */
        String password = DigestUtils.md5Hex(loginParam.getPassword() + slat);
        User user = findUserByEmailAndPassword(loginParam.getEmail(),loginParam.getPassword());
        if (user == null){
            return ApiResult.fail(ErrorCode.ACCOUNT_PWD_NOT_EXIST.getCode(),ErrorCode.ACCOUNT_PWD_NOT_EXIST.getMsg());
        }
        /* 生成token，redis缓存备用*/
        String token = JWTUtils.createToken(user.getId());
        redisTemplate.opsForValue().set("TOKEN_"+token, JSON.toJSONString(user),1, TimeUnit.DAYS);
        return ApiResult.success(token);
    }

    @Override
    public ApiResult register(LoginParam loginParam) {
        /* 检查参数是否合法 */
        if (StringUtils.isBlank(loginParam.getEmail())
                || StringUtils.isBlank(loginParam.getUsername())
                || StringUtils.isBlank(loginParam.getPassword())){
            return ApiResult.fail(ErrorCode.PARAMS_ERROR.getCode(), ErrorCode.PARAMS_ERROR.getMsg());
        }
        /* 检查邮箱是否唯一 */
        if (findUserByEmail(loginParam.getEmail()) != null){
            return ApiResult.fail(ErrorCode.ACCOUNT_EXIST.getCode(),"该邮箱已经被注册了");
        }
        User user = new User();
        user.setEmail(loginParam.getEmail());
        user.setImage("");
        user.setUsername(loginParam.getUsername());
        /* 密码需要加密保存 */
        user.setPassword(DigestUtils.md5Hex(loginParam.getPassword() + slat));
        /* 默认注册权限为2级 */
        user.setAuthority(2);
        user.setStatus(1);
        saveUser(user);
        /* 生成token，redis缓存备用*/
        String token = JWTUtils.createToken(user.getId());
        redisTemplate.opsForValue().set("TOKEN_"+token, JSON.toJSONString(user),1, TimeUnit.DAYS);
        return ApiResult.success(token);
    }

    @Override
    public ApiResult findUserByToken(String token) {
        /**
         * 1. token合法性校验
         *    是否为空，解析是否成功 redis是否存在
         * 2. 如果校验失败 返回错误
         * 3. 如果成功，返回对应的结果 LoginUserVo
         */
        User user = loginService.checkToken(token);
        if (user == null){
            return Result.fail(ErrorCode.TOKEN_ERROR.getCode(),ErrorCode.TOKEN_ERROR.getMsg());
        }
        LoginUserVo loginUserVo = new LoginUserVo();
        loginUserVo.setId(String.valueOf(sysUser.getId()));
        loginUserVo.setNickname(sysUser.getNickname());
        loginUserVo.setAvatar(sysUser.getAvatar());
        loginUserVo.setAccount(sysUser.getAccount());
        return ApiResult.success(loginUserVo);
        return ApiResult.success();
    }

    @Override
    public User findUserById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public User findUserByEmail(String email) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail, email);
        queryWrapper.last("limit 1");
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public User findUserByEmailAndPassword(String email, String password) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail, email);
        queryWrapper.eq(User::getPassword, password);
        queryWrapper.last("limit 1");
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public void saveUser(User user) {
        userMapper.insert(user);
    }

    @Override
    public void deleteUserById(Long id) {
        userMapper.deleteById(id);
    }
}
