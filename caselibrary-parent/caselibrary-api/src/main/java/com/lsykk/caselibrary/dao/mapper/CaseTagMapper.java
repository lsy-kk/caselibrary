package com.lsykk.caselibrary.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lsykk.caselibrary.dao.pojo.CaseTag;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CaseTagMapper extends BaseMapper<CaseTag> {

    void updateStatusByCaseIdAndTagId(Long caseId, Long tagId);
}
