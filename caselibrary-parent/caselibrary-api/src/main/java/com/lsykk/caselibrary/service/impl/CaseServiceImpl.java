package com.lsykk.caselibrary.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsykk.caselibrary.dao.mapper.CaseBodyMapper;
import com.lsykk.caselibrary.dao.mapper.CaseMapper;
import com.lsykk.caselibrary.dao.pojo.CaseHeader;
import com.lsykk.caselibrary.dao.pojo.CaseBody;
import com.lsykk.caselibrary.service.CaseService;
import com.lsykk.caselibrary.service.TagService;
import com.lsykk.caselibrary.service.UserService;
import com.lsykk.caselibrary.utils.DateUtils;
import com.lsykk.caselibrary.vo.ApiResult;
import com.lsykk.caselibrary.vo.CaseBodyVo;
import com.lsykk.caselibrary.vo.CaseHeaderVo;
import com.lsykk.caselibrary.vo.params.PageParams;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CaseServiceImpl implements CaseService {

    @Autowired
    private CaseMapper caseMapper;
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
        Page<CaseHeader> casePage = caseMapper.selectPage(page, queryWrapper);
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
        Page<CaseHeader> casePage = caseMapper.selectPage(page, queryWrapper);
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
        Page<CaseHeader> casePage = caseMapper.selectPage(page, queryWrapper);
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
        Page<CaseHeader> casePage = caseMapper.selectPage(page, queryWrapper);
        List<CaseHeader> caseHeaderList = casePage.getRecords();
        return ApiResult.success(copyList(caseHeaderList, true));
    }

    @Override
    public CaseHeader getCaseById(Long id){
        return caseMapper.selectById(id);
    }

    @Override
    public ApiResult insertCase(CaseHeader newCaseHeader){
        // 初始时，案例为未发布状态
        newCaseHeader.setState(0);
        newCaseHeader.setStatus(1);
        caseMapper.insert(newCaseHeader);
        return ApiResult.success();
    }

    @Override
    public ApiResult updateCase(CaseHeader newCaseHeader){
        caseMapper.updateById(newCaseHeader);
        return ApiResult.success();
    }

    @Override
    public List<CaseHeader> getCasesByFavoritesId(Long favoritesId){
        //return caseMapper.findCasesByFavoritesId(favoritesId);
        return null;
    }

    private List<CaseHeaderVo> copyList(List<CaseHeader> list, boolean isBody){
        List<CaseHeaderVo> caseHeaderVoList = new ArrayList<>();
        for (CaseHeader cse : list) {
            caseHeaderVoList.add(copy(cse, isBody));
        }
        return caseHeaderVoList;
    }

    private CaseHeaderVo copy(CaseHeader cse, boolean isBody){
        CaseHeaderVo caseHeaderVo = new CaseHeaderVo();
        BeanUtils.copyProperties(cse, caseHeaderVo);
        caseHeaderVo.setCreateTime(DateUtils.getTime(cse.getCreateTime()));
        caseHeaderVo.setUpdateTime(DateUtils.getTime(cse.getUpdateTime()));
        if (isBody){
            caseHeaderVo.setCaseBody(findCaseBodyById(cse.getId()));
        }
        caseHeaderVo.setAuthorName(userService.findUserById(cse.getAuthorId()).getUsername());
        caseHeaderVo.setTags(tagService.findTagsByCaseId(cse.getId()));
        return caseHeaderVo;
    }

    private CaseBodyVo findCaseBodyById(Long caseId) {
        CaseBody CaseBody = caseBodyMapper.findCaseBodyByCaseId(caseId);
        CaseBodyVo CaseBodyVo = new CaseBodyVo();
        CaseBodyVo.setContent(CaseBody.getContent());
        return CaseBodyVo;
    }
}
