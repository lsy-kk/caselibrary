package com.lsykk.caselibrary.service;

import com.lsykk.caselibrary.dao.pojo.*;
import com.lsykk.caselibrary.vo.UserDetail;
import com.lsykk.caselibrary.vo.params.CaseParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorizeService {

    @Autowired
    private FavoritesService favoritesService;

    public boolean checkUserId(Long userId) {
        if (userId== null){
            return false;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetail){
            UserDetail userDetail  = (UserDetail)authentication.getPrincipal();
            return userId.equals(userDetail.getId());
        }
        return false;
    }

    public boolean checkUser(User user) {
        if (user == null || user.getId() == null){
            return false;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetail){
            UserDetail userDetail  = (UserDetail)authentication.getPrincipal();
            return user.getId().equals(userDetail.getId());
        }
        return false;
    }
    public boolean checkCaseHeader(CaseHeader caseHeader) {

        if (caseHeader == null || caseHeader.getAuthorId() == null){
            return false;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetail){
            UserDetail userDetail  = (UserDetail)authentication.getPrincipal();
            return caseHeader.getAuthorId().equals(userDetail.getId());
        }
        return false;
    }

    public boolean checkCaseParam(CaseParam caseParam) {
        if (caseParam == null){
            return false;
        }
        return checkCaseHeader(caseParam.getCaseHeader());
    }

    public boolean checkFavoritesId(Long favoritesId) {
        if (favoritesId == null){
            return false;
        }
        Favorites favorites = favoritesService.findFavoritesById(favoritesId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetail){
            UserDetail userDetail  = (UserDetail)authentication.getPrincipal();
            return favorites.getOwnerId().equals(userDetail.getId());
        }
        return false;
    }

    public boolean checkFavorites(Favorites favorites) {
        if (favorites == null || favorites.getOwnerId() == null){
            return false;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetail){
            UserDetail userDetail  = (UserDetail)authentication.getPrincipal();
            return favorites.getOwnerId().equals(userDetail.getId());
        }
        return false;
    }

    public boolean checkFavoritesInstanceList(List<FavoritesInstance> favoritesInstanceList) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetail){
            UserDetail userDetail  = (UserDetail)authentication.getPrincipal();
            for (FavoritesInstance favoritesInstance: favoritesInstanceList){
                if (favoritesInstance == null || favoritesInstance.getUserId() == null){
                    return false;
                }
                if (!favoritesInstance.getUserId().equals(userDetail.getId())){
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public boolean checkComment(Comment comment) {
        if (comment== null || comment.getAuthorId() == null){
            return false;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetail){
            UserDetail userDetail  = (UserDetail)authentication.getPrincipal();
            return comment.getAuthorId().equals(userDetail.getId());
        }
        return false;
    }
}
