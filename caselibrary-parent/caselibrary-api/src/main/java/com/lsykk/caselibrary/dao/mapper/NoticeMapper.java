package com.lsykk.caselibrary.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsykk.caselibrary.dao.pojo.Notice;

import java.util.List;

public interface NoticeMapper extends BaseMapper<Notice> {

    // 插入并获取主键
    int insertAndGetId(Notice notice);

    // 根据用户id查找私信
    Page<Notice> findChatByUserId(Page<Notice> noticePage, Long userId);

    // 根据用户id和聊天对象id查找未读私信
    List<Notice> findUnreadChatByUserIds(Long userId, Long chatUserId);

    // 根据用户id和聊天对象id查找已读私信
    Page<Notice> findReadChatByUserIds(Page<Notice> noticePage, Long userId, Long chatUserId);
}
