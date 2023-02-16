package com.lsykk.caselibrary.service;

import com.lsykk.caselibrary.dao.pojo.CaseHeader;
import com.lsykk.caselibrary.vo.ApiResult;
import com.lsykk.caselibrary.vo.params.PageParams;

import java.util.List;

public interface CaseService {

    /**
     * 分页获取查找结果(可选id查询，管理员，status不限）
     * @param pageParams
     * @return
     */
    ApiResult getCaseListAll(PageParams pageParams, Long id);

    /**
     * 获取管理员待审核/已打回案例列表
     * @param pageParams
     * @param state
     * @return
     */
    ApiResult getDealList(PageParams pageParams, Integer state);

    /**
     * 获取其他用户的案例列表
     * @param pageParams
     * @param userId
     * @return
     */
    ApiResult getOtherAuthorList(PageParams pageParams, Long userId);

    /**
     * 获取自己的案例列表
     * @param pageParams
     * @param userId
     * @param state
     * @return
     */
    ApiResult getMyList(PageParams pageParams, Long userId, Integer state);

    /**
     * 根据id获取案例
     * @param id
     * @return
     */
    CaseHeader getCaseById(Long id);

    /**
     * 新增案例头
     * @param newCaseHeader
     * @return
     */
    ApiResult insertCase(CaseHeader newCaseHeader);

    /**
     * 更新案例头部信息
     * @param newCaseHeader
     * @return
     */
    ApiResult updateCase(CaseHeader newCaseHeader);

    /**
     * 根据收藏夹id，获取其中的案例
     * @param favoritesId
     * @return
     */
    List<CaseHeader> getCasesByFavoritesId(Long favoritesId);
}
