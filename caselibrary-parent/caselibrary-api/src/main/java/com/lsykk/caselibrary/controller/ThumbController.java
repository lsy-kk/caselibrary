package com.lsykk.caselibrary.controller;

import com.lsykk.caselibrary.common.aop.LogAnnotation;
import com.lsykk.caselibrary.service.ThumbService;
import com.lsykk.caselibrary.vo.ApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/thumb")
public class ThumbController {
    @Autowired
    private ThumbService thumbService;

    @PreAuthorize("@authorizeService.checkUserId(#userId)")
    @GetMapping("/insert")
    @LogAnnotation(module="点赞",operator="新增点赞记录")
    public ApiResult insert(@RequestParam Long caseId, @RequestParam Long userId){
        return thumbService.insertThumb(caseId, userId);
    }

    @PreAuthorize("@authorizeService.checkUserId(#userId)")
    @GetMapping("/delete")
    @LogAnnotation(module="点赞",operator="删除点赞记录")
    public ApiResult delete(@RequestParam Long caseId, @RequestParam Long userId){
        return thumbService.deleteThumb(caseId, userId);
    }

    @PreAuthorize("@authorizeService.checkUserId(#userId)")
    @GetMapping("/get")
    @LogAnnotation(module="点赞",operator="获取点赞记录")
    public ApiResult get(@RequestParam Long caseId, @RequestParam Long userId){
        return ApiResult.success(thumbService.getThumbByCaseIdAndUserId(caseId, userId));
    }
}
