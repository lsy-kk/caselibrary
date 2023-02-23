package com.lsykk.caselibrary.controller;

import com.lsykk.caselibrary.dao.pojo.CaseBody;
import com.lsykk.caselibrary.dao.pojo.CaseHeader;
import com.lsykk.caselibrary.service.CaseService;
import com.lsykk.caselibrary.vo.ApiResult;
import com.lsykk.caselibrary.vo.CaseBodyVo;
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
    @GetMapping("getCaseVoList")
    public ApiResult getCaseVoList(@RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "10") Integer pageSize,
                                    @RequestParam(required = false) Long id,
                                    @RequestParam(required = false) Long authorId,
                                    @RequestParam(required = false) Integer visible,
                                    @RequestParam(required = false) Integer state,
                                    @RequestParam(required = false) Integer status,
                                    @RequestParam(required = false) boolean isBody,
                                    @RequestParam(required = false) boolean isComment) {
        PageParams pageParams = new PageParams(page, pageSize);
        return caseService.getCaseHeaderVoList(pageParams, id, authorId, visible, state, status, isBody, isComment);
    }

    // 获取热度排序列表
    @GetMapping("getHotList")
    public ApiResult getHotList(){
        return ApiResult.success();
    }

    // 获取搜索关键字排序列表
    @GetMapping("getSearchList")
    public ApiResult getSearchList(@RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                   @RequestParam(defaultValue = "") String keyword){
        PageParams pageParams = new PageParams(page, pageSize);
        return caseService.getSearchList(pageParams, keyword);
    }

    // 获取其他作者主页案例列表
    @GetMapping("getOtherAuthorList")
    public ApiResult getOtherAuthorList(@RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "10") Integer pageSize,
                                        @RequestParam(defaultValue = "10") Long userId,
                                        @RequestParam(defaultValue = "false") boolean isBody,
                                        @RequestParam(defaultValue = "false") boolean isComment){
        PageParams pageParams = new PageParams(page, pageSize);
        return ApiResult.success(caseService.getOtherAuthorList(pageParams, userId, isBody, isComment));
    }

    // 获取个人主页案例列表
    @GetMapping("getMyList")
    public ApiResult getMyList(@RequestParam(defaultValue = "1") Integer page,
                               @RequestParam(defaultValue = "10") Integer pageSize,
                               @RequestParam Long userId,
                               @RequestParam(defaultValue = "3") Integer state,
                               @RequestParam(defaultValue = "false") boolean isBody,
                               @RequestParam(defaultValue = "false") boolean isComment){
        PageParams pageParams = new PageParams(page, pageSize);
        return ApiResult.success(caseService.getMyList(pageParams, userId, state, isBody, isComment));
    }

    @GetMapping("/getCasesByFavoritesId")
    public ApiResult getCasesByFavoritesId(@RequestParam Long favoritesId){
        return ApiResult.success(caseService.getCasesByFavoritesId(favoritesId));
    }

    @PostMapping("/insertCaseHeader")
    public ApiResult insertCaseHeader(@RequestBody CaseHeader caseHeader){
        return caseService.insertCaseHeader(caseHeader);
    }

    @GetMapping("/getCaseHeader")
    public ApiResult getCaseHeader(@RequestParam Long caseId){
        return ApiResult.success(caseService.getCaseHeaderById(caseId));
    }

    @PostMapping("/updateCaseHeader")
    public ApiResult updateCaseHeader(@RequestBody CaseHeader caseHeader){
        return caseService.updateCaseHeader(caseHeader);
    }

    @GetMapping("/getCaseHeaderVo")
    public ApiResult getCaseHeaderVo(@RequestParam Long caseId,
                                     @RequestParam(defaultValue = "false") boolean isBody,
                                     @RequestParam(defaultValue = "false") boolean isComment){
        return ApiResult.success(caseService.getCaseHeaderVoById(caseId, isBody, isComment));
    }

    @PostMapping("/uploadFile")
    public ApiResult uploadFile(MultipartFile file){
        return caseService.uploadFile(file);
    }


    @GetMapping("/downloadFile")
    public void downloadFile(String filePath, HttpServletResponse response) throws Exception {
        FileInputStream in = new FileInputStream(filePath);
        byte[] bytes = IOUtils.toByteArray(in);
        response.setContentType("application/force-download");
        response.setContentLength(bytes.length);
        response.setHeader("Content-Disposition", "attachment;filename=result");
        ServletOutputStream out = response.getOutputStream();
        out.write(bytes);
        out.close();
        in.close();
    }

    @PostMapping("/exportMarkdownFile")
    public void exportMarkdownFile(@RequestBody CaseBody caseBody, HttpServletResponse response) throws Exception {
        String filePath = caseService.exportMarkdownFile(caseBody.getContent());
        FileInputStream in = new FileInputStream(filePath);
        byte[] bytes = IOUtils.toByteArray(in);
        response.setContentType("application/force-download");
        response.setContentLength(bytes.length);
        response.setHeader("Content-Disposition", "attachment;filename=result");
        ServletOutputStream out = response.getOutputStream();
        out.write(bytes);
        out.close();
        in.close();
    }


    @GetMapping("/getCaseBodyByCaseId")
    public ApiResult getCaseBodyByCaseId(@RequestParam Long caseId){
        return caseService.getCaseBodyByCaseId(caseId);
    }

    @PostMapping("/insertCaseBody")
    public ApiResult insertCaseBody(@RequestBody CaseBody caseBody){
        return caseService.insertCaseBody(caseBody);
    }


    @PostMapping("/updateCaseBody")
    public ApiResult updateCaseBody(@RequestBody CaseBody caseBody){
        return caseService.updateCaseBody(caseBody);
    }
}
