package com.lsykk.caselibrary.service;

import com.lsykk.caselibrary.dao.pojo.Tag;
import com.lsykk.caselibrary.vo.ApiResult;
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
     * 根据案例id，找到其对应的所有标签
     * @param caseId
     * @return
     */
    List<TagVo> findTagsByCaseId(Long caseId);
}
