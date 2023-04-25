package com.lsykk.caselibrary.controller;

import com.lsykk.caselibrary.service.CaseService;
import com.lsykk.caselibrary.service.TagService;
import com.lsykk.caselibrary.service.UserService;
import com.lsykk.caselibrary.vo.ApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/backdoor")
public class BackdoorController {

    @Autowired
    private CaseService caseService;
    @Autowired
    private TagService tagService;
    @Autowired
    private UserService userService;

    @GetMapping("/caseHeaderVoRepository")
    public ApiResult caseHeaderVoRepositoryReload(){
        return caseService.caseHeaderVoRepositoryReload();
    }

    @GetMapping("/tagVoRepository")
    public ApiResult tagVoRepositoryReload(){
        return tagService.tagVoRepositoryReload();
    }

    @GetMapping("/userVoRepository")
    public ApiResult userVoRepositoryReload(){
        return userService.userVoRepositoryReload();
    }

}
