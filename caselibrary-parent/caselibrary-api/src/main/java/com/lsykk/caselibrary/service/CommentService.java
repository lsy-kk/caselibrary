package com.lsykk.caselibrary.service;

import com.lsykk.caselibrary.dao.pojo.Comment;
import com.lsykk.caselibrary.vo.ApiResult;
import com.lsykk.caselibrary.vo.CommentVo;
import com.lsykk.caselibrary.vo.params.PageParams;

import java.util.List;

public interface CommentService {

    /**
     * 获取评论列表（可选条件），管理员
     * @param pageParams
     * @param id
     * @param authorId
     * @param caseId
     * @param status
     * @return
     */
    public ApiResult getCommentList(PageParams pageParams, Long id, Long authorId, Long caseId, Integer status);
    /**
     * 获取对应案例的评论
     * @param caseId
     * @return
     */
    List<CommentVo> getCommentVoListByCaseId(Long caseId);

    /**
     * 添加一条评论
     * @param comment
     * @return
     */
    ApiResult insertComment(Comment comment);

    /**
     * 修改评论信息
     * @param comment
     * @return
     */
    ApiResult updateComment(Comment comment);

    /**
     * 根据id获取评论
     * @param id
     * @return
     */
    Comment getCommentById(Long id);

    /**
     * 根据id获取评论vo
     * @param id
     * @return
     */
    CommentVo getCommentVoById(Long id);
}
