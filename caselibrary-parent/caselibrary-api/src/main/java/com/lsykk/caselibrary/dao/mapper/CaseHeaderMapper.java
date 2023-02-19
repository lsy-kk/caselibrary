package com.lsykk.caselibrary.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsykk.caselibrary.dao.pojo.CaseHeader;

public interface CaseHeaderMapper extends BaseMapper<CaseHeader> {


    // 根据收藏夹id，找到收藏夹中的案例（status=1）
    IPage<CaseHeader> findCasesByFavoritesId(Page page, Long favoritesId);

    // 根据标签id（status=1）
    IPage<CaseHeader> findCasesByTagId(Page page, Long tagId);

    // 插入并获取主键
    Long insertAndGetId(CaseHeader caseHeader);
}
