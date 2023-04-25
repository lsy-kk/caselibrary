package com.lsykk.caselibrary.dao.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@TableName("case_header")
@Data
public class CaseHeader {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String summary;

    private Long authorId;

    private Integer thumb;

    private Integer viewtimes;

    private Integer comment;

    private Integer favorites;

    private Double hot;

    private Integer state;

    private Integer visible;

    private Integer status;

    private Date createTime;

    private Date updateTime;

}
