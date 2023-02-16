package com.lsykk.caselibrary.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CaseHeaderVo {

    private Long id;

    private String title;

    private String summary;

    private Long authorId;

    private Integer thumb;

    private Integer viewtimes;

    private Integer comment;

    private Integer visible;

    private String createTime;

    private String updateTime;

    // 额外的
    private CaseBodyVo caseBody;

    private String authorName;

    private List<TagVo> tags;
}
