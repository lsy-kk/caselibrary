package com.lsykk.caselibrary.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CaseHeaderVo {

    private Long id;

    private String title;

    private String summary;

    private Integer thumb;

    private Integer viewtimes;

    private Integer comment;

    private Integer favorites;

    private Integer visible;

    private String createTime;

    private String updateTime;

    // 额外的可选参数
    private CaseBodyVo caseBody;

    private List<CommentVo> comments;

    private UserVo author;

    private List<TagVo> tags;
}
