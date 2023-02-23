package com.lsykk.caselibrary.service.impl;

import com.alibaba.fastjson.JSON;
import com.lsykk.caselibrary.dao.pojo.User;
import com.lsykk.caselibrary.service.LoginService;
import com.lsykk.caselibrary.service.UserService;
import com.lsykk.caselibrary.utils.JWTUtils;
import com.lsykk.caselibrary.vo.ApiResult;
import com.lsykk.caselibrary.vo.ErrorCode;
import com.lsykk.caselibrary.vo.params.LoginParam;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class LoginServiceImpl implements LoginService {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    // 加密盐
    private static final String slat = "kkysl!?226";

    @Override
    public ApiResult login(LoginParam loginParam) {
        /* 检查邮箱与密码是否为空 */
        if (StringUtils.isBlank(loginParam.getEmail()) || StringUtils.isBlank(loginParam.getPassword())){
            return ApiResult.fail(ErrorCode.PARAMS_ERROR.getCode(),ErrorCode.PARAMS_ERROR.getMsg());
        }
        /* 加密密码 */
        String password = encryptedPassword(loginParam.getPassword());
        User user = userService.findUserByEmailAndPassword(loginParam.getEmail(), password);
        if (user == null){
            return ApiResult.fail(ErrorCode.ACCOUNT_PWD_NOT_EXIST.getCode(),ErrorCode.ACCOUNT_PWD_NOT_EXIST.getMsg());
        }
        /* 生成token，redis缓存备用*/
        String token = JWTUtils.createToken(user.getId());
        redisTemplate.opsForValue().set("TOKEN_"+token, JSON.toJSONString(user),1, TimeUnit.DAYS);
        return ApiResult.success(token);
    }

    @Override
    public String encryptedPassword(String password){
        return DigestUtils.md5Hex(password + slat);
    }

    @Override
    public ApiResult logout(String token){
        redisTemplate.delete("TOKEN_" + token);
        return ApiResult.success();
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
        if (userService.findUserByEmail(loginParam.getEmail()) != null){
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
        userService.saveUser(user);
        /* 生成token，redis缓存备用*/
        String token = JWTUtils.createToken(user.getId());
        redisTemplate.opsForValue().set("TOKEN_"+token, JSON.toJSONString(user),1, TimeUnit.DAYS);
        return ApiResult.success(token);
    }

    @Override
    public User checkToken(String token){
        if (StringUtils.isBlank(token)){
            return null;
        }
        Map<String, Object> stringObjectMap = JWTUtils.checkToken(token);
        if (stringObjectMap == null){
            return null;
        }
        /* 根据token，获取redis中缓存的user信息*/
        String userJson = redisTemplate.opsForValue().get("TOKEN_" + token);
        if (StringUtils.isBlank(userJson)){
            return null;
        }
        return JSON.parseObject(userJson, User.class);
    }
}