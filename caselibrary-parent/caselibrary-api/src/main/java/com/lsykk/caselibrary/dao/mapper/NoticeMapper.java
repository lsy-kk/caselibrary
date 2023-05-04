package com.lsykk.caselibrary.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lsykk.caselibrary.dao.pojo.Notice;

public interface NoticeMapper extends BaseMapper<Notice> {

    // 插入并获取主键
    int insertAndGetId(Notice notice);
}
