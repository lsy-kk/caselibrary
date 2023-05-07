package com.lsykk.caselibrary.controller;

import com.lsykk.caselibrary.common.aop.LogAnnotation;
import com.lsykk.caselibrary.dao.pojo.User;
import com.lsykk.caselibrary.service.AuthorizeService;
import com.lsykk.caselibrary.service.LoginService;
import com.lsykk.caselibrary.service.UserService;
import com.lsykk.caselibrary.vo.ApiResult;
import com.lsykk.caselibrary.vo.params.PageParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

// 使用json数据进行交互
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private LoginService loginService;

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/getList")
    @LogAnnotation(module="用户",operator="管理员获取用户列表")
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

    @GetMapping("/getSearchList")
    @LogAnnotation(module="用户",operator="获取搜索用户列表")
    public ApiResult getSearchList(@RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                   @RequestParam(defaultValue = "") String keyword){
        PageParams pageParams = new PageParams(page, pageSize);
        return userService.getSearchList(pageParams, keyword);
    }

    @PreAuthorize("hasAuthority('admin')")
    @PostMapping("/insert")
    @LogAnnotation(module="用户",operator="新增用户")
    public ApiResult insert(@RequestBody User user){
        return userService.insertUser(user);
    }

    @PreAuthorize("@authorizeService.checkUser(#user) or hasAuthority('admin')")
    @PutMapping("/update")
    @LogAnnotation(module="用户",operator="更新用户信息")
    public ApiResult update(@RequestBody User user){
        return userService.updateUser(user);
    }

    @PreAuthorize("@authorizeService.checkUser(#user) or hasAuthority('admin')")
    @PutMapping("/updatePassword")
    @LogAnnotation(module="用户",operator="更新用户密码信息")
    public ApiResult updatePassword(@RequestBody User user){
        return userService.updatePassword(user);
    }

    @GetMapping("/getUserVoByToken")
    @LogAnnotation(module="用户",operator="根据TOKEN获取用户")
    public ApiResult getUserVoByToken(@RequestHeader("Authorization") String token){
        return userService.findUserVoByToken(token);
    }

    @GetMapping("/getUserVoById")
    @LogAnnotation(module="用户",operator="根据ID获取用户")
    public ApiResult getUserVoById(@RequestParam Long id){
        return ApiResult.success(userService.findUserVoById(id));
    }
}
