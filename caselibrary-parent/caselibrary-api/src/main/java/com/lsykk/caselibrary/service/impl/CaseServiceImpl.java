package com.lsykk.caselibrary.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsykk.caselibrary.dao.mapper.CaseBodyMapper;
import com.lsykk.caselibrary.dao.mapper.CaseHeaderMapper;
import com.lsykk.caselibrary.dao.mapper.CaseTagMapper;
import com.lsykk.caselibrary.dao.pojo.CaseHeader;
import com.lsykk.caselibrary.dao.pojo.CaseBody;
import com.lsykk.caselibrary.dao.pojo.User;
import com.lsykk.caselibrary.dao.repository.CaseHeaderVoRepository;
import com.lsykk.caselibrary.service.*;
import com.lsykk.caselibrary.utils.DateUtils;
import com.lsykk.caselibrary.vo.*;
import com.lsykk.caselibrary.vo.params.CaseParam;
import com.lsykk.caselibrary.vo.params.PageParams;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CaseServiceImpl implements CaseService {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Autowired
    private ThreadService threadService;
    @Autowired
    private CaseHeaderVoRepository caseHeaderVoRepository;
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;
    @Autowired
    private CaseHeaderMapper caseHeaderMapper;
    @Autowired
    private CaseBodyMapper caseBodyMapper;
    @Autowired
    private CaseTagMapper caseTagMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private TagService tagService;
    @Autowired
    private FileService fileService;
    @Autowired
    private CommentService commentService;

    @Override
    public ApiResult getCaseHeaderVoList(PageParams pageParams, Long id, Long authorId, Integer visible,
                                         Integer state, boolean isBody, boolean isComment){
        //分页查询 case数据库表
        Page<CaseHeader> page = new Page<>(pageParams.getPage(), pageParams.getPageSize());
        LambdaQueryWrapper<CaseHeader> queryWrapper = new LambdaQueryWrapper<>();
        if (id != null){
            queryWrapper.eq(CaseHeader::getId, id);
        }
        else {
            queryWrapper.eq(CaseHeader::getStatus, 1);
            queryWrapper.eq(state!=null, CaseHeader::getState, state);
            queryWrapper.eq(visible!=null, CaseHeader::getVisible, visible);
            queryWrapper.eq(authorId!=null, CaseHeader::getAuthorId, authorId);
        }
        // 按照id（即发布时间）倒叙排序
        queryWrapper.orderByDesc(CaseHeader::getId);
        Page<CaseHeader> casePage = caseHeaderMapper.selectPage(page, queryWrapper);
        PageVo<CaseHeaderVo> pageVo = new PageVo();
        pageVo.setRecordList(copyList(casePage.getRecords(), isBody, isComment));
        pageVo.setTotal(casePage.getTotal());
        return ApiResult.success(pageVo);
    }

    @Override
    public ApiResult getCaseHeaderList(PageParams pageParams, Long id, Long authorId, Integer visible,
                                       Integer state, Integer status){
        //分页查询 case数据库表
        Page<CaseHeader> page = new Page<>(pageParams.getPage(), pageParams.getPageSize());
        LambdaQueryWrapper<CaseHeader> queryWrapper = new LambdaQueryWrapper<>();
        if (id != null){
            queryWrapper.eq(CaseHeader::getId, id);
        }
        else {
            queryWrapper.eq(status!=null, CaseHeader::getStatus, status);
            queryWrapper.eq(state!=null, CaseHeader::getState, state);
            queryWrapper.eq(visible!=null, CaseHeader::getVisible, visible);
            queryWrapper.eq(authorId!=null, CaseHeader::getAuthorId, authorId);
        }
        // 按照id（即发布时间）倒叙排序
        queryWrapper.orderByDesc(CaseHeader::getId);
        Page<CaseHeader> casePage = caseHeaderMapper.selectPage(page, queryWrapper);
        PageVo<CaseHeader> pageVo = new PageVo();
        pageVo.setRecordList(casePage.getRecords());
        pageVo.setTotal(casePage.getTotal());
        return ApiResult.success(pageVo);
    }

    @Override
    public ApiResult getHotList(PageParams pageParams){
        QueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery("visible",1))
                .filter(QueryBuilders.termQuery("state",1))
                .filter(QueryBuilders.termQuery("status",1));
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder()
                .withPageable(PageRequest.of(pageParams.getPage()-1, pageParams.getPageSize()))
                .withQuery(queryBuilder)
                .withSort(SortBuilders.fieldSort("hot").order(SortOrder.DESC));
        NativeSearchQuery searchQuery = searchQueryBuilder.build();
        SearchHits<CaseHeaderVo> search = elasticsearchRestTemplate.search(searchQuery, CaseHeaderVo.class);
        List<SearchHit<CaseHeaderVo>> searchHits = search.getSearchHits();
        List<CaseHeaderVo> caseHeaderVoList = new ArrayList<>();
        for (SearchHit<CaseHeaderVo> searchHit : searchHits) {
            caseHeaderVoList.add(searchHit.getContent());
        }
        PageVo<CaseHeaderVo> pageVo = new PageVo();
        pageVo.setRecordList(caseHeaderVoList);
        pageVo.setTotal(search.getTotalHits());
        return ApiResult.success(pageVo);
    }

    @Override
    public ApiResult getSearchList(PageParams pageParams, String keyWords, String type){
        // 查询条件
        QueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.boolQuery()
                    .should(QueryBuilders.matchQuery("title", keyWords))
                    .should(QueryBuilders.matchQuery("summary", keyWords))
                    .should(QueryBuilders.nestedQuery(
                            "tags",
                            QueryBuilders.matchQuery("tags.name", keyWords),
                            ScoreMode.Total))
                    .should(QueryBuilders.nestedQuery(
                            "author",
                            QueryBuilders.matchQuery("author.username", keyWords),
                            ScoreMode.Total)))
                .filter(QueryBuilders.termQuery("visible",1))
                .filter(QueryBuilders.termQuery("state",1))
                .filter(QueryBuilders.termQuery("status",1));
        //查询构建器
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder()
                // 分页设置
                .withPageable(PageRequest.of(pageParams.getPage()-1, pageParams.getPageSize()))
                // 查询条件
                .withQuery(queryBuilder)
                // 添加高亮显示字段
                .withHighlightFields(
                        new HighlightBuilder.Field("title"),
                        new HighlightBuilder.Field("summary"))
                // 高亮显示颜色
                .withHighlightBuilder(new HighlightBuilder().preTags("<span style='color:red'>").postTags("</span>"));
        switch (type) {
            case "hot":
                // 热度值倒排
                searchQueryBuilder.withSort(SortBuilders.fieldSort("hot").order(SortOrder.DESC));
                break;
            case "new":
                // 序号（即日期）倒排
                searchQueryBuilder.withSort(SortBuilders.fieldSort("id").order(SortOrder.DESC));
                break;
            case "view":
                // 点击数倒排
                searchQueryBuilder.withSort(SortBuilders.fieldSort("viewtimes").order(SortOrder.DESC));
                break;
            case "thumb":
                // 点赞数倒排
                searchQueryBuilder.withSort(SortBuilders.fieldSort("thumb").order(SortOrder.DESC));
                break;
            case "favorites":
                // 收藏数倒排
                searchQueryBuilder.withSort(SortBuilders.fieldSort("favorites").order(SortOrder.DESC));
                break;
        }
        NativeSearchQuery searchQuery = searchQueryBuilder.build();
        // 查询
        SearchHits<CaseHeaderVo> search = elasticsearchRestTemplate.search(searchQuery, CaseHeaderVo.class);
        // 查询结果（分页，只是一部分）
        List<SearchHit<CaseHeaderVo>> searchHits = search.getSearchHits();
        // 用于存放返回值的列表
        List<CaseHeaderVo> caseHeaderVoList = new ArrayList<>();
        // 遍历，高亮处理
        for (SearchHit<CaseHeaderVo> searchHit : searchHits) {
            // 高亮的内容
            Map<String, List<String>> highlightFields = searchHit.getHighlightFields();
            //将高亮的内容填充到content中
            searchHit.getContent().setTitle(
                    highlightFields.get("title") == null ? searchHit.getContent().getTitle() : highlightFields.get("title").get(0));
            searchHit.getContent().setSummary(
                    highlightFields.get("summary") == null ? searchHit.getContent().getSummary() : highlightFields.get("summary").get(0));
            //放到实体类中
            caseHeaderVoList.add(searchHit.getContent());
        }
        PageVo<CaseHeaderVo> pageVo = new PageVo();
        pageVo.setRecordList(caseHeaderVoList);
        pageVo.setTotal(search.getTotalHits());
        return ApiResult.success(pageVo);
    }

    @Override
    public ApiResult caseHeaderVoRepositoryReload(){
        //分页查询 case数据库表
        LambdaQueryWrapper<CaseHeader> queryWrapper = new LambdaQueryWrapper<>();
        List<CaseHeader> caseList = caseHeaderMapper.selectList(queryWrapper);
        caseHeaderVoRepository.deleteAll();
        for (CaseHeader caseHeader: caseList){
            caseHeaderVoRepository.save(copy(caseHeader, false, false));
        }
        return ApiResult.success();
    }

    @Override
    public ApiResult getListByTagId(PageParams pageParams, Long tagId){
        Page page = new Page(pageParams.getPage(), pageParams.getPageSize());
        Page<CaseHeader> casePage = caseHeaderMapper.findCasesByTagId(page, tagId);
        PageVo<CaseHeaderVo> pageVo = new PageVo();
        pageVo.setRecordList(copyList(casePage.getRecords(), false, false));
        pageVo.setTotal(casePage.getTotal());
        return ApiResult.success(pageVo);
    }

    @Override
    public ApiResult getListByFavoritesId(PageParams pageParams, Long favoritesId){
        Page page = new Page(pageParams.getPage(), pageParams.getPageSize());
        Page<CaseHeader> casePage = caseHeaderMapper.findCasesByFavoritesId(page, favoritesId);
        PageVo<CaseHeaderVo> pageVo = new PageVo();
        pageVo.setRecordList(copyList(casePage.getRecords(), false, false));
        pageVo.setTotal(casePage.getTotal());
        return ApiResult.success(pageVo);
    }

    @Override
    public ApiResult getCaseParamById(Long caseId){
        CaseHeader caseHeader;
        CaseBodyVo caseBodyVoLatest;
        CaseBody caseBody = null;
        List<Long> tags = new ArrayList<>();
        if (caseId == null){
            // 首次创建的情况，返回一个空的header,body 和 tags
            // 这个逻辑前端做了
            caseHeader = new CaseHeader();
            caseHeader.setState(0);
            caseBodyVoLatest = new CaseBodyVo();
            caseBodyVoLatest.setState(0);
            caseBodyVoLatest.setVersion(0);
        }
        else {
            caseHeader = caseHeaderMapper.selectById(caseId);
            CaseBody caseBodyLatest = caseBodyMapper.findCaseBodyLatestByCaseId(caseId);
            caseBodyVoLatest = copy(caseBodyLatest);
            if (caseBodyLatest.getState() == 0){
                // 若最新的是草稿，额外提供最新的发布版本
                caseBody = caseBodyMapper.findCaseBodyByCaseId(caseId);
            }
            List<TagVo> tagList = tagService.findTagVoByCaseId(caseId);
            tags = tagList.stream()
                    .map(TagVo::getId)
                    .collect(Collectors.toList());
        }
        // 构建返回参数
        CaseParam caseParam = new CaseParam();
        caseParam.setCaseHeader(caseHeader);
        caseParam.setCaseBodyVoLatest(caseBodyVoLatest);
        caseParam.setCaseBodyVo(copy(caseBody));
        caseParam.setOldTags(tags);
        caseParam.setNewTags(tags);
        return ApiResult.success(caseParam);
    }

    @Override
    public ApiResult submitCaseParam(CaseParam caseParam){
        // 前端设置好state
        CaseHeader caseHeader = caseParam.getCaseHeader();
        CaseBodyVo caseBodyVoLatest = caseParam.getCaseBodyVoLatest();
        List<Long> oldTags = caseParam.getOldTags();
        List<Long> newTags = caseParam.getNewTags();
        // 检查参数
        if (StringUtils.isBlank(caseHeader.getTitle()) ||
                caseHeader.getAuthorId() == null ||
                caseHeader.getVisible() == null ||
                caseHeader.getState() == null ||
                caseBodyVoLatest.getState() == null ||
                caseBodyVoLatest.getVersion() == null){
            return ApiResult.fail(ErrorCode.PARAMS_ERROR);
        }
        caseHeader.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        // 首次发布的情况，先插入header
        if (caseHeader.getId() == null){
            caseHeader.setComment(0);
            caseHeader.setThumb(0);
            caseHeader.setViewtimes(0);
            caseHeader.setStatus(1);
            caseHeader.setCreateTime(new Timestamp(System.currentTimeMillis()));
            caseHeaderMapper.insertAndGetId(caseHeader);
            User user = userService.findUserById(caseHeader.getAuthorId());
            user.setCaseNumber(user.getCaseNumber() + 1);
            userService.update(user);
            if (caseHeader.getId() == null){
                return ApiResult.fail(ErrorCode.DATABASE_INSERT_ERROR);
            }
        }
        else {
            caseHeaderMapper.updateById(caseHeader);
        }
        // caseBodyVoLatest转caseBodyLatest，注意caseId
        CaseBody caseBodyLatest = copyBack(caseBodyVoLatest, caseHeader.getId());
        // 下面的更新是一整个事务
        // 只有caseBodyLatest是需要更新的
        if (caseBodyLatest.getId() == null){
            caseBodyMapper.insert(caseBodyLatest);
        }
        else {
            caseBodyMapper.updateById(caseBodyLatest);
        }
        // 最后是tag的添加
        tagService.updateCaseTagByCaseId(oldTags, newTags, caseHeader.getId());
        // ElasticSearch保存
        caseHeaderVoRepository.save(copy(caseHeader, false, false));
        // 返回案例id
        return ApiResult.success(caseHeader.getId());
    }

    @Override
    public CaseHeaderVo getCaseHeaderVoById(Long id, boolean isBody, boolean isComment){
        CaseHeader caseHeader = getCaseHeaderById(id);
        CaseHeaderVo caseHeaderVo = copy(caseHeader, isBody, isComment);
        threadService.updateCaseViewtimes(id, 1);
        return caseHeaderVo;
    }

    @Override
    public CaseHeader getCaseHeaderById(Long id){
        return caseHeaderMapper.selectById(id);
    }

    @Override
    public ApiResult updateCaseHeader(CaseHeader caseHeader){
        if (caseHeader.getId() == null){
            return ApiResult.fail(ErrorCode.PARAMS_ERROR);
        }
        caseHeader.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        caseHeaderMapper.updateById(caseHeader);
        CaseHeader newCaseHeader = getCaseHeaderById(caseHeader.getId());
        caseHeaderVoRepository.save(copy(newCaseHeader, false, false));
        return ApiResult.success();
    }
    private List<CaseHeaderVo> copyList(List<CaseHeader> list, boolean isBody, boolean isComment){
        List<CaseHeaderVo> caseHeaderVoList = new ArrayList<>();
        for (CaseHeader caseHeader : list) {
            caseHeaderVoList.add(copy(caseHeader, isBody, isComment));
        }
        return caseHeaderVoList;
    }

    private CaseHeaderVo copy(CaseHeader caseHeader, boolean isBody, boolean isComment){
        CaseHeaderVo caseHeaderVo = new CaseHeaderVo();
        BeanUtils.copyProperties(caseHeader, caseHeaderVo);
        if (caseHeader.getCreateTime() != null){
            caseHeaderVo.setCreateTime(DateUtils.getTime(caseHeader.getCreateTime()));
        }
        if (caseHeader.getUpdateTime() != null){
            caseHeaderVo.setUpdateTime(DateUtils.getTime(caseHeader.getUpdateTime()));
        }
        if (isBody){
            caseHeaderVo.setCaseBody(findCaseBodyById(caseHeader.getId()));
        }
        if (isComment){
            caseHeaderVo.setComments(commentService.getCommentVoListByCaseId(caseHeader.getId()));
        }
        caseHeaderVo.setAuthor(userService.findUserVoById(caseHeader.getAuthorId()));
        caseHeaderVo.setTags(tagService.findTagVoByCaseId(caseHeader.getId()));
        return caseHeaderVo;
    }

    private CaseBodyVo findCaseBodyById(Long caseId) {
        CaseBody caseBody = caseBodyMapper.findCaseBodyByCaseId(caseId);
        return copy(caseBody);
    }

    private List<CaseBodyVo> copyList(List<CaseBody> list){
        List<CaseBodyVo> caseBodyVoList = new ArrayList<>();
        for (CaseBody caseBody : list) {
            caseBodyVoList.add(copy(caseBody));
        }
        return caseBodyVoList;
    }

    private CaseBodyVo copy(CaseBody caseBody){
        if (caseBody == null){
            return null;
        }
        CaseBodyVo caseBodyVo = new CaseBodyVo();
        BeanUtils.copyProperties(caseBody, caseBodyVo);
        caseBodyVo.setAppendixList(fileService.getFileVoByString(caseBody.getAppendix()));
        if (caseBody.getCreateTime() != null ){
            caseBodyVo.setCreateTime(DateUtils.getTime(caseBody.getCreateTime()));
        }
        if (caseBody.getUpdateTime() != null){
            caseBodyVo.setUpdateTime(DateUtils.getTime(caseBody.getUpdateTime()));
        }
        return caseBodyVo;
    }

    private CaseBody copyBack(CaseBodyVo caseBodyVo, Long caseId){
        if (caseBodyVo == null){
            return null;
        }
        CaseBody caseBody = new CaseBody();
        BeanUtils.copyProperties(caseBodyVo, caseBody);
        List<String> list = caseBodyVo.getAppendixList()
                .stream()
                .map(FileVo::getUrl)
                .collect(Collectors.toList());
        caseBody.setCaseId(caseId);
        caseBody.setAppendix(String.join(";", list));
        caseBody.setStatus(1);
        // 不需要处理time，这个会自动更新
        return caseBody;
    }
}
