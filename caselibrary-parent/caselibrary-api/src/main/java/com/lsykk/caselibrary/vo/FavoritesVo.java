package com.lsykk.caselibrary.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FavoritesVo {

    private Long id;

    private String name;

    private String description;

    private String image;

    private UserVo owner;

    private Integer visible;

    // 可选的，收藏夹中的案例列表
    private PageVo<CaseHeaderVo> caseList;

    // 可选的，收藏夹中的案例数量
    private Integer caseNumber;

    // 可选的，针对特定案例，是否收藏该案例
    private boolean favorites;

    private String createTime;

    private String updateTime;
}
