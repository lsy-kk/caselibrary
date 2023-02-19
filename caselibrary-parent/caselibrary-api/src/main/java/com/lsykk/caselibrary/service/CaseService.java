package com.lsykk.caselibrary.service;

import com.lsykk.caselibrary.dao.pojo.CaseBody;
import com.lsykk.caselibrary.dao.pojo.CaseHeader;
import com.lsykk.caselibrary.vo.ApiResult;
import com.lsykk.caselibrary.vo.params.PageParams;
import org.springframework.web.multipart.MultipartFile;

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
     * 根据收藏夹id，获取其中的案例
     * @param favoritesId
     * @return
     */
    List<CaseHeader> getCasesByFavoritesId(Long favoritesId);

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

    /**
     * 根据caseId，获取当前版本号最新的CaseBody（面向案例内容编辑的功能）
     * @param caseId
     * @return
     */
    ApiResult getCaseBodyByCaseId(Long caseId);

    /**
     * 更新casebody信息
     * @param caseBody
     * @return
     */
    ApiResult updateCaseBody(CaseBody caseBody);
}
