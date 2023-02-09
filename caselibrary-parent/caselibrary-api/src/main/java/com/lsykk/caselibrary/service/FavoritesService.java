package com.lsykk.caselibrary.service;

import com.lsykk.caselibrary.dao.pojo.Favorites;
import com.lsykk.caselibrary.vo.ApiResult;
import com.lsykk.caselibrary.vo.params.PageParams;

public interface FavoritesService {

    /**
     * 根据搜索条件，分页获取收藏夹列表
     * @param pageParams
     * @param favorites
     * @return
     */
    ApiResult getFavoritesList(PageParams pageParams, Favorites favorites);

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

}
