package com.lsykk.caselibrary.service;

import com.lsykk.caselibrary.dao.pojo.CaseHeader;
import com.lsykk.caselibrary.dao.pojo.Comment;
import com.lsykk.caselibrary.dao.pojo.Notice;
import com.lsykk.caselibrary.vo.ApiResult;
import com.lsykk.caselibrary.vo.NoticeVo;

import java.util.List;

public interface NoticeService {


    /**
     * 管理员向特定人员发送消息
     * @param fromId
     * @param toId
     * @param title
     * @param content
     * @return
     */
    boolean sendMessageByAdmin(Long fromId, Long toId, String title, String content);

    /**
     * 更改用户信息的通知
     * @param fromId
     * @param toId
     * @return
     */
    boolean sendSystemChangeUserMessage(Long fromId, Long toId);

    /**
     * 更改用户密码的通知
     * @param fromId
     * @param toId
     * @return
     */
    boolean sendSystemChangePasswordMessage(Long fromId, Long toId);


    /**
     * 评论的提示信息
     * @param comment
     * @return
     */
    boolean sendMessageByComment(Comment comment);

    /**
     * 私信通知
     * @param fromId
     * @param toId
     * @param content
     * @return
     */
    boolean sendCommunicateMessage(Long fromId, Long toId, String content);
    /**
     * 向数据库插入一条消息
     * @param notice
     * @return
     */
    boolean sendMessage(Notice notice);

    /**
     * 根据接收者id，消息类型type，消息是否已读isRead，找对应的消息列表
     * @param userId
     * @param type
     * @param isRead
     * @return
     */
    List<NoticeVo> findNoticeVoByUserIdAndTypeAndIsRead(Long userId, Integer type, Integer isRead);

    /**
     * 更新所有未读消息为已读
     * @param userId
     * @param type
     * @return
     */
    ApiResult updateUnRead(Long userId, Integer type);
}
