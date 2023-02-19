package com.lsykk.caselibrary.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsykk.caselibrary.dao.mapper.CaseBodyMapper;
import com.lsykk.caselibrary.dao.mapper.CaseHeaderMapper;
import com.lsykk.caselibrary.dao.pojo.CaseHeader;
import com.lsykk.caselibrary.dao.pojo.CaseBody;
import com.lsykk.caselibrary.service.CaseService;
import com.lsykk.caselibrary.service.TagService;
import com.lsykk.caselibrary.service.UserService;
import com.lsykk.caselibrary.utils.DateUtils;
import com.lsykk.caselibrary.vo.ApiResult;
import com.lsykk.caselibrary.vo.CaseBodyVo;
import com.lsykk.caselibrary.vo.CaseHeaderVo;
import com.lsykk.caselibrary.vo.ErrorCode;
import com.lsykk.caselibrary.vo.params.PageParams;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

@Service
public class CaseServiceImpl implements CaseService {

    @Autowired
    private CaseHeaderMapper caseHeaderMapper;
    @Autowired
    private CaseBodyMapper caseBodyMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private TagService tagService;

    public ApiResult getCaseListAll(PageParams pageParams, Long id){
        //分页查询 case数据库表
        Page<CaseHeader> page = new Page<>(pageParams.getPage(), pageParams.getPageSize());
        LambdaQueryWrapper<CaseHeader> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(id!=null, CaseHeader::getId, id);
        // 按照id排序
        queryWrapper.orderByAsc(CaseHeader::getId);
        Page<CaseHeader> casePage = caseHeaderMapper.selectPage(page, queryWrapper);
        List<CaseHeader> caseHeaderList = casePage.getRecords();
        return ApiResult.success(caseHeaderList);
    }

    public ApiResult getDealList(PageParams pageParams, Integer state){
        //分页查询 case数据库表
        Page<CaseHeader> page = new Page<>(pageParams.getPage(), pageParams.getPageSize());
        LambdaQueryWrapper<CaseHeader> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CaseHeader::getStatus, 1);
        queryWrapper.eq(state!=null, CaseHeader::getState, state);
        // 按照id排序
        queryWrapper.orderByAsc(CaseHeader::getId);
        Page<CaseHeader> casePage = caseHeaderMapper.selectPage(page, queryWrapper);
        List<CaseHeader> caseHeaderList = casePage.getRecords();
        return ApiResult.success(caseHeaderList);
    }

    @Override
    public ApiResult getOtherAuthorList(PageParams pageParams, Long userId){
        //分页查询 case数据库表
        Page<CaseHeader> page = new Page<>(pageParams.getPage(), pageParams.getPageSize());
        LambdaQueryWrapper<CaseHeader> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CaseHeader::getStatus, 1);
        queryWrapper.eq(CaseHeader::getState, 3);
        queryWrapper.eq(CaseHeader::getVisible, 1);
        queryWrapper.eq(userId!=null, CaseHeader::getAuthorId, userId);
        // 按照id倒序排序
        queryWrapper.orderByDesc(CaseHeader::getId);
        Page<CaseHeader> casePage = caseHeaderMapper.selectPage(page, queryWrapper);
        List<CaseHeader> caseHeaderList = casePage.getRecords();
        return ApiResult.success(copyList(caseHeaderList, true));
    }

    @Override
    public ApiResult getMyList(PageParams pageParams, Long userId, Integer state){
        //分页查询 case数据库表
        Page<CaseHeader> page = new Page<>(pageParams.getPage(), pageParams.getPageSize());
        LambdaQueryWrapper<CaseHeader> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CaseHeader::getStatus, 1);
        queryWrapper.eq(state!=null, CaseHeader::getState, state);
        queryWrapper.eq(userId!=null, CaseHeader::getAuthorId, userId);
        // 按照id倒序排序
        queryWrapper.orderByDesc(CaseHeader::getId);
        Page<CaseHeader> casePage = caseHeaderMapper.selectPage(page, queryWrapper);
        List<CaseHeader> caseHeaderList = casePage.getRecords();
        return ApiResult.success(copyList(caseHeaderList, true));
    }

    @Override
    public CaseHeader getCaseHeaderById(Long id){
        return caseHeaderMapper.selectById(id);
    }

    @Override
    public ApiResult insertCaseHeader(CaseHeader caseHeader){
        if (StringUtils.isBlank(caseHeader.getTitle()) ||
                caseHeader.getAuthorId() == null ||
                caseHeader.getVisible() == null){
            return ApiResult.fail(ErrorCode.PARAMS_ERROR);
        }
        caseHeader.setStatus(1);
        caseHeaderMapper.insertAndGetId(caseHeader);
        // 返回当前caseheader的Id
        return ApiResult.success(caseHeader.getId());
    }

    @Override
    public ApiResult updateCaseHeader(CaseHeader newCaseHeader){
        caseHeaderMapper.updateById(newCaseHeader);
        return ApiResult.success();
    }

    @Override
    public List<CaseHeader> getCasesByFavoritesId(Long favoritesId){
        //return caseHeaderMapper.findCasesByFavoritesId(favoritesId);
        return null;
    }

    @Override
    public ApiResult uploadFile(MultipartFile file){
        // 获取文件原本的名字
        String originName = file.getOriginalFilename();
        if (StringUtils.isBlank(originName)){
            // 上传的文件类型错误
            return ApiResult.fail(ErrorCode.File_Upload_Illegal);
        }
        // set保存可接受的文件类型（后缀）
        Set<String> set = new HashSet<>();
        set.add(".pdf");
        set.add(".doc");
        set.add(".docx");
        set.add(".gif");
        set.add(".jpg");
        set.add(".png");
        // 获取文件后缀("."以及后面的内容)
        String fileType = originName.substring(originName.lastIndexOf("."));
        if (!set.contains(fileType)){
            // 上传的文件类型错误
            return ApiResult.fail(ErrorCode.File_Upload_Illegal);
        }
        // 若不是图片，使用原本的名称
        String fileName = originName;
        if (fileType.equals(".jpg") || fileType.equals(".png")){
            // 使用唯一的UUID作为图片名称
            fileName = UUID.randomUUID() + fileType;
        }
        // 以yyyyMMdd格式获取当前时间
        String format = DateUtils.getTimeSimple();
        // 保存路径。默认定位到的当前用户目录("user.dir")（即工程根目录），为每一天创建一个文件夹
        String savePath = System.getProperty("user.dir") + "\\" + "files" + "\\" + format;
        // 保存文件的文件夹
        File folder = new File(savePath);
        // 判断文件夹是否存在，不存在则创建文件夹。若创建文件夹失败，返回失败信息。
        if (!folder.exists() && !folder.mkdirs()){
            ApiResult.fail(ErrorCode.File_Upload_Error);
        }
        try {
            file.transferTo(new File(folder, fileName));
            String filePath = savePath + "\\" + fileName;
            return ApiResult.success(filePath);
        } catch (IOException e){
            return ApiResult.fail(ErrorCode.File_Upload_Error);
        }
    }

    @Override
    public String exportMarkdownFile(String content) {
        // 以yyyyMMdd格式获取当前时间
        String format = DateUtils.getTimeSimple();
        String fileName = UUID.randomUUID() + "-" + format + ".md";
        // 保存路径。默认定位到的当前用户目录("user.dir")（即工程根目录），为每一天创建一个文件夹
        String savePath = System.getProperty("user.dir") + "\\" + "files" + "\\" + "markdown";
        // 保存文件的文件夹
        File folder = new File(savePath);
        // 判断文件夹是否存在，不存在则创建文件夹。若创建文件夹失败，返回失败信息。
        if (!folder.exists() && !folder.mkdirs()){
            ApiResult.fail(ErrorCode.File_Upload_Error);
        }
        String filepath = savePath + "\\" + fileName;
        try (FileWriter fileWriter = new FileWriter(filepath)) {
            fileWriter.write(content);
        }
        catch (Exception exception){
            System.out.println("Error: " + exception.getMessage());
        }
        return filepath;
    }

    @Override
    public ApiResult updateCaseBody(CaseBody caseBody){
        if (caseBody.getCaseId() == null || caseBody.getVersion() == null){
            return ApiResult.fail(ErrorCode.PARAMS_ERROR);
        }
        // 虽然是update，其实是insert
        // 前端记得version+1
        caseBodyMapper.insert(caseBody);
        return ApiResult.success();
    }

    @Override
    public ApiResult getCaseBodyByCaseId(Long caseId){
        CaseBody caseBody = caseBodyMapper.findCaseBodyByCaseId(caseId);
        return ApiResult.success(caseBody);
    }

    private List<CaseHeaderVo> copyList(List<CaseHeader> list, boolean isBody){
        List<CaseHeaderVo> caseHeaderVoList = new ArrayList<>();
        for (CaseHeader caseHeader : list) {
            caseHeaderVoList.add(copy(caseHeader, isBody));
        }
        return caseHeaderVoList;
    }

    private CaseHeaderVo copy(CaseHeader caseHeader, boolean isBody){
        CaseHeaderVo caseHeaderVo = new CaseHeaderVo();
        BeanUtils.copyProperties(caseHeader, caseHeaderVo);
        caseHeaderVo.setCreateTime(DateUtils.getTime(caseHeader.getCreateTime()));
        caseHeaderVo.setUpdateTime(DateUtils.getTime(caseHeader.getUpdateTime()));
        if (isBody){
            caseHeaderVo.setCaseBody(findCaseBodyById(caseHeader.getId()));
        }
        caseHeaderVo.setAuthorName(userService.findUserById(caseHeader.getAuthorId()).getUsername());
        caseHeaderVo.setTags(tagService.findTagsByCaseId(caseHeader.getId()));
        return caseHeaderVo;
    }

    private CaseBodyVo findCaseBodyById(Long caseId) {
        CaseBody caseBody = caseBodyMapper.findCaseBodyByCaseId(caseId);
        return copy(caseBody);
    }

    private CaseBodyVo copy(CaseBody caseBody){
        CaseBodyVo caseBodyVo = new CaseBodyVo();
        BeanUtils.copyProperties(caseBody, caseBodyVo);
        List<String> list = Arrays.asList(caseBody.getAppendix().split(";"));
        caseBodyVo.setCreateTime(DateUtils.getTime(caseBody.getCreateTime()));
        caseBodyVo.setUpdateTime(DateUtils.getTime(caseBody.getUpdateTime()));
        caseBodyVo.setAppendixList(list);
        return caseBodyVo;
    }
}
