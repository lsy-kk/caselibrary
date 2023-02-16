package com.lsykk.caselibrary.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lsykk.caselibrary.dao.pojo.CaseBody;

public interface CaseBodyMapper extends BaseMapper<CaseBody> {

    /**
     * 根据案例id，获取status为1且version最大的案例内容
     * @param caseId
     * @return
     */
    CaseBody findCaseBodyByCaseId(Long caseId);
}
