package com.lsykk.caselibrary.controller;

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
    public ApiResult login(@RequestBody LoginParam loginParam){
        return loginService.login(loginParam);
    }

    //退出登录
    @GetMapping("/logout")
    public ApiResult logout(@RequestHeader("Authorization") String token){
        return loginService.logout(token);
    }

    @PostMapping("/sendEmailCode")
    public ApiResult sendEmailCode(@RequestBody LoginParam loginParam){
        return loginService.sendVerifyMail(loginParam.getEmail());
    }

    @PostMapping("/loginByEmailCode")
    public ApiResult loginByEmailCode(@RequestBody LoginParam loginParam){
        return loginService.loginByEmailCode(loginParam);
    }
    //注册
    @PostMapping("/register")
    public ApiResult register(@RequestBody LoginParam loginParam){
        return loginService.register(loginParam);
    }

    @GetMapping("/reLogin")
    public ApiResult reLogin(@RequestHeader("Authorization") String token){
        return ApiResult.success(loginService.reLogin(token));
    }
}
