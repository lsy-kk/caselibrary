package com.lsykk.caselibrary.dao.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;

@Document(indexName = "caseheader")
// ElasticSearch会默认创建索引
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

    private Integer state;

    private Integer visible;

    private Integer status;

    private Date createTime;

    private Date updateTime;

}
