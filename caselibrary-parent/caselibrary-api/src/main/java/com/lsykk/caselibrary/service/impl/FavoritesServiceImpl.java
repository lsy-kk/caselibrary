package com.lsykk.caselibrary.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsykk.caselibrary.dao.mapper.FavoritesInstanceMapper;
import com.lsykk.caselibrary.dao.mapper.FavoritesMapper;
import com.lsykk.caselibrary.dao.pojo.Favorites;
import com.lsykk.caselibrary.dao.pojo.FavoritesInstance;
import com.lsykk.caselibrary.service.FavoritesService;
import com.lsykk.caselibrary.service.ThumbService;
import com.lsykk.caselibrary.service.UserService;
import com.lsykk.caselibrary.utils.DateUtils;
import com.lsykk.caselibrary.vo.*;
import com.lsykk.caselibrary.vo.params.PageParams;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FavoritesServiceImpl implements FavoritesService {
    @Autowired
    private FavoritesMapper favoritesMapper;
    @Autowired
    private FavoritesInstanceMapper favoritesInstanceMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private ThumbService thumbService;

    @Override
    public ApiResult getFavoritesList(PageParams pageParams, Long id, Long ownId, Integer status){
        Page<Favorites> page = new Page<>(pageParams.getPage(), pageParams.getPageSize());
        LambdaQueryWrapper<Favorites> queryWrapper = new LambdaQueryWrapper<>();
        if (id != null){
            queryWrapper.eq(Favorites::getId, id);
        }
        else {
            queryWrapper.eq(status!=null, Favorites::getStatus, status);
            queryWrapper.eq(ownId!=null, Favorites::getOwnerId, ownId);
        }
        queryWrapper.orderByDesc(Favorites::getId);
        Page<Favorites> favoritesPage = favoritesMapper.selectPage(page, queryWrapper);
        PageVo<Favorites> pageVo = new PageVo();
        pageVo.setRecordList(favoritesPage.getRecords());
        pageVo.setTotal(favoritesPage.getTotal());
        return ApiResult.success(pageVo);
    }

    @Override
    public ApiResult findFavoritesVoByUserId(Long id){
        LambdaQueryWrapper<Favorites> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Favorites::getOwnerId, id);
        queryWrapper.eq(Favorites::getStatus, 1);
        return ApiResult.success(copyList(favoritesMapper.selectList(queryWrapper)));
    }

    @Override
    public ApiResult findFavoritesVoByCaseIdAndUserId(Long caseId, Long userId){
        LambdaQueryWrapper<Favorites> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Favorites::getOwnerId, userId);
        queryWrapper.eq(Favorites::getStatus, 1);
        List<FavoritesVo> favoritesVoList = copyList(favoritesMapper.selectList(queryWrapper));
        for (int i=0; i<favoritesVoList.size(); ++i){
            LambdaQueryWrapper<FavoritesInstance> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(FavoritesInstance::getCaseId, caseId);
            wrapper.eq(FavoritesInstance::getFavoritesId, favoritesVoList.get(i).getId());
            wrapper.eq(FavoritesInstance::getStatus, 1);
            wrapper.last("limit 1");
            FavoritesInstance favoritesInstance = favoritesInstanceMapper.selectOne(wrapper);
            favoritesVoList.get(i).setFavorites(favoritesInstance != null);
        }
        return ApiResult.success(favoritesVoList);
    }

    @Override
    public boolean getFavoritesByCaseIdAndUserId(Long caseId, Long userId){
        LambdaQueryWrapper<FavoritesInstance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FavoritesInstance::getCaseId, caseId);
        wrapper.eq(FavoritesInstance::getStatus, 1);
        wrapper.last("limit 1");
        FavoritesInstance favoritesInstance = favoritesInstanceMapper.selectOne(wrapper);
        return favoritesInstance != null;
    }

    @Override
    public ApiResult getUserAttitudeVo(Long caseId, Long userId){
        UserAttitudeVo userAttitudeVo = new UserAttitudeVo();
        userAttitudeVo.setFavorites(getFavoritesByCaseIdAndUserId(caseId, userId));
        userAttitudeVo.setThumb(thumbService.getThumbByCaseIdAndUserId(caseId, userId));
        return ApiResult.success(userAttitudeVo);
    }

    @Override
    public FavoritesVo findFavoritesVoById(Long id){
        return copy(findFavoritesById(id));
    }


    @Override
    public Favorites findFavoritesById(Long id){
        return favoritesMapper.selectById(id);
    }

    @Override
    public ApiResult insertFavorites(Favorites favorites){
        if (StringUtils.isBlank(favorites.getName()) ||
                favorites.getOwnerId() == null ||
                favorites.getVisible() == null){
            return ApiResult.fail(ErrorCode.PARAMS_ERROR.getCode(), ErrorCode.PARAMS_ERROR.getMsg());
        }
        favorites.setStatus(1);
        favoritesMapper.insertAndGetId(favorites);
        return ApiResult.success(copy(favorites));
    }

    @Override
    public ApiResult updateFavorites(Favorites favorites){
        if (favorites.getId() == null ||
                StringUtils.isBlank(favorites.getName()) ||
                favorites.getOwnerId() == null ||
                favorites.getVisible() == null){
            return ApiResult.fail(ErrorCode.PARAMS_ERROR.getCode(), ErrorCode.PARAMS_ERROR.getMsg());
        }
        favoritesMapper.updateById(favorites);
        return ApiResult.success();
    }

    @Override
    public ApiResult changeFavoritesStatus(Long favoritesId, Integer status){
        LambdaUpdateWrapper<FavoritesInstance> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(FavoritesInstance::getFavoritesId, favoritesId);
        updateWrapper.set(FavoritesInstance::getStatus, status);
        favoritesInstanceMapper.update(null, updateWrapper);
        LambdaUpdateWrapper<Favorites> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Favorites::getId, favoritesId);
        wrapper.set(Favorites::getStatus, status);
        favoritesMapper.update(null, wrapper);
        return ApiResult.success();
    }

    @Override
    public ApiResult insertItems(List<FavoritesInstance> favoritesInstances){
        for (FavoritesInstance favoritesInstance: favoritesInstances){
            favoritesInstanceMapper.insert(favoritesInstance);
        }
        return ApiResult.success();
    }

    @Override
    public ApiResult deleteItems(List<FavoritesInstance> favoritesInstances){
        for (FavoritesInstance favoritesInstance: favoritesInstances) {
            LambdaQueryWrapper<FavoritesInstance> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(FavoritesInstance::getCaseId, favoritesInstance.getCaseId());
            queryWrapper.eq(FavoritesInstance::getFavoritesId, favoritesInstance.getFavoritesId());
            favoritesInstanceMapper.delete(queryWrapper);
        }
        return ApiResult.success();
    }

    private List<FavoritesVo> copyList(List<Favorites> list){
        List<FavoritesVo> favoritesVoList = new ArrayList<>();
        for (Favorites favorites : list) {
            favoritesVoList.add(copy(favorites));
        }
        return favoritesVoList;
    }

    private FavoritesVo copy(Favorites favorites){
        FavoritesVo favoritesVo = new FavoritesVo();
        BeanUtils.copyProperties(favorites, favoritesVo);
        favoritesVo.setOwner(userService.findUserVoById(favorites.getOwnerId()));
        if (favorites.getCreateTime() != null){
            favoritesVo.setCreateTime(DateUtils.getTime(favorites.getCreateTime()));
        }
        if (favorites.getUpdateTime() != null){
            favoritesVo.setUpdateTime(DateUtils.getTime(favorites.getUpdateTime()));
        }
        return favoritesVo;
    }
}
