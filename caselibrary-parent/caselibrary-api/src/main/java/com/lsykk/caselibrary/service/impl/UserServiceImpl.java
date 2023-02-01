package com.lsykk.caselibrary.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsykk.caselibrary.dao.mapper.UserMapper;
import com.lsykk.caselibrary.dao.pojo.Case;
import com.lsykk.caselibrary.dao.pojo.User;
import com.lsykk.caselibrary.service.LoginService;
import com.lsykk.caselibrary.service.UserService;
import com.lsykk.caselibrary.vo.ApiResult;
import com.lsykk.caselibrary.vo.ErrorCode;
import com.lsykk.caselibrary.vo.LoginVo;
import com.lsykk.caselibrary.vo.params.PageParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private LoginService loginService;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;


    @Override
    public ApiResult findUserByToken(String token) {
        /**
         * 1. token合法性校验
         *    是否为空，解析是否成功 redis是否存在
         * 2. 如果校验失败 返回错误
         * 3. 如果成功，返回对应的结果 LoginVo
         */
        User user = loginService.checkToken(token);
        if (user == null){
            return ApiResult.fail(ErrorCode.TOKEN_ERROR.getCode(),ErrorCode.TOKEN_ERROR.getMsg());
        }
        LoginVo loginVo = transUserToLoginVo(user);
        return ApiResult.success(loginVo);
    }

    @Override
    public ApiResult getUserList(PageParams pageParams){
        // 分页查询 user数据库表
        Page<User> page = new Page<>(pageParams.getPage(), pageParams.getPageSize());
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        // 按照时间顺序排序
        queryWrapper.orderByDesc(User::getId);
        Page<User> userPage = userMapper.selectPage(page, queryWrapper);
        List<User> userList = userPage.getRecords();
        return ApiResult.success(userList);
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
        user.setStatus(1);
        userMapper.insert(user);
    }

    @Override
    public void updateUser(User user){
        userMapper.updateById(user);
    }

    @Override
    public void deleteUserById(Long id) {
        userMapper.deleteById(id);
    }

    @Override
    public LoginVo transUserToLoginVo(User user){
        LoginVo loginVo = new LoginVo();
        loginVo.setId(user.getId());
        loginVo.setEmail(user.getEmail());
        loginVo.setImage(user.getImage());
        loginVo.setUsername(user.getUsername());
        loginVo.setAuthority(user.getAuthority());
        loginVo.setStatus(user.getStatus());
        return loginVo;
    }
}
