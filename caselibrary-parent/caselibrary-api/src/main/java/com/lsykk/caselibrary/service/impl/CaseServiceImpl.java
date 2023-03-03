package com.lsykk.caselibrary.service.impl;

import com.UpYun;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsykk.caselibrary.dao.mapper.CaseBodyMapper;
import com.lsykk.caselibrary.dao.mapper.CaseHeaderMapper;
import com.lsykk.caselibrary.dao.mapper.CaseTagMapper;
import com.lsykk.caselibrary.dao.pojo.CaseHeader;
import com.lsykk.caselibrary.dao.pojo.CaseBody;
import com.lsykk.caselibrary.dao.repository.CaseHeaderRepository;
import com.lsykk.caselibrary.service.CaseService;
import com.lsykk.caselibrary.service.TagService;
import com.lsykk.caselibrary.service.UserService;
import com.lsykk.caselibrary.utils.DateUtils;
import com.lsykk.caselibrary.vo.*;
import com.lsykk.caselibrary.vo.params.CaseParam;
import com.lsykk.caselibrary.vo.params.PageParams;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CaseServiceImpl implements CaseService {

    @Autowired
    private CaseHeaderRepository caseHeaderRepository;
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

    @Override
    public ApiResult getCaseHeaderVoList(PageParams pageParams, Long id, Long authorId, Integer visible,
                                         Integer state, Integer status, boolean isBody, boolean isComment){
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
        List<CaseHeader> caseHeaderList = casePage.getRecords();
        return ApiResult.success(copyList(caseHeaderList, isBody, isComment));
    }


    @Override
    public ApiResult getOtherAuthorList(PageParams pageParams, Long userId, boolean isBody, boolean isComment){
        //分页查询 case数据库表
        Page<CaseHeader> page = new Page<>(pageParams.getPage(), pageParams.getPageSize());
        LambdaQueryWrapper<CaseHeader> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CaseHeader::getStatus, 1);
        queryWrapper.eq(CaseHeader::getState, 3);
        queryWrapper.eq(CaseHeader::getVisible, 1);
        queryWrapper.eq(userId!=null, CaseHeader::getAuthorId, userId);
        // 按照id倒序排序
        queryWrapper.orderByDesc(CaseHeader::getId);
        Page<CaseHeader> casePage = caseHeaderMapper.selectPage(page, queryWrapper);
        List<CaseHeader> caseHeaderList = casePage.getRecords();
        return ApiResult.success(copyList(caseHeaderList, isBody, isComment));
    }

    @Override
    public ApiResult getMyList(PageParams pageParams, Long userId, Integer visible, Integer state, boolean isBody, boolean isComment){
        //分页查询 case数据库表
        Page<CaseHeader> page = new Page<>(pageParams.getPage(), pageParams.getPageSize());
        LambdaQueryWrapper<CaseHeader> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CaseHeader::getStatus, 1);
        queryWrapper.eq(state!=null, CaseHeader::getState, state);
        queryWrapper.eq(userId!=null, CaseHeader::getAuthorId, userId);
        // 按照id倒序排序
        queryWrapper.orderByDesc(CaseHeader::getId);
        Page<CaseHeader> casePage = caseHeaderMapper.selectPage(page, queryWrapper);
        List<CaseHeader> caseHeaderList = casePage.getRecords();
        return ApiResult.success(copyList(caseHeaderList, isBody, isComment));
    }

    @Override
    public ApiResult getSearchList(PageParams pageParams, String keyWords){
        // 查询条件
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .should(QueryBuilders.matchQuery("title", keyWords))
                .should(QueryBuilders.matchQuery("summary", keyWords))
                .filter(QueryBuilders.termQuery("visible",1))
                .filter(QueryBuilders.termQuery("state",3))
                .filter(QueryBuilders.termQuery("status",1));
        //查询构建器
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                // 分页设置
                .withPageable(PageRequest.of(pageParams.getPage(),pageParams.getPageSize()))
                // 日期倒排
                .withSort(SortBuilders.fieldSort("updateTime").order(SortOrder.DESC))
                // 查询条件
                .withQuery(queryBuilder)
                // 添加高亮显示字段
                .withHighlightFields(
                        new HighlightBuilder.Field("title"),
                        new HighlightBuilder.Field("summary"))
                // 高亮显示颜色
                .withHighlightBuilder(new HighlightBuilder().preTags("<span style='color:red'>").postTags("</span>"))
                .build();
        // 查询
        SearchHits<CaseHeader> search = elasticsearchRestTemplate.search(searchQuery, CaseHeader.class);
        // 查询结果（分页，只是一部分）
        List<SearchHit<CaseHeader>> searchHits = search.getSearchHits();
        // 用于存放返回值的列表
        List<CaseHeader> caseHeaderList = new ArrayList<>();
        // 遍历，高亮处理
        for (SearchHit<CaseHeader> searchHit : searchHits) {
            // 高亮的内容
            Map<String, List<String>> highlightFields = searchHit.getHighlightFields();
            //将高亮的内容填充到content中
            searchHit.getContent().setTitle(
                    highlightFields.get("title") == null ? searchHit.getContent().getTitle() : highlightFields.get("title").get(0));
            searchHit.getContent().setSummary(
                    highlightFields.get("summary") == null ? searchHit.getContent().getSummary() : highlightFields.get("summary").get(0));
            //放到实体类中
            caseHeaderList.add(searchHit.getContent());
        }
        return ApiResult.success(copyList(caseHeaderList, false, false));
    }

    @Override
    public List<CaseHeader> getCasesByFavoritesId(Long favoritesId){
        //return caseHeaderMapper.findCasesByFavoritesId(favoritesId);
        return null;
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
        // header更新时间置为空
        caseHeader.setUpdateTime(null);
        // 首次发布的情况，先插入header
        if (caseHeader.getId() == null){
            caseHeaderMapper.insertAndGetId(caseHeader);
            if (caseHeader.getId() == null){
                return ApiResult.fail(ErrorCode.DATABASE_INSERT_ERROR);
            }
        }
        else {
            caseHeaderMapper.updateById(caseHeader);
        }
        // ElasticSearch保存
        caseHeaderRepository.save(caseHeader);
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
        // 返回案例id
        return ApiResult.success(caseHeader.getId());
    }

    @Override
    public CaseHeaderVo getCaseHeaderVoById(Long id, boolean isBody, boolean isComment){
        CaseHeader caseHeader = getCaseHeaderById(id);
        return copy(caseHeader, isBody, isComment);
    }

    @Override
    public CaseHeader getCaseHeaderById(Long id){
        return caseHeaderMapper.selectById(id);
    }

    @Override
    public ApiResult insertCaseHeader(CaseHeader caseHeader){
        if (StringUtils.isBlank(caseHeader.getTitle()) ||
                caseHeader.getAuthorId() == null ||
                caseHeader.getVisible() == null){
            return ApiResult.fail(ErrorCode.PARAMS_ERROR);
        }
        caseHeader.setStatus(1);
        caseHeaderMapper.insertAndGetId(caseHeader);
        // ES
        caseHeaderRepository.save(caseHeader);
        // 返回当前caseheader的Id
        return ApiResult.success(caseHeader.getId());
    }

    @Override
    public ApiResult updateCaseHeader(CaseHeader caseHeader){
        if (caseHeader.getId() == null){
            return ApiResult.fail(ErrorCode.PARAMS_ERROR);
        }
        caseHeaderMapper.updateById(caseHeader);
        caseHeaderRepository.save(caseHeader);
        return ApiResult.success();
    }

    @Override
    public ApiResult insertCaseBody(CaseBody caseBody){
        if (caseBody.getCaseId() == null){
            return ApiResult.fail(ErrorCode.PARAMS_ERROR);
        }
        caseBody.setStatus(1);
        caseBodyMapper.insert(caseBody);
        return ApiResult.success();
    }

    @Override
    public ApiResult updateCaseBody(CaseBody caseBody){
        if (caseBody.getCaseId() == null){
            return ApiResult.fail(ErrorCode.PARAMS_ERROR);
        }
        caseBody.setStatus(1);
        caseBodyMapper.updateById(caseBody);
        return ApiResult.success();
    }

    @Override
    public ApiResult getCaseBodyByCaseId(Long caseId){
        CaseBody caseBody = caseBodyMapper.findCaseBodyByCaseId(caseId);
        return ApiResult.success(caseBody);
    }

    @Override
    public ApiResult uploadFile(MultipartFile file) {
        // 获取文件原本的名字
        String originName = file.getOriginalFilename();
        if (StringUtils.isBlank(originName)){
            // 上传的文件类型错误
            return ApiResult.fail(ErrorCode.File_Upload_Illegal);
        }
        // set保存可接受的文件类型（后缀）
        Set<String> set = new HashSet<>();
        set.add(".pdf");
        set.add(".doc");
        set.add(".docx");
        set.add(".ppt");
        set.add(".rar");
        set.add(".zip");
        set.add(".gif");
        set.add(".jpg");
        set.add(".png");
        // 获取文件后缀("."以及后面的内容)
        String fileType = originName.substring(originName.lastIndexOf("."));
        if (!set.contains(fileType)){
            // 上传的文件类型错误
            return ApiResult.fail(ErrorCode.File_Upload_Illegal);
        }
        // 又拍云实例构造
        UpYun upYun = new UpYun("case-lib","kkysl", "Ms69o6Q8cI6spo8zscbu35ukL1z5nGI5");
        String savePath;
        if (fileType.equals(".jpg") || fileType.equals(".png") || fileType.equals(".gif")){
            // 使用唯一的UUID作为图片名称
            savePath = "images/" + UUID.randomUUID() + fileType;
        }
        else {
            // 若不是图片，使用原本的名称，保存在UUID收藏夹中
            savePath = "files/" + UUID.randomUUID() + "/" + originName;
        }
        boolean res = false;
        try {
            res = upYun.writeFile(savePath, file.getBytes(), false);
        } catch (Exception e) {
            e.printStackTrace();
            ApiResult.fail(ErrorCode.File_Upload_Error);
        }
        if (res){
            String url = "http://case-lib.test.upcdn.net/" + savePath;
            return ApiResult.success(url);
        }
        return ApiResult.fail(ErrorCode.File_Upload_Error);
    }

    @Override
    public String exportMarkdownFile(String content) {
        // 以yyyyMMdd格式获取当前时间
        String format = DateUtils.getTimeSimple();
        String fileName = UUID.randomUUID() + "-" + format + ".md";
        // 保存路径。默认定位到的当前用户目录("user.dir")（即工程根目录），为每一天创建一个文件夹
        String savePath = System.getProperty("user.dir") + "\\" + "files" + "\\" + "markdown";
        // 保存文件的文件夹
        File folder = new File(savePath);
        // 判断文件夹是否存在，不存在则创建文件夹。若创建文件夹失败，返回失败信息。
        if (!folder.exists() && !folder.mkdirs()){
            ApiResult.fail(ErrorCode.File_Upload_Error);
        }
        String filepath = savePath + "\\" + fileName;
        try (FileWriter fileWriter = new FileWriter(filepath)) {
            fileWriter.write(content);
        }
        catch (Exception exception){
            System.out.println("Error: " + exception.getMessage());
        }
        return filepath;
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
        caseHeaderVo.setCreateTime(DateUtils.getTime(caseHeader.getCreateTime()));
        caseHeaderVo.setUpdateTime(DateUtils.getTime(caseHeader.getUpdateTime()));
        if (isBody){
            caseHeaderVo.setCaseBody(findCaseBodyById(caseHeader.getId()));
        }
        if (isComment){
            //caseHeaderVo.setCaseBody(findCaseBodyById(caseHeader.getId()));
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
        if (StringUtils.isNotBlank(caseBody.getAppendix())){
            List<String> list = Arrays.asList(caseBody.getAppendix().split(";"));
            caseBodyVo.setAppendixList(list);
        }
        else {
            caseBodyVo.setAppendixList(null);
        }
        caseBodyVo.setCreateTime(DateUtils.getTime(caseBody.getCreateTime()));
        caseBodyVo.setUpdateTime(DateUtils.getTime(caseBody.getUpdateTime()));

        return caseBodyVo;
    }

    private CaseBody copyBack(CaseBodyVo caseBodyVo, Long caseId){
        if (caseBodyVo == null){
            return null;
        }
        CaseBody caseBody = new CaseBody();
        BeanUtils.copyProperties(caseBodyVo, caseBody);
        List<String> list = caseBodyVo.getAppendixList();
        caseBody.setCaseId(caseId);
        caseBody.setAppendix(String.join(";", list));
        caseBody.setStatus(1);
        // 不需要处理time，这个会自动更新
        return caseBody;
    }
}
