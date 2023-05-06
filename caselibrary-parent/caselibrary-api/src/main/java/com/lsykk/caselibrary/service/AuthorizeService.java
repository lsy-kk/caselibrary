package com.lsykk.caselibrary.service;

import com.lsykk.caselibrary.dao.pojo.*;
import com.lsykk.caselibrary.vo.UserDetail;
import com.lsykk.caselibrary.vo.params.CaseParam;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthorizeService {

    public boolean checkUserId(Long userId) {
        if (userId== null){
            return false;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetail userDetail  = (UserDetail)authentication.getPrincipal();
        return userId.equals(userDetail.getId());
    }

    public boolean checkUser(User user) {
        if (user == null || user.getId() == null){
            return false;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetail userDetail  = (UserDetail)authentication.getPrincipal();
        return user.getId().equals(userDetail.getId());
    }
    public boolean checkCaseHeader(CaseHeader caseHeader) {

        if (caseHeader == null || caseHeader.getAuthorId() == null){
            return false;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetail userDetail  = (UserDetail)authentication.getPrincipal();
        return caseHeader.getAuthorId().equals(userDetail.getId());
    }

    public boolean checkCaseParam(CaseParam caseParam) {
        if (caseParam == null){
            return false;
        }
        return checkCaseHeader(caseParam.getCaseHeader());
    }

    public boolean checkFavorites(Favorites favorites) {
        if (favorites == null || favorites.getOwnerId() == null){
            return false;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetail userDetail  = (UserDetail)authentication.getPrincipal();
        return favorites.getOwnerId().equals(userDetail.getId());
    }

    public boolean checkFavoritesInstance(FavoritesInstance favoritesInstance) {
        if (favoritesInstance == null || favoritesInstance.getUserId() == null){
            return false;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetail userDetail  = (UserDetail)authentication.getPrincipal();
        return favoritesInstance.getUserId().equals(userDetail.getId());
    }

    public boolean checkComment(Comment comment) {
        if (comment== null || comment.getAuthorId() == null){
            return false;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetail userDetail  = (UserDetail)authentication.getPrincipal();
        return comment.getAuthorId().equals(userDetail.getId());
    }
}
