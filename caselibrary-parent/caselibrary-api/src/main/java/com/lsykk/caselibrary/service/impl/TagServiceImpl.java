package com.lsykk.caselibrary.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsykk.caselibrary.dao.mapper.CaseTagMapper;
import com.lsykk.caselibrary.dao.mapper.TagMapper;
import com.lsykk.caselibrary.dao.pojo.CaseHeader;
import com.lsykk.caselibrary.dao.pojo.CaseTag;
import com.lsykk.caselibrary.dao.pojo.Tag;
import com.lsykk.caselibrary.service.TagService;
import com.lsykk.caselibrary.vo.*;
import com.lsykk.caselibrary.vo.params.PageParams;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TagServiceImpl implements TagService {
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private CaseTagMapper caseTagMapper;

    @Override
    public ApiResult getTagList(PageParams pageParams, Tag tag){
        /* 分页查询 tag数据库表 */
        Page<Tag> page = new Page<>(pageParams.getPage(), pageParams.getPageSize());
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        /* 动态SQL语句 */
        queryWrapper.eq(tag.getId()!=null, Tag::getId, tag.getId());
        queryWrapper.like(StringUtils.isNotBlank(tag.getName()), Tag::getName, tag.getName());
        /* 按照ID顺序排序 */
        queryWrapper.orderByAsc(Tag::getId);
        Page<Tag> tagPage = tagMapper.selectPage(page, queryWrapper);
        List<Tag> tagList = tagPage.getRecords();
        return ApiResult.success(tagList);
    }

    @Override
    public ApiResult getTagVoList(PageParams pageParams, Long id, String name){
        /* 分页查询 tag数据库表 */
        Page<Tag> page = new Page<>(pageParams.getPage(), pageParams.getPageSize());
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        /* 动态SQL语句 */
        queryWrapper.eq(id!=null, Tag::getId, id);
        queryWrapper.like(StringUtils.isNotBlank(name), Tag::getName, name);
        /* 按照ID顺序排序 */
        queryWrapper.orderByAsc(Tag::getId);
        Page<Tag> tagPage = tagMapper.selectPage(page, queryWrapper);
        PageVo<TagVo> pageVo = new PageVo();
        pageVo.setRecordList(copyList(tagPage.getRecords()));
        pageVo.setTotal(tagPage.getTotal());
        return ApiResult.success(pageVo);
    }

    @Override
    public ApiResult getTagListByPrefix(String prefix){
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        /* 动态SQL语句 */
        queryWrapper.likeLeft(StringUtils.isNotBlank(prefix), Tag::getName, prefix);
        queryWrapper.orderByAsc(Tag::getId);
        List<Tag> tagList = tagMapper.selectList(queryWrapper);
        return ApiResult.success(tagList);
    }

    @Override
    public Tag findTagById(Long id){
        return tagMapper.selectById(id);
    }

    @Override
    public ApiResult insertTag(Tag tag){
        if (StringUtils.isBlank(tag.getName())){
            return ApiResult.fail(ErrorCode.PARAMS_ERROR.getCode(), ErrorCode.PARAMS_ERROR.getMsg());
        }
        tag.setStatus(1);
        tagMapper.insert(tag);
        return ApiResult.success();
    }

    @Override
    public ApiResult updateTag(Tag tag){
        if (tag.getId()==null || StringUtils.isBlank(tag.getName())){
            return ApiResult.fail(ErrorCode.PARAMS_ERROR.getCode(), ErrorCode.PARAMS_ERROR.getMsg());
        }
        tagMapper.updateById(tag);
        return ApiResult.success();
    }

    @Override
    public List<CaseTagVo> findCaseTagVoByCaseId(Long caseId){
        LambdaQueryWrapper<CaseTag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CaseTag::getCaseId, caseId);
        List<CaseTag> list = caseTagMapper.selectList(queryWrapper);
        return copyCaseTagList(list);
    }

    @Override
    public void updateCaseTagByCaseId(List<Long> oldList, List<Long> newList, Long caseId){
        Collections.sort(oldList);
        Collections.sort(newList);
        if (oldList.equals(newList)){
            return;
        }
        int i = 0;
        for (Long id : newList){
            while (i < oldList.size() && id > oldList.get(i)){
                // 旧的匹配不到，删掉
                Map<String,Object> mp = new HashMap<>();
                mp.put("tag_id", oldList.get(i));
                mp.put("case_id", caseId);
                caseTagMapper.deleteByMap(mp);
                ++i;
            }
            if (i < oldList.size() && id.equals(oldList.get(i))){
                // 匹配上了，继续
                ++i;
            }
            else {
                // 新的匹配不到，插入
                CaseTag caseTag = new CaseTag();
                caseTag.setTagId(id);
                caseTag.setCaseId(caseId);
                caseTagMapper.insert(caseTag);
            }
        }
    }

    @Override
    public List<TagVo> findTagVoByCaseId(Long caseId){
        List<Tag> list = tagMapper.findTagsByCaseId(caseId);
        return copyList(list);
    }

    @Override
    public ApiResult updateTagByCaseId(List<Long> tagIds, Long caseId){
        return ApiResult.success();
    }

    private List<TagVo> copyList(List<Tag> tagList){
        List<TagVo> tagVoList = new ArrayList<>();
        for (Tag tag : tagList) {
            tagVoList.add(copy(tag));
        }
        return tagVoList;
    }

    private TagVo copy(Tag tag){
        TagVo tagVo = new TagVo();
        BeanUtils.copyProperties(tag, tagVo);
        return tagVo;
    }

    private List<CaseTagVo> copyCaseTagList(List<CaseTag> caseTagList){
        List<CaseTagVo> caseTagVoList = new ArrayList<>();
        for (CaseTag caseTag : caseTagList) {
            caseTagVoList.add(copyCaseTag(caseTag));
        }
        return caseTagVoList;
    }

    private CaseTagVo copyCaseTag(CaseTag caseTag){
        CaseTagVo caseTagVo = new CaseTagVo();
        BeanUtils.copyProperties(caseTag, caseTagVo);
        return caseTagVo;
    }

    private CaseTag copyCaseTagBack(CaseTagVo caseTagVo, Long caseId){
        CaseTag caseTag = new CaseTag();
        BeanUtils.copyProperties(caseTagVo, caseTag);
        caseTag.setCaseId(caseId);
        caseTag.setStatus(1);
        return caseTag;
    }
}
