package com.lsykk.caselibrary.controller;

import com.lsykk.caselibrary.dao.pojo.User;
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
    public UserService userService;

    @PostMapping("/getlist")
    public ApiResult getUserList(@RequestBody PageParams pageParams){
        return userService.getUserList(pageParams);
    }
    @PostMapping("/save")
    public ApiResult save(@RequestBody User user){
        userService.saveUser(user);
        return ApiResult.success();
    }

    @PutMapping("/update")
    public ApiResult update(@RequestBody User user){
        userService.updateUser(user);
        return ApiResult.success();
    }
}
