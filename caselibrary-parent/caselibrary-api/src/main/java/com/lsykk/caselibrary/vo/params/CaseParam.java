package com.lsykk.caselibrary.vo.params;

import com.lsykk.caselibrary.dao.pojo.CaseHeader;
import com.lsykk.caselibrary.vo.CaseBodyVo;
import com.lsykk.caselibrary.vo.CaseTagVo;
import lombok.Data;

import java.util.List;

@Data
public class CaseParam {

    // 案例头
    private CaseHeader caseHeader;

    // 版本号最大的body，有可能是草稿，也有可能已经发布
    private CaseBodyVo caseBodyVoLatest;

    // 版本号最大的、已发布的body，
    private CaseBodyVo caseBodyVo;

    // 案例标签（旧）
    private List<Long> oldTags;

    // 案例标签（新）
    private List<Long> newTags;
}
