package com.lsykk.caselibrary.service;

import com.lsykk.caselibrary.dao.pojo.CaseHeader;
import com.lsykk.caselibrary.vo.ApiResult;
import com.lsykk.caselibrary.vo.CaseHeaderVo;
import com.lsykk.caselibrary.vo.params.CaseParam;
import com.lsykk.caselibrary.vo.params.PageParams;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CaseService {

    /**
     * 分页获取查找结果(可选的多种条件查询），面向用户
     * @param pageParams
     * @param id
     * @param authorId
     * @param visible
     * @param state
     * @param isBody
     * @param isComment
     * @return
     */
    ApiResult getCaseHeaderVoList(PageParams pageParams, Long id, Long authorId, Integer visible,
                                  Integer state, boolean isBody, boolean isComment);

    /**
     * 分页获取查找结果，面向管理员
     * @param pageParams
     * @param id
     * @param authorId
     * @param visible
     * @param state
     * @param status
     * @return
     */
    ApiResult getCaseHeaderList(PageParams pageParams, Long id, Long authorId, Integer visible,
                                  Integer state, Integer status);

    /**
     * 获取最热的案例
     * @param pageParams
     * @return
     */
    ApiResult getHotList(PageParams pageParams);

    /**
     * 获取其他用户的案例列表（visible=1，state=1，status=1）
     * @param pageParams
     * @param userId
     * @return
     */
    ApiResult getOtherAuthorList(PageParams pageParams, Long userId, boolean isBody, boolean isComment);

    /**
     * 获取自己的案例列表（visible=0|1，state可选，status=1）
     * @param pageParams
     * @param userId
     * @param visible
     * @param state
     * @return
     */
    ApiResult getMyList(PageParams pageParams, Long userId, Integer visible, Integer state, boolean isBody, boolean isComment);

    /**
     * 案例关键字（title，summary）搜索，分页获取结果
     * @param pageParams
     * @param keyWords
     * @param type
     * @return
     */
    ApiResult getSearchList(PageParams pageParams, String keyWords, String type);

    /**
     * 同步caseHeaderVoRepository的后门接口
     * @return
     */
    ApiResult caseHeaderVoRepositoryReload();
    /**
     * 根据id获取案例头部信息（VO）
     * @param id
     * @return
     */
    CaseHeaderVo getCaseHeaderVoById(Long id, boolean isBody, boolean isComment);

    /**
     * 根据tagId，获取含有该tag的案例列表
     * @param pageParams
     * @param tagId
     * @return
     */
    ApiResult getListByTagId(PageParams pageParams, Long tagId);

    /**
     * 根据收藏夹id，分页获取其中的案例
     * @param favoritesId
     * @return
     */
    ApiResult getListByFavoritesId(PageParams pageParams, Long favoritesId);

    /**
     * 根据id获取案例param（header, body和 case_tag）
     * @param caseId
     * @return
     */
    ApiResult getCaseParamById(Long caseId);

    /**
     * 提交案例，更新案例param（header, body和 case_tag）
     * @param caseParam)
     * @return
     */
    ApiResult submitCaseParam(CaseParam caseParam);

    /**
     * 根据id获取案例头部信息
     * @param id
     * @return
     */
    CaseHeader getCaseHeaderById(Long id);

    /**
     * 管理员更新案例信息
     * @param caseHeader
     * @return
     */
    ApiResult updateCaseHeader(CaseHeader caseHeader);

}
