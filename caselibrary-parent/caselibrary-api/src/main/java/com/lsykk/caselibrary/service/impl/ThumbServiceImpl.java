package com.lsykk.caselibrary.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lsykk.caselibrary.dao.mapper.ThumbMapper;
import com.lsykk.caselibrary.dao.pojo.Thumb;
import com.lsykk.caselibrary.service.ThumbService;
import com.lsykk.caselibrary.vo.ApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ThumbServiceImpl implements ThumbService {
    @Autowired
    private ThumbMapper thumbMapper;

    @Override
    public ApiResult insertThumb(Long caseId, Long userId){
        Thumb thumb = new Thumb();
        thumb.setCaseId(caseId);
        thumb.setUserId(userId);
        thumbMapper.insert(thumb);
        return ApiResult.success();
    }

    @Override
    public ApiResult deleteThumb(Long caseId, Long userId){
        LambdaQueryWrapper<Thumb> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Thumb::getCaseId, caseId);
        queryWrapper.eq(Thumb::getUserId, userId);
        thumbMapper.delete(queryWrapper);
        return ApiResult.success();
    }


    @Override
    public boolean getThumbByCaseIdAndUserId(Long caseId, Long userId){
        LambdaQueryWrapper<Thumb> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Thumb::getCaseId, caseId);
        queryWrapper.eq(Thumb::getUserId, userId);
        queryWrapper.eq(Thumb::getStatus, 1);
        Thumb thumb = thumbMapper.selectOne(queryWrapper);
        return thumb != null;
    }
}
