package com.lsykk.caselibrary.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsykk.caselibrary.dao.mapper.CaseMapper;
import com.lsykk.caselibrary.dao.pojo.Case;
import com.lsykk.caselibrary.service.CaseService;
import com.lsykk.caselibrary.vo.ApiResult;
import com.lsykk.caselibrary.vo.params.PageParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CaseServiceImpl implements CaseService {

    @Autowired
    private CaseMapper caseMapper;

    public ApiResult getCaseList(PageParams pageParams){
        //分页查询 case数据库表
        Page<Case> page = new Page<>(pageParams.getPage(), pageParams.getPageSize());
        LambdaQueryWrapper<Case> queryWrapper = new LambdaQueryWrapper<>();
        // 按照时间顺序排序
        queryWrapper.orderByDesc(Case::getCreateTime);
        Page<Case> casePage = caseMapper.selectPage(page, queryWrapper);
        List<Case> caseList = casePage.getRecords();
        return ApiResult.success(caseList);
    }
}
