package com.lsykk.caselibrary.vo;

import lombok.Data;

@Data
public class UserVo {

    private Long id;

    private String email;

    private String image;

    private String username;

    private Integer authority;
}