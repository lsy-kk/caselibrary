package com.lsykk.caselibrary.controller;

import com.lsykk.caselibrary.dao.pojo.User;
import com.lsykk.caselibrary.service.LoginService;
import com.lsykk.caselibrary.service.UserService;
import com.lsykk.caselibrary.vo.ApiResult;
import com.lsykk.caselibrary.vo.params.PageParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// 使用json数据进行交互
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private LoginService loginService;

    @GetMapping("/getList")
    public ApiResult getUserList(@RequestParam(defaultValue = "1") Integer page,
                                 @RequestParam(defaultValue = "10") Integer pageSize,
                                 @RequestParam(required = false)  Long id,
                                 @RequestParam(required = false)  String email,
                                 @RequestParam(required = false)  Integer authority){
        PageParams pageParams = new PageParams(page, pageSize);
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setAuthority(authority);
        return userService.getUserList(pageParams, user);
    }
    @PostMapping("/insert")
    public ApiResult insert(@RequestBody User user){
        return userService.insertUser(user);
    }

    @PutMapping("/update")
    public ApiResult update(@RequestBody User user){
        return userService.updateUser(user);
    }

    @PutMapping("/updatePassword")
    public ApiResult updatePassword(@RequestBody User user){
        return userService.updatePassword(user);
    }

    @GetMapping("/getUserByToken")
    public ApiResult getUserByToken(@RequestParam String token){
        return userService.findUserByToken(token);
    }

    @GetMapping("/getUserById")
    public ApiResult getUserById(@RequestParam Long id){
        return ApiResult.success(userService.findUserById(id));
    }

    @GetMapping("/getUserVoById")
    public ApiResult getUserVoById(@RequestParam Long id){
        return ApiResult.success(userService.findUserVoById(id));
    }
}
