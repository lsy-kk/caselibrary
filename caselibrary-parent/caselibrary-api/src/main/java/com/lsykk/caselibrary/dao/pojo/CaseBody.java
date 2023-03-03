package com.lsykk.caselibrary.dao.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@TableName("case_body")
@Data
public class CaseBody {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long caseId;

    private String content;

    private String appendix;

    private Integer version;

    private Integer state;

    private Integer status;

    private Date createTime;

    private Date updateTime;
}
