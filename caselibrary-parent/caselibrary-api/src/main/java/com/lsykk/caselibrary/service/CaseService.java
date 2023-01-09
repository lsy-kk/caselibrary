package com.lsykk.caselibrary.service;

import com.lsykk.caselibrary.vo.ApiResult;
import com.lsykk.caselibrary.vo.params.PageParams;

public interface CaseService {

    public ApiResult getCaseList(PageParams pageParams);
}
