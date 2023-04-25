package com.lsykk.caselibrary.dao.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

@TableName("tag")
@Data
public class Tag {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String description;

    private String image;

    private Integer status;

    private Date createTime;

    private Date updateTime;
}
