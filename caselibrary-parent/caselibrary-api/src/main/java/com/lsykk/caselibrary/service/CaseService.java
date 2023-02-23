package com.lsykk.caselibrary.service;

import com.lsykk.caselibrary.dao.pojo.CaseBody;
import com.lsykk.caselibrary.dao.pojo.CaseHeader;
import com.lsykk.caselibrary.vo.ApiResult;
import com.lsykk.caselibrary.vo.CaseHeaderVo;
import com.lsykk.caselibrary.vo.params.PageParams;
import com.upyun.UpException;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CaseService {

    /**
     * 分页获取查找结果(可选的多种条件查询，管理员，status不限）
     * @param pageParams
     * @param id
     * @param authorId
     * @param visible
     * @param state
     * @param status
     * @param isBody
     * @param isComment
     * @return
     */
    ApiResult getCaseHeaderVoList(PageParams pageParams, Long id, Long authorId, Integer visible,
                                  Integer state, Integer status, boolean isBody, boolean isComment);

    /**
     * 获取最近的案例
     * @param pageParams
     * @return
     */
    //ApiResult getLatestList(PageParams pageParams);

    /**
     * 获取最热的案例
     * @param pageParams
     * @return
     */
    //ApiResult getHotList(PageParams pageParams);

    /**
     * 获取其他用户的案例列表（visible=1，state=3，status=1）
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
     * @return
     */
    ApiResult getSearchList(PageParams pageParams, String keyWords);


    /**
     * 根据id获取案例头部信息（VO）
     * @param id
     * @return
     */
    CaseHeaderVo getCaseHeaderVoById(Long id, boolean isBody, boolean isComment);

    /**
     * 根据收藏夹id，获取其中的案例
     * @param favoritesId
     * @return
     */
    List<CaseHeader> getCasesByFavoritesId(Long favoritesId);

    /**
     * 根据id获取案例头部信息
     * @param id
     * @return
     */
    CaseHeader getCaseHeaderById(Long id);

    /**
     * 新增案例头
     * @param newCaseHeader
     * @return
     */
    ApiResult insertCaseHeader(CaseHeader newCaseHeader);

    /**
     * 更新案例头部信息
     * @param newCaseHeader
     * @return
     */
    ApiResult updateCaseHeader(CaseHeader newCaseHeader);

    /**
     * 根据caseId，获取当前版本号最新的CaseBody（面向案例内容编辑的功能）
     * @param caseId
     * @return
     */
    ApiResult getCaseBodyByCaseId(Long caseId);

    /**
     * 更新casebody信息(提交功能)
     * @param caseBody
     * @return
     */
    ApiResult insertCaseBody(CaseBody caseBody);

    /**
     * 更新casebody信息（保存功能）
     * @param caseBody
     * @return
     */
    ApiResult updateCaseBody(CaseBody caseBody);

    /**
     * 上传文件到服务器，返回文件路径
     * @param file
     * @return
     */
    ApiResult uploadFile(MultipartFile file);

    /**
     * 写content到md文件中，返回文件地址
     * @param content
     * @return
     */
    String exportMarkdownFile(String content);
}
