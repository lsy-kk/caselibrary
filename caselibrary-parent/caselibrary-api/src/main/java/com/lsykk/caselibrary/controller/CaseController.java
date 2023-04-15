package com.lsykk.caselibrary.controller;

import com.lsykk.caselibrary.dao.pojo.CaseBody;
import com.lsykk.caselibrary.dao.pojo.CaseHeader;
import com.lsykk.caselibrary.service.CaseService;
import com.lsykk.caselibrary.vo.ApiResult;
import com.lsykk.caselibrary.vo.CaseBodyVo;
import com.lsykk.caselibrary.vo.params.CaseParam;
import com.lsykk.caselibrary.vo.params.PageParams;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
    @GetMapping("/getCaseList")
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
    public ApiResult getHotList(){
        return ApiResult.success();
    }

    // 获取搜索关键字排序列表
    @GetMapping("/getSearchList")
    public ApiResult getSearchList(@RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                   @RequestParam(defaultValue = "") String keyword){
        PageParams pageParams = new PageParams(page, pageSize);
        return caseService.getSearchList(pageParams, keyword);
    }

    // 获取其他作者主页案例列表
    @GetMapping("/getOtherAuthorList")
    public ApiResult getOtherAuthorList(@RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "10") Integer pageSize,
                                        @RequestParam(defaultValue = "10") Long userId,
                                        @RequestParam(defaultValue = "false") boolean isBody,
                                        @RequestParam(defaultValue = "false") boolean isComment){
        PageParams pageParams = new PageParams(page, pageSize);
        return ApiResult.success(caseService.getOtherAuthorList(pageParams, userId, isBody, isComment));
    }

    // 获取个人主页案例列表
    @GetMapping("/getMyList")
    public ApiResult getMyList(@RequestParam(defaultValue = "1") Integer page,
                               @RequestParam(defaultValue = "10") Integer pageSize,
                               @RequestParam Long userId,
                               @RequestParam(defaultValue = "1") Integer visible,
                               @RequestParam(defaultValue = "3") Integer state,
                               @RequestParam(defaultValue = "false") boolean isBody,
                               @RequestParam(defaultValue = "false") boolean isComment){
        PageParams pageParams = new PageParams(page, pageSize);
        return ApiResult.success(caseService.getMyList(pageParams, userId, visible, state, isBody, isComment));
    }

    @GetMapping("/getCasesByFavoritesId")
    public ApiResult getCasesByFavoritesId(@RequestParam Long favoritesId){
        return ApiResult.success(caseService.getCasesByFavoritesId(favoritesId));
    }

    @GetMapping("/getCaseHeaderVo")
    public ApiResult getCaseHeaderVo(@RequestParam Long caseId,
                                     @RequestParam(defaultValue = "false") boolean isBody,
                                     @RequestParam(defaultValue = "false") boolean isComment){
        return ApiResult.success(caseService.getCaseHeaderVoById(caseId, isBody, isComment));
    }


    @PostMapping("/submitCaseParam")
    public ApiResult submitCaseParam(@RequestBody CaseParam caseParam){
        return caseService.submitCaseParam(caseParam);
    }

    @GetMapping("/getCaseParam")
    public ApiResult getCaseParam(@RequestParam(required = false) Long caseId){
        return caseService.getCaseParamById(caseId);
    }

    @PutMapping("/update")
    public ApiResult updateCaseHeader(@RequestBody CaseHeader caseHeader){
        return caseService.updateCaseHeader(caseHeader);
    }
}
