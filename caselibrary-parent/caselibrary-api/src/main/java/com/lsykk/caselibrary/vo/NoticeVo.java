package com.lsykk.caselibrary.vo;

import lombok.Data;

import java.util.Date;

@Data
public class NoticeVo {

    private Long id;

    private UserVo fromUser;

    private Long toId;

    private String title;

    private String content;

    private Integer type;

    private Long caseId;

    private Integer isRead;

    private String createTime;
}
