package com.lsykk.caselibrary.controller;

import com.lsykk.caselibrary.dao.pojo.Favorites;
import com.lsykk.caselibrary.dao.pojo.FavoritesInstance;
import com.lsykk.caselibrary.service.FavoritesService;
import com.lsykk.caselibrary.vo.ApiResult;
import com.lsykk.caselibrary.vo.params.PageParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favorites")
public class FavoritesController {

    @Autowired
    private FavoritesService favoritesService;

    @GetMapping("/getFavoritesList")
    public ApiResult getFavoritesList(@RequestParam(defaultValue = "1") Integer page,
                                      @RequestParam(defaultValue = "10") Integer pageSize,
                                      @RequestParam(required = false)  Long id,
                                      @RequestParam(required = false)  Long ownerId,
                                      @RequestParam(required = false)  Integer status){
        PageParams pageParams = new PageParams(page, pageSize);
        return favoritesService.getFavoritesList(pageParams, id, ownerId, status);
    }

    @GetMapping("/getFavoritesVoId")
    public ApiResult getFavoritesVoId(@RequestParam Long id){
        return ApiResult.success(favoritesService.findFavoritesVoById(id));
    }

    @GetMapping("/getFavoritesVoByOwnerId")
    public ApiResult getFavoritesVoByOwnerId(@RequestParam Long ownerId){
        return favoritesService.findFavoritesVoByUserId(ownerId);
    }

    @GetMapping("/getFavoritesVoByCaseIdAndUserId")
    public ApiResult getFavoritesVoByCaseIdAndUserId(@RequestParam Long caseId, @RequestParam Long userId){
        return favoritesService.findFavoritesVoByCaseIdAndUserId(caseId, userId);
    }

    @GetMapping("/getUserAttitudeVo")
    public ApiResult getUserAttitudeVo(@RequestParam Long caseId, @RequestParam Long userId){
        return favoritesService.getUserAttitudeVo(caseId, userId);
    }

    @PostMapping("/insert")
    public ApiResult insert(@RequestBody Favorites favorites){
        return favoritesService.insertFavorites(favorites);
    }

    @PutMapping("/update")
    public ApiResult update(@RequestBody Favorites favorites){
        return favoritesService.updateFavorites(favorites);
    }

    @GetMapping("/changeStatus")
    public ApiResult changeStatus(@RequestParam Long favoritesId, @RequestParam Integer status){
        return favoritesService.changeFavoritesStatus(favoritesId, status);
    }

    @PostMapping("/insertItems")
    public ApiResult insertItems(@RequestBody List<FavoritesInstance> favoritesInstances){
        return favoritesService.insertItems(favoritesInstances);
    }

    @PutMapping("/deleteItems")
    public ApiResult deleteItems(@RequestBody List<FavoritesInstance> favoritesInstances){
        return favoritesService.deleteItems(favoritesInstances);
    }
}
