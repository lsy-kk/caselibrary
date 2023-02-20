package com.lsykk.caselibrary.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lsykk.caselibrary.dao.pojo.Tag;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TagMapper extends BaseMapper<Tag> {

    // 根据案例id，获取案例带有的tag的信息
    List<Tag> findTagsByCaseId(Long caseId);
}
