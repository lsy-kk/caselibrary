package com.lsykk.caselibrary.service;

import com.lsykk.caselibrary.dao.pojo.Tag;
import com.lsykk.caselibrary.vo.ApiResult;
import com.lsykk.caselibrary.vo.CaseTagVo;
import com.lsykk.caselibrary.vo.TagVo;
import com.lsykk.caselibrary.vo.params.PageParams;

import java.util.List;

public interface TagService {

    /**
     * 分页（可能有条件）获取标签列表
     * @param pageParams
     * @param tag
     * @return
     */
    ApiResult getTagList(PageParams pageParams, Tag tag);

    /**
     * （可能有前缀限制）获取标签列表
     * @param prefix
     * @return
     */
    ApiResult getTagListByPrefix(String prefix);

    /**
     * 根据id获取tag
     * @param id
     * @return
     */
    Tag findTagById(Long id);

    /**
     * 新增标签
     * @param tag
     * @return
     */
    ApiResult insertTag(Tag tag);

    /**
     * 根据id更新标签
     * @param tag
     * @return
     */
    ApiResult updateTag(Tag tag);

    /**
     * 根据案例id，找到对应的caseTagVo记录
     * @param caseId
     * @return
     */
    List<CaseTagVo> findCaseTagVoByCaseId(Long caseId);

    /**
     * 更新CaseId对应的CaseTag记录
     * @param oldList
     * @param newList
     * @param caseId
     */
    void updateCaseTagByCaseId(List<Long> oldList, List<Long> newList, Long caseId);

    /**
     * 根据案例id，找到其对应的所有标签
     * @param caseId
     * @return
     */
    List<TagVo> findTagVoByCaseId(Long caseId);

    /**
     * 根据案例id，更新案例标签
     * @param tagIds
     * @param caseId
     * @return
     */
    ApiResult updateTagByCaseId(List<Long> tagIds, Long caseId);
}
