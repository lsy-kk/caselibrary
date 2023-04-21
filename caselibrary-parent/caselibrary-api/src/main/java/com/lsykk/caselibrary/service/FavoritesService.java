package com.lsykk.caselibrary.service;

import com.lsykk.caselibrary.dao.pojo.Favorites;
import com.lsykk.caselibrary.dao.pojo.FavoritesInstance;
import com.lsykk.caselibrary.vo.ApiResult;
import com.lsykk.caselibrary.vo.FavoritesVo;
import com.lsykk.caselibrary.vo.UserAttitudeVo;
import com.lsykk.caselibrary.vo.params.PageParams;

import java.util.List;

public interface FavoritesService {

    /**
     * 获取收藏夹列表（可选条件），管理员
     * @param pageParams
     * @param id
     * @param ownId
     * @param status
     * @return
     */
    ApiResult getFavoritesList(PageParams pageParams, Long id, Long ownId, Integer status);

    /**
     * 根据用户id，获取用户创建的所有收藏夹
     * @param id
     * @return
     */
    ApiResult findFavoritesVoByUserId(Long id);

    /**
     * 根据用户id和案例id，获取用户创建的所有收藏夹，以及是否收藏有该案例
     * @param caseId
     * @param userId
     * @return
     */
    ApiResult findFavoritesVoByCaseIdAndUserId(Long caseId, Long userId);

    /**
     * 通过案例id和用户id，判断是否有收藏记录
     * @param caseId
     * @param userId
     * @return
     */
    boolean getFavoritesByCaseIdAndUserId(Long caseId, Long userId);

    /**
     * 获取特定用户对特定案例的点赞和收藏情况
     * @param caseId
     * @param userId
     * @return
     */
    ApiResult getUserAttitudeVo(Long caseId, Long userId);

    /**
     * 根据收藏夹id获取对应收藏夹vo
     * @param id
     * @return
     */
    FavoritesVo findFavoritesVoById(Long id);

    /**
     * 根据收藏夹id获取对应收藏夹信息
     * @param id
     * @return
     */
    Favorites findFavoritesById(Long id);

    /**
     * 添加收藏夹
     * @param favorites
     * @return
     */
    ApiResult insertFavorites(Favorites favorites);

    /**
     * 更新收藏夹信息
     * @param favorites
     * @return
     */
    ApiResult updateFavorites(Favorites favorites);

    /**
     * 改变收藏夹状态（status），以及收藏夹中的收藏记录的状态
     * @param favoritesId
     * @return
     */
    ApiResult changeFavoritesStatus(Long favoritesId, Integer status);
    /**
     * 向收藏夹中增加记录
     * @param favoritesInstances
     * @return
     */
    ApiResult insertItems(List<FavoritesInstance> favoritesInstances);

    /**
     * 删除收藏夹中记录
     * @param favoritesInstances
     * @return
     */
    ApiResult deleteItems(List<FavoritesInstance> favoritesInstances);
}
