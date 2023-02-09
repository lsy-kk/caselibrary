package com.lsykk.caselibrary.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsykk.caselibrary.dao.mapper.TagMapper;
import com.lsykk.caselibrary.dao.pojo.Tag;
import com.lsykk.caselibrary.service.TagService;
import com.lsykk.caselibrary.vo.ApiResult;
import com.lsykk.caselibrary.vo.ErrorCode;
import com.lsykk.caselibrary.vo.params.PageParams;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {
    @Autowired
    private TagMapper tagMapper;

    public ApiResult getTagList(PageParams pageParams, Tag tag){
        /* 分页查询 tag数据库表 */
        Page<Tag> page = new Page<>(pageParams.getPage(), pageParams.getPageSize());
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        /* 动态SQL语句 */
        queryWrapper.eq(tag.getId()!=null, Tag::getId, tag.getId());
        queryWrapper.like(StringUtils.isNotBlank(tag.getName()), Tag::getName, tag.getName());
        /* 按照ID顺序排序 */
        queryWrapper.orderByAsc(Tag::getId);
        Page<Tag> tagPage = tagMapper.selectPage(page, queryWrapper);
        List<Tag> tagList = tagPage.getRecords();
        return ApiResult.success(tagList);
    }

    public Tag findTagById(Long id){
        return tagMapper.selectById(id);
    }

    public ApiResult insertTag(Tag tag){
        if (StringUtils.isBlank(tag.getName())){
            return ApiResult.fail(ErrorCode.PARAMS_ERROR.getCode(), ErrorCode.PARAMS_ERROR.getMsg());
        }
        tag.setStatus(1);
        tagMapper.insert(tag);
        return ApiResult.success();
    }

    public ApiResult updateTag(Tag tag){
        if (tag.getId()==null || StringUtils.isBlank(tag.getName())){
            return ApiResult.fail(ErrorCode.PARAMS_ERROR.getCode(), ErrorCode.PARAMS_ERROR.getMsg());
        }
        tagMapper.updateById(tag);
        return ApiResult.success();
    }
}
