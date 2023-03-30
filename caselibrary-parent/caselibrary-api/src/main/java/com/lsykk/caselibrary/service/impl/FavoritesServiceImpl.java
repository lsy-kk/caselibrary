package com.lsykk.caselibrary.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lsykk.caselibrary.dao.mapper.FavoritesInstanceMapper;
import com.lsykk.caselibrary.dao.mapper.FavoritesMapper;
import com.lsykk.caselibrary.dao.pojo.Favorites;
import com.lsykk.caselibrary.dao.pojo.FavoritesInstance;
import com.lsykk.caselibrary.service.FavoritesService;
import com.lsykk.caselibrary.vo.ApiResult;
import com.lsykk.caselibrary.vo.ErrorCode;
import com.lsykk.caselibrary.vo.params.PageParams;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoritesServiceImpl implements FavoritesService {
    @Autowired
    private FavoritesMapper favoritesMapper;
    @Autowired
    private FavoritesInstanceMapper favoritesInstanceMapper;

    @Override
    public ApiResult getFavoritesList(PageParams pageParams, Favorites favorites) {
        /* 分页查询 favorites数据库表 */
        Page<Favorites> page = new Page<>(pageParams.getPage(), pageParams.getPageSize());
        LambdaQueryWrapper<Favorites> queryWrapper = new LambdaQueryWrapper<>();
        /* 动态SQL语句 */
        queryWrapper.eq(favorites.getId()!=null, Favorites::getId, favorites.getId());
        queryWrapper.like(StringUtils.isNotBlank(favorites.getName()), Favorites::getName, favorites.getName());
        queryWrapper.eq(favorites.getOwnerId()!=null, Favorites::getOwnerId, favorites.getOwnerId());
        /* 按照ID顺序排序 */
        queryWrapper.orderByAsc(Favorites::getId);
        Page<Favorites> favoritesPage = favoritesMapper.selectPage(page, queryWrapper);
        List<Favorites> favoritesList = favoritesPage.getRecords();
        return ApiResult.success(favoritesList);
    }

    @Override
    public List<Favorites> findFavoritesByUserId(Long id){
        LambdaQueryWrapper<Favorites> queryWrapper = new LambdaQueryWrapper<>();
        /* 动态SQL语句 */
        queryWrapper.eq(id !=null, Favorites::getOwnerId, id);
        queryWrapper.eq(Favorites::getStatus, 1);
        return favoritesMapper.selectList(queryWrapper);
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
        favoritesMapper.insert(favorites);
        return ApiResult.success();
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
    public ApiResult insertItem(FavoritesInstance favoritesInstance){
        favoritesInstanceMapper.insert(favoritesInstance);
        return ApiResult.success();
    }

    @Override
    public ApiResult updateItem(FavoritesInstance favoritesInstance){
        favoritesInstanceMapper.updateById(favoritesInstance);
        return ApiResult.success();
    }
}
