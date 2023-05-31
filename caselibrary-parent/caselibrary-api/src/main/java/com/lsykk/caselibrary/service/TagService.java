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
     * 分页获取标签Vo列表
     * @param pageParams
     * @param id
     * @param name
     * @return
     */
    ApiResult getTagVoList(PageParams pageParams, Long id, String name);

    /**
     * （可能有前缀限制）获取标签列表
     * @param prefix
     * @return
     */
    ApiResult getTagListByPrefix(String prefix);

    /**
     * 标签关键字搜索
     * @param pageParams
     * @param keyWords
     * @return
     */
    ApiResult getSearchList(PageParams pageParams, String keyWords);

    /**
     * 更新tagVoRepository的后门接口
     * @return
     */
    ApiResult tagVoRepositoryReload();

    /**
     * 更新所有标签的caseNumber字段
     * @return
     */
    // ApiResult updateTagCaseNumber();

    /**
     * 根据id获取tagVo
     * @param id
     * @return
     */
    ApiResult findTagVoById(Long id);

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
}
