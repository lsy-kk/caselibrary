package com.lsykk.caselibrary.vo;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
@Data
public class UserDetail implements UserDetails {

    private Long id;
    // 邮箱
    private String username;

    private String image;
    // username
    private String nickname;

    private String password;

    private Integer authority;

    private Integer caseNumber;

    private Integer status;

    private Date createTime;

    private Date updateTime;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> permissions = new HashSet<GrantedAuthority>();
        if (authority == 2){
            permissions.add(new SimpleGrantedAuthority("student"));
        }
        if (authority == 1){
            permissions.add(new SimpleGrantedAuthority("student"));
            permissions.add(new SimpleGrantedAuthority("teacher"));
        }
        if (authority == 0){
            permissions.add(new SimpleGrantedAuthority("student"));
            permissions.add(new SimpleGrantedAuthority("teacher"));
            permissions.add(new SimpleGrantedAuthority("admin"));
        }
        return permissions;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
