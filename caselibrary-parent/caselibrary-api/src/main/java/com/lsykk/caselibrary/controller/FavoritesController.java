package com.lsykk.caselibrary.controller;

import com.lsykk.caselibrary.dao.pojo.Favorites;
import com.lsykk.caselibrary.service.FavoritesService;
import com.lsykk.caselibrary.vo.ApiResult;
import com.lsykk.caselibrary.vo.params.PageParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/favorites")
public class FavoritesController {

    @Autowired
    private FavoritesService favoritesService;

    @GetMapping("/getList")
    public ApiResult getFavoritesList(@RequestParam(defaultValue = "1") Integer page,
                                      @RequestParam(defaultValue = "10") Integer pageSize,
                                      @RequestParam(required = false)  Long id,
                                      @RequestParam(required = false)  String name,
                                      @RequestParam(required = false)  Long ownerId){
        PageParams pageParams = new PageParams(page, pageSize);
        Favorites favorites = new Favorites();
        favorites.setId(id);
        favorites.setName(name);
        favorites.setOwnerId(ownerId);
        return favoritesService.getFavoritesList(pageParams, favorites);
    }

    @PostMapping("/insert")
    public ApiResult insert(@RequestBody Favorites favorites){
        return favoritesService.insertFavorites(favorites);
    }

    @PutMapping("/update")
    public ApiResult update(@RequestBody Favorites favorites){
        return favoritesService.updateFavorites(favorites);
    }

}
