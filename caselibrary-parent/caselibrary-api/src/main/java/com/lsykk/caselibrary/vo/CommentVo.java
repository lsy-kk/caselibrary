package com.lsykk.caselibrary.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CommentVo {

    private Long id;

    private String content;

    private UserVo author;

    private UserVo toUser;

    private List<CommentVo> children;

    private String createTime;

    private String updateTime;
}
