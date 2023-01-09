package com.lsykk.caselibrary.controller;

import com.lsykk.caselibrary.vo.ApiResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 使用json数据进行交互
@RestController
@RequestMapping("/user")
public class UserController {

    /**
     * 登录
     * @return
     */
    @PostMapping("/login")
    public ApiResult login(){
        return ApiResult.success();
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    public ApiResult register(){
        return ApiResult.success();
    }
}
