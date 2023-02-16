package com.lsykk.caselibrary.controller;

import com.lsykk.caselibrary.service.CaseService;
import com.lsykk.caselibrary.vo.ApiResult;
import com.lsykk.caselibrary.vo.params.PageParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/case")
public class CaseController {

    @Autowired
    private CaseService caseService;

    // 管理员，获取所有案例列表，支持id查询
    @GetMapping("getList")
    public ApiResult getCaseListAll(@RequestParam(defaultValue = "1") Integer page,
                                 @RequestParam(defaultValue = "10") Integer pageSize,
                                 @RequestParam(required = false) Long id) {
        PageParams pageParams = new PageParams(page, pageSize);
        return caseService.getCaseListAll(pageParams, id);
    }

    // 管理员，获取待审批/被打回案例列表
    @GetMapping("getDealList")
    public ApiResult getDealList(@RequestParam(defaultValue = "1") Integer page,
                                 @RequestParam(defaultValue = "10") Integer pageSize,
                                 @RequestParam(defaultValue = "1") Integer state) {
        PageParams pageParams = new PageParams(page, pageSize);
        return caseService.getDealList(pageParams, state);
    }

    // 获取热度排序列表
    @GetMapping("getHotList")
    public ApiResult getHotList(){
        return ApiResult.success();
    }

    // 获取搜索关键字排序列表
    @GetMapping("getSearchList")
    public ApiResult getSearchList(){
        return ApiResult.success();
    }

    // 获取其他作者主页案例列表
    @GetMapping("getOtherAuthorList")
    public ApiResult getOtherAuthorList(@RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "10") Integer pageSize,
                                        @RequestParam(defaultValue = "10") Long userId){
        PageParams pageParams = new PageParams(page, pageSize);
        return ApiResult.success(caseService.getOtherAuthorList(pageParams, userId));
    }

    // 获取个人主页案例列表
    @GetMapping("getMyList")
    public ApiResult getMyList(@RequestParam(defaultValue = "1") Integer page,
                               @RequestParam(defaultValue = "10") Integer pageSize,
                               @RequestParam Long userId,
                               @RequestParam(defaultValue = "3") Integer state){
        PageParams pageParams = new PageParams(page, pageSize);
        return ApiResult.success(caseService.getMyList(pageParams, userId, state));
    }

    @GetMapping("/getCasesByFavoritesId")
    public ApiResult getCasesByFavoritesId(@RequestParam Long favoritesId){
        return ApiResult.success(caseService.getCasesByFavoritesId(favoritesId));
    }
}
