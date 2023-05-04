package com.lsykk.caselibrary.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lsykk.caselibrary.dao.mapper.NoticeMapper;
import com.lsykk.caselibrary.dao.pojo.CaseHeader;
import com.lsykk.caselibrary.dao.pojo.Comment;
import com.lsykk.caselibrary.dao.pojo.Notice;
import com.lsykk.caselibrary.dao.pojo.User;
import com.lsykk.caselibrary.service.CaseService;
import com.lsykk.caselibrary.service.NoticeService;
import com.lsykk.caselibrary.service.NoticeWebSocket;
import com.lsykk.caselibrary.service.UserService;
import com.lsykk.caselibrary.utils.DateUtils;
import com.lsykk.caselibrary.vo.ApiResult;
import com.lsykk.caselibrary.vo.NoticeVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
public class NoticeServiceImpl implements NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private CaseService caseService;

    @Override
    public boolean sendMessageByAdmin(Long fromId, Long toId, String title, String content){
        Notice notice = new Notice();
        notice.setFromId(fromId);
        notice.setToId(toId);
        notice.setTitle(title);
        notice.setContent(content);
        notice.setType(1);
        return sendMessage(notice);
    }

    @Override
    public boolean sendSystemChangeUserMessage(Long fromId, Long toId){
        String title = "您的账号信息已修改";
        String content = "请前往个人中心-设置页面查看具体修改内容";
        return sendMessageByAdmin(fromId, toId, title, content);
    }

    @Override
    public boolean sendSystemChangePasswordMessage(Long fromId, Long toId){
        String title = "您的用户密码已修改";
        String content = "若需再次修改请前往个人中心-设置页面";
        return sendMessageByAdmin(fromId, toId, title, content);
    }

    @Override
    public boolean sendMessageByComment(Comment comment){
        Notice notice = new Notice();
        notice.setFromId(comment.getAuthorId());
        User fromUser = userService.findUserById(comment.getAuthorId());
        if (comment.getToUserId() == null){
            CaseHeader caseHeader = caseService.getCaseHeaderById(comment.getCaseId());
            if (caseHeader.getAuthorId().equals(comment.getAuthorId())){
                return true;
            }
            notice.setToId(caseHeader.getAuthorId());
            notice.setTitle("对你的案例发表了评论，请前往回复");
        }
        else {
            if (comment.getToUserId().equals(comment.getAuthorId())){
                return true;
            }
            notice.setToId(comment.getToUserId());;
            notice.setTitle("回复了您的评论，请前往查看");
        }
        notice.setContent(comment.getContent());
        notice.setCaseId(comment.getCaseId());
        notice.setType(2);
        return sendMessage(notice);
    }

    @Override
    public boolean sendCommunicateMessage(Long fromId, Long toId, String content){
        if (fromId == null || toId == null || content.length() > 255){
            return false;
        }
        Notice notice = new Notice();
        notice.setFromId(fromId);
        notice.setToId(toId);
        notice.setContent(content);
        notice.setType(3);
        return sendMessage(notice);
    }

    @Override
    public boolean sendMessage(Notice notice){
        if (notice.getFromId() == null || notice.getToId() == null){
            return false;
        }
        notice.setIsRead(0);
        notice.setStatus(1);
        notice.setCreateTime(new Timestamp(System.currentTimeMillis()));
        noticeMapper.insertAndGetId(notice);
        NoticeWebSocket.sendMessageByUserId(notice.getToId().toString(), copy(notice));
        return true;
    }

    @Override
    public List<NoticeVo> findNoticeVoByUserIdAndTypeAndIsRead(Long userId, Integer type, Integer isRead){
        return copyList(findNoticeByUserIdAndTypeAndIsRead(userId, type, isRead));
    }

    private List<Notice> findNoticeByUserIdAndTypeAndIsRead(Long userId, Integer type, Integer isRead){
        LambdaQueryWrapper<Notice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Notice::getStatus, 1);
        queryWrapper.eq(Notice::getToId, userId);
        queryWrapper.eq(Notice::getType, type);
        queryWrapper.eq(Notice::getIsRead, isRead);
        // 按照id（即发布时间）倒叙排序
        queryWrapper.orderByDesc(Notice::getId);
        List<Notice> noticeList = noticeMapper.selectList(queryWrapper);
        return noticeList;
    }

    @Override
    public ApiResult updateUnRead(Long userId, Integer type){
        List<Notice> noticeList = findNoticeByUserIdAndTypeAndIsRead(userId, type, 0);
        for (Notice notice : noticeList) {
            notice.setIsRead(1);
            noticeMapper.updateById(notice);
        }
        return ApiResult.success();
    }

    private List<NoticeVo> copyList(List<Notice> noticeList){
        List<NoticeVo> noticeVoList = new ArrayList<>();
        for (Notice notice: noticeList){
            noticeVoList.add(copy(notice));
        }
        return noticeVoList;
    }

    private NoticeVo copy(Notice notice){
        NoticeVo noticeVo = new NoticeVo();
        BeanUtils.copyProperties(notice, noticeVo);
        if (notice.getCreateTime() != null){
            noticeVo.setCreateTime(DateUtils.getTime(notice.getCreateTime()));
        }
        noticeVo.setFromUser(userService.findUserVoById(notice.getFromId()));
        return noticeVo;
    }
}
