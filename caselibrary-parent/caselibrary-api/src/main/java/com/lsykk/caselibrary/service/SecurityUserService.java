package com.lsykk.caselibrary.service;

import com.lsykk.caselibrary.dao.pojo.User;
import com.lsykk.caselibrary.vo.UserDetail;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class SecurityUserService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findUserByEmail(username);
        if (user == null){
            throw new UsernameNotFoundException("用户不存在");
        }
        return copy(user);
    }

    private UserDetail copy(User user){
        UserDetail userDetail = new UserDetail();
        BeanUtils.copyProperties(user, userDetail);
        userDetail.setUsername(user.getEmail());
        userDetail.setNickname(user.getUsername());
        return userDetail;
    }

}
