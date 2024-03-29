package com.lsykk.caselibrary.vo;

import lombok.Data;

import java.util.List;

@Data
public class CaseBodyVo {

    private Long id;

    private String content;

    private List<FileVo> appendixList;

    private Integer state;

    private Integer version;

    private String createTime;

    private String updateTime;
}
