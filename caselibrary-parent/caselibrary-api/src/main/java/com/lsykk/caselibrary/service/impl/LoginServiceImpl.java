package com.lsykk.caselibrary.service.impl;

import com.alibaba.fastjson.JSON;
import com.lsykk.caselibrary.dao.pojo.User;
import com.lsykk.caselibrary.service.LoginService;
import com.lsykk.caselibrary.service.UserService;
import com.lsykk.caselibrary.utils.DateUtils;
import com.lsykk.caselibrary.utils.JWTUtils;
import com.lsykk.caselibrary.utils.MailUtils;
import com.lsykk.caselibrary.vo.ApiResult;
import com.lsykk.caselibrary.vo.ErrorCode;
import com.lsykk.caselibrary.vo.UserVo;
import com.lsykk.caselibrary.vo.params.LoginParam;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class LoginServiceImpl implements LoginService {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Autowired
    private AuthenticationManager authenticationManager;

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

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        /* 返回生成token */
        return ApiResult.success(JWTUtils.createToken(user.getId()));
    }

    @Override
    public String encryptedPassword(String password){
        return DigestUtils.md5Hex(password + slat);
    }

    @Override
    public ApiResult logout(String token){
        return ApiResult.success();
    }

    @Override
    public ApiResult sendVerifyMail(String email){
        String code = MailUtils.getRandomCode(6);
        // 发送验证码邮件
        if (MailUtils.sendVerifyCode(email, code)){
            // 加密
            String enCode = encryptedPassword(code);
            // redis缓存加密后的验证码，设置过期时间位5分钟
            redisTemplate.opsForValue().set("EmailCode_" + email, enCode,5, TimeUnit.MINUTES);
            return ApiResult.success();
        }
        // 发送邮件失败
        return ApiResult.fail(ErrorCode.CANNOT_SENT_EMAIL);
    }

    @Override
    public ApiResult loginByEmailCode(LoginParam loginParam){
        // 检查邮箱与密码是否为空
        if (StringUtils.isBlank(loginParam.getEmail())){
            return ApiResult.fail(ErrorCode.PARAMS_ERROR.getCode(),ErrorCode.PARAMS_ERROR.getMsg());
        }
        // 检查验证码是否有效
        if (!checkVerifyCode(loginParam.getEmail(), loginParam.getPassword())){
            return ApiResult.fail(ErrorCode.EMAIL_CODE_ERROR);
        }
        User user = userService.findUserByEmail(loginParam.getEmail());
        // 未注册邮箱，注册
        if (user == null){
            // 密码设为特殊空值
            loginParam.setPassword("");
            return register(loginParam);
        }
        // 已注册邮箱，直接登录，返回token
        return ApiResult.success(JWTUtils.createToken(user.getId()));
    }

     // 检查验证码
     private boolean checkVerifyCode(String email, String code){
         if (StringUtils.isBlank(code)){
             return false;
         }
        // 获取redis中缓存的对应验证码
        String trueCode = redisTemplate.opsForValue().get("EmailCode_" + email);
        // 加密
        String enCode = encryptedPassword(code);
        return enCode.equals(trueCode);
     }

    @Override
    public ApiResult register(LoginParam loginParam) {
        // 检查邮箱参数是否合法
        if (StringUtils.isBlank(loginParam.getEmail())){
            return ApiResult.fail(ErrorCode.PARAMS_ERROR.getCode(), ErrorCode.PARAMS_ERROR.getMsg());
        }
        // 检查邮箱是否唯一
        if (userService.findUserByEmail(loginParam.getEmail()) != null){
            return ApiResult.fail(ErrorCode.ACCOUNT_EXIST.getCode(),"该邮箱已经被注册了");
        }
        User user = new User();
        user.setEmail(loginParam.getEmail());
        // 默认头像
        user.setImage("http://case-lib.test.upcdn.net/images/848bd165-48e9-4788-92d7-91b7fc1b169d.jpg");
        // 随机用户名
        user.setUsername("user_" + MailUtils.getRandomCode(8));
        // 检查密码是否为空，为空表示快速登录（特殊值）
        if (StringUtils.isNotBlank(loginParam.getPassword())){
            // 密码需要加密保存
            user.setPassword(DigestUtils.md5Hex(loginParam.getPassword() + slat));
        }
        else {
            user.setPassword("");
        }
        /* 默认注册权限为2级 */
        user.setAuthority(2);
        user.setStatus(1);
        userService.saveUser(user);
        redisTemplate.opsForValue().set("UserVo_" + String.valueOf(user.getId()), JSON.toJSONString(user),5, TimeUnit.MINUTES);
        /* 生成token */
        return ApiResult.success(JWTUtils.createToken(user.getId()));
    }

    @Override
    public User checkToken(String token){
        if (StringUtils.isBlank(token)){
            return null;
        }
        String str = JWTUtils.checkAndGetUserId(token);
        if (str == null){
            return null;
        }
        Long userId = Long.valueOf(str);
        return userService.findUserById(userId);
    }

    @Override
    public ApiResult reLogin(String token){
        String str = JWTUtils.checkAndGetUserId(token);
        if (str == null){
            return ApiResult.fail(ErrorCode.TOKEN_ERROR);
        }
        Long userId = Long.valueOf(str);
        // 返回新token
        return ApiResult.success(JWTUtils.createToken(userId));
    }

    public UserVo copy(User user){
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(user, userVo);
        if (user.getCreateTime() != null ){
            userVo.setCreateTime(DateUtils.getTime(user.getCreateTime()));
        }
        return userVo;
    }
}
