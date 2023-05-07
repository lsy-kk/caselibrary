package com.lsykk.caselibrary.controller;

import com.lsykk.caselibrary.common.aop.LogAnnotation;
import com.lsykk.caselibrary.common.cache.Cache;
import com.lsykk.caselibrary.dao.pojo.CaseBody;
import com.lsykk.caselibrary.dao.pojo.CaseHeader;
import com.lsykk.caselibrary.service.CaseService;
import com.lsykk.caselibrary.vo.ApiResult;
import com.lsykk.caselibrary.vo.CaseBodyVo;
import com.lsykk.caselibrary.vo.params.CaseParam;
import com.lsykk.caselibrary.vo.params.PageParams;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;

@RestController
@RequestMapping("/case")
public class CaseController {

    @Autowired
    private CaseService caseService;

    // 分页、根据条件获取所有案例Vo列表
    @GetMapping("/getCaseVoList")
    @LogAnnotation(module="案例",operator="条件获取案例列表")
    public ApiResult getCaseVoList(@RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "10") Integer pageSize,
                                    @RequestParam(required = false) Long id,
                                    @RequestParam(required = false) Long authorId,
                                    @RequestParam(required = false) Integer visible,
                                    @RequestParam(required = false) Integer state,
                                    @RequestParam(defaultValue = "false") boolean isBody,
                                    @RequestParam(defaultValue = "false") boolean isComment) {
        PageParams pageParams = new PageParams(page, pageSize);
        return caseService.getCaseHeaderVoList(pageParams, id, authorId, visible, state, isBody, isComment);
    }

    // 分页、根据条件获取所有案例列表
    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/getCaseList")
    @LogAnnotation(module="案例",operator="管理员获取案例列表")
    public ApiResult getCaseList(@RequestParam(defaultValue = "1") Integer page,
                                 @RequestParam(defaultValue = "10") Integer pageSize,
                                 @RequestParam(required = false) Long id,
                                 @RequestParam(required = false) Long authorId,
                                 @RequestParam(required = false) Integer visible,
                                 @RequestParam(required = false) Integer state,
                                 @RequestParam(required = false) Integer status) {
        PageParams pageParams = new PageParams(page, pageSize);
        return caseService.getCaseHeaderList(pageParams, id, authorId, visible, state, status);
    }

    // 获取热度排序列表
    @GetMapping("/getHotList")
    @Cache(expire = 5 * 60 * 1000,name = "getHotCaseList")
    @LogAnnotation(module="案例",operator="获取最热案例列表")
    public ApiResult getHotList(@RequestParam(defaultValue = "1") Integer page,
                                @RequestParam(defaultValue = "10") Integer pageSize){
        PageParams pageParams = new PageParams(page, pageSize);
        return caseService.getHotList(pageParams);
    }

    // 获取搜索关键字排序列表
    @GetMapping("/getSearchList")
    @LogAnnotation(module="案例",operator="获取搜索案例列表")
    public ApiResult getSearchList(@RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                   @RequestParam(defaultValue = "") String keyword,
                                   @RequestParam(defaultValue = "hot") String type){
        PageParams pageParams = new PageParams(page, pageSize);
        return caseService.getSearchList(pageParams, keyword, type);
    }

    @GetMapping("/getListByTagId")
    @LogAnnotation(module="案例",operator="根据标签获取案例列表")
    public ApiResult getListByTagId(@RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "10") Integer pageSize,
                                    @RequestParam Long tagId){
        PageParams pageParams = new PageParams(page, pageSize);
        return caseService.getListByTagId(pageParams, tagId);
    }

    @GetMapping("/getListByFavoritesId")
    @LogAnnotation(module="案例",operator="根据收藏夹获取案例列表")
    public ApiResult getListByFavoritesId(@RequestParam(defaultValue = "1") Integer page,
                                          @RequestParam(defaultValue = "10") Integer pageSize,
                                          @RequestParam Long favoritesId){
        PageParams pageParams = new PageParams(page, pageSize);
        return caseService.getListByFavoritesId(pageParams, favoritesId);
    }

    @GetMapping("/getCaseHeaderVo")
    @LogAnnotation(module="案例",operator="根据ID获取案例")
    public ApiResult getCaseHeaderVo(@RequestParam Long caseId,
                                     @RequestParam(defaultValue = "false") boolean isBody,
                                     @RequestParam(defaultValue = "false") boolean isComment){
        return ApiResult.success(caseService.getCaseHeaderVoById(caseId, isBody, isComment));
    }


    @PreAuthorize("@authorizeService.checkCaseParam(#caseParam) and hasAuthority('teacher')")
    @PostMapping("/submitCaseParam")
    @LogAnnotation(module="案例",operator="提交案例参数")
    public ApiResult submitCaseParam(@RequestBody CaseParam caseParam){
        return caseService.submitCaseParam(caseParam);
    }

    @PreAuthorize("hasAuthority('teacher')")
    @GetMapping("/getCaseParam")
    @LogAnnotation(module="案例",operator="获取案例参数")
    public ApiResult getCaseParam(@RequestParam(required = false) Long caseId){
        return caseService.getCaseParamById(caseId);
    }

    @PreAuthorize("hasAuthority('admin')")
    @PutMapping("/update")
    @LogAnnotation(module="案例",operator="更改案例头部信息")
    public ApiResult updateCaseHeader(@RequestBody CaseHeader caseHeader){
        return caseService.updateCaseHeader(caseHeader);
    }
}
