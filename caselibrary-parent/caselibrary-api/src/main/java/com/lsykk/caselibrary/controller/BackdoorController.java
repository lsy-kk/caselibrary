package com.lsykk.caselibrary.controller;

import com.lsykk.caselibrary.service.*;
import com.lsykk.caselibrary.vo.ApiResult;
import com.lsykk.caselibrary.vo.NoticeVo;
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
    @Autowired
    private NoticeService noticeService;

    @GetMapping("/sendMessage")
    public ApiResult sendMessage(){
        NoticeVo noticeVo = new NoticeVo();
        noticeVo.setContent("你好");
        noticeVo.setTitle("这是一条来自管理员的消息");
        NoticeWebSocket.sendMessage(noticeVo);
        return ApiResult.success();
    }

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
