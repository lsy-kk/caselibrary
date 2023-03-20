package com.lsykk.caselibrary.dao.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@TableName("comment")
@Data
public class Comment {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long caseId;

    private String content;

    private Long authorId;

    private Long parentId;

    private Long toUserId;

    private Integer status;

    private Date createTime;

    private Date updateTime;

}
