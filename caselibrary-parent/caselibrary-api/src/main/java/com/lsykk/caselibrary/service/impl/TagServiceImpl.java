package com.lsykk.caselibrary.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsykk.caselibrary.dao.mapper.CaseTagMapper;
import com.lsykk.caselibrary.dao.mapper.TagMapper;
import com.lsykk.caselibrary.dao.pojo.CaseTag;
import com.lsykk.caselibrary.dao.pojo.Tag;
import com.lsykk.caselibrary.dao.repository.TagVoRepository;
import com.lsykk.caselibrary.service.TagService;
import com.lsykk.caselibrary.utils.DateUtils;
import com.lsykk.caselibrary.vo.*;
import com.lsykk.caselibrary.vo.params.PageParams;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TagServiceImpl implements TagService {
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private TagVoRepository tagVoRepository;
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;
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
    public ApiResult getSearchList(PageParams pageParams, String keyWords){
        QueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.boolQuery()
                        .should(QueryBuilders.matchQuery("name", keyWords))
                        .should(QueryBuilders.matchQuery("description", keyWords)))
                .filter(QueryBuilders.termQuery("status",1));
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder()
                .withPageable(PageRequest.of(pageParams.getPage()-1, pageParams.getPageSize()))
                .withQuery(queryBuilder)
                .withHighlightFields(
                        new HighlightBuilder.Field("name"),
                        new HighlightBuilder.Field("description"))
                .withHighlightBuilder(new HighlightBuilder().preTags("<span style='color:red'>").postTags("</span>"));
        NativeSearchQuery searchQuery = searchQueryBuilder.build();
        SearchHits<TagVo> search = elasticsearchRestTemplate.search(searchQuery, TagVo.class);
        List<SearchHit<TagVo>> searchHits = search.getSearchHits();
        List<TagVo> tagVoList = new ArrayList<>();
        for (SearchHit<TagVo> searchHit : searchHits) {
            Map<String, List<String>> highlightFields = searchHit.getHighlightFields();
            searchHit.getContent().setName(
                    highlightFields.get("name") == null ? searchHit.getContent().getName() : highlightFields.get("name").get(0));
            searchHit.getContent().setDescription(
                    highlightFields.get("description") == null ? searchHit.getContent().getDescription() : highlightFields.get("description").get(0));
            tagVoList.add(searchHit.getContent());
        }
        PageVo<TagVo> pageVo = new PageVo();
        pageVo.setRecordList(tagVoList);
        pageVo.setTotal(search.getTotalHits());
        return ApiResult.success(pageVo);
    }

    @Override
    public ApiResult tagVoRepositoryReload(){
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        List<Tag> tagList = tagMapper.selectList(queryWrapper);
        for (Tag tag: tagList){
            tagVoRepository.save(copy(tag));
        }
        return ApiResult.success();
    }

    @Override
    public ApiResult findTagVoById(Long id){
        return ApiResult.success(copy(findTagById(id)));
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
        if (tag.getCreateTime() != null ){
            tagVo.setCreateTime(DateUtils.getTime(tag.getCreateTime()));
        }
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
