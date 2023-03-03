package com.lsykk.caselibrary.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lsykk.caselibrary.dao.pojo.CaseBody;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CaseBodyMapper extends BaseMapper<CaseBody> {

    /**
     * 根据案例id，获取status为1、version最大且state=1（已发布）的内容
     * @param caseId
     * @return
     */
    CaseBody findCaseBodyByCaseId(Long caseId);

    /**
     * 根据案例id，获取status为1且version最大的内容（state可能是0/1）
     * @param caseId
     * @return
     */
    CaseBody findCaseBodyLatestByCaseId(Long caseId);

}
