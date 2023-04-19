package com.lsykk.caselibrary.service;

import com.lsykk.caselibrary.dao.pojo.Thumb;
import com.lsykk.caselibrary.vo.ApiResult;

public interface ThumbService {

    /**
     * 根据案例id和用户id，插入一条点赞信息
     * @param caseId
     * @param userId
     * @return
     */
    ApiResult insertThumb(Long caseId, Long userId);

    /**
     * 根据案例id和用户id，删除点赞信息
     * @param caseId
     * @param userId
     * @return
     */
    ApiResult deleteThumb(Long caseId, Long userId);

    /**
     * 通过案例id和用户id，判断是否有点赞记录
     * @param caseId
     * @param userId
     * @return
     */
    boolean getThumbByCaseIdAndUserId(Long caseId, Long userId);
}
