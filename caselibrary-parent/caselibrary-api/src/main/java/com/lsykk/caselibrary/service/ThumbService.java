package com.lsykk.caselibrary.service;

import com.lsykk.caselibrary.dao.pojo.Thumb;
import com.lsykk.caselibrary.vo.ApiResult;

public interface ThumbService {

    /**
     * 插入一条点赞信息
     * @param thumb
     * @return
     */
    ApiResult insertThumb(Thumb thumb);

    /**
     * 通过案例id和用户id获取点赞记录
     * @param caseId
     * @param userId
     * @return
     */
    boolean getThumbByCaseIdAndUserId(Long caseId, Long userId);
}
