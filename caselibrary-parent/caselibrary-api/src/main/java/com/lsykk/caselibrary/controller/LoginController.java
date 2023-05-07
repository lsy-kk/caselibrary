package com.lsykk.caselibrary.controller;

import com.lsykk.caselibrary.common.aop.LogAnnotation;
import com.lsykk.caselibrary.service.LoginService;
import com.lsykk.caselibrary.vo.ApiResult;
import com.lsykk.caselibrary.vo.params.LoginParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
public class LoginController {
    @Autowired
    private LoginService loginService;

    // 登录
    @PostMapping("")
    @LogAnnotation(module="登录",operator="邮箱密码登录")
    public ApiResult login(@RequestBody LoginParam loginParam){
        return loginService.login(loginParam);
    }

    //退出登录
    @GetMapping("/logout")
    @LogAnnotation(module="登录",operator="登出")
    public ApiResult logout(@RequestHeader("Authorization") String token){
        return loginService.logout(token);
    }

    @PostMapping("/sendEmailCode")
    @LogAnnotation(module="登录",operator="发送邮箱验证码")
    public ApiResult sendEmailCode(@RequestBody LoginParam loginParam){
        return loginService.sendVerifyMail(loginParam.getEmail());
    }

    @PostMapping("/loginByEmailCode")
    @LogAnnotation(module="登录",operator="邮箱验证码登录或注册")
    public ApiResult loginByEmailCode(@RequestBody LoginParam loginParam){
        return loginService.loginByEmailCode(loginParam);
    }
    //注册
    @PostMapping("/register")
    @LogAnnotation(module="登录",operator="邮箱密码注册")
    public ApiResult register(@RequestBody LoginParam loginParam){
        return loginService.register(loginParam);
    }

    @GetMapping("/reLogin")
    @LogAnnotation(module="登录",operator="重新登录")
    public ApiResult reLogin(@RequestHeader("Authorization") String token){
        return ApiResult.success(loginService.reLogin(token));
    }
}
