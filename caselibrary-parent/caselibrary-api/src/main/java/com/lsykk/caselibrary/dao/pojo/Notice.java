package com.lsykk.caselibrary.dao.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@TableName("notice")
@Data
public class Notice {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long fromId;

    private Long toId;

    private String title;

    private String content;

    private Integer type;

    private Long caseId;

    private Integer isRead;

    private Integer status;

    private Date createTime;

    private Date updateTime;
}
