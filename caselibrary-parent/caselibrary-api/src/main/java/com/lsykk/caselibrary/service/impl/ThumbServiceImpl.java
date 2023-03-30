package com.lsykk.caselibrary.service.impl;

import com.lsykk.caselibrary.dao.mapper.ThumbMapper;
import com.lsykk.caselibrary.dao.pojo.Thumb;
import com.lsykk.caselibrary.service.ThumbService;
import com.lsykk.caselibrary.vo.ApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ThumbServiceImpl implements ThumbService {
    @Autowired
    private ThumbMapper thumbMapper;

    @Override
    public ApiResult insertThumb(Thumb thumb){
        thumbMapper.insert(thumb);
        return ApiResult.success();
    }

    @Override
    public boolean getThumbByCaseIdAndUserId(Long caseId, Long userId){
        //
        return false;
    }
}
